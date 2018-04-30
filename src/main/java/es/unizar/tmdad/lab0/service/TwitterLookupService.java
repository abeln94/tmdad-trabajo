package es.unizar.tmdad.lab0.service;

import es.unizar.tmdad.lab0.rabbitmq.RabbitMQEndpoint;
import es.unizar.tmdad.lab0.repo.DBAccess;
import es.unizar.tmdad.lab0.settings.Preferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.social.twitter.api.SearchMetadata;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class TwitterLookupService implements StreamListener {

    @Autowired
    private SimpMessageSendingOperations smso;

    @Autowired
    private RabbitMQEndpoint rabbitMQ;
    
    @Autowired
    private Preferences pref;
    
    @Autowired
    private DBAccess twac;

    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.accessToken}")
    private String accessToken;

    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;


    private Stream stream = null;
    private Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private void updateStream() {
        if (stream != null) {
            stopStream();
        }
        if (!users.isEmpty()) {
            startStream();
        }

    }

    public void startStream() {
        if (pref.getQuery()!=null && !pref.getQuery().isEmpty()) {
            Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            List<StreamListener> list = new ArrayList<>();
            list.add(this);
            stream = twitter.streamingOperations().filter(pref.getQuery(), list);
            System.out.println("Started stream with query=" + pref.getQuery());
        }
    }

    public void stopStream() {
        if (stream != null) {
            try{
                stream.close();
            }catch(IndexOutOfBoundsException e){
                System.out.println("Ignoring error when closing stream");
            }
            System.out.println("Stream closed");
        }
        stream = null;
    }

    public void subscribeUser(String sessionId) {
        users.add(sessionId);

        if (users.size() == 1) {
            updateStream();
        }
    }

    public void unSubscribeUser(String sessionId) {
        users.remove(sessionId);
        if (users.isEmpty()) {
            updateStream();
        }
    }

    
    public void changeQuery(String query) {
        pref.setQuery(query);
        System.out.println("New query: " + query);

        updateStream();
    }
    

    // ---------- Stream Listener -------------//
    @Override
    public void onTweet(Tweet tweet) {
        System.out.println("Received tweet");
        // GUARDAR EN BD
        twac.saveTweet(tweet, pref.getQuery());
        rabbitMQ.sendTweet(tweet);
    }

    public void onProcessedTweet(Tweet tweet) {
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        smso.convertAndSend("/topic/search", tweet, map);
    }

    @Override
    public void onDelete(StreamDeleteEvent deleteEvent) {
    }

    @Override
    public void onLimit(int numberOfLimitedTweets) {
        System.out.println("StreamListener#onLimit");
    }

    @Override
    public void onWarning(StreamWarningEvent warningEvent) {
        System.out.println("StreamListener#onWarning");
    }

}
