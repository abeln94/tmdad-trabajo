package es.carlosabel.tmdad.trabajo.endpoints;

import es.carlosabel.tmdad.trabajo.settings.Preferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

/**
 * Interact with Twitter
 * - Configure stream
 * - Receive raw tweets from twitter
 */
@Service
@RefreshScope
public class TwitterEndpoint implements StreamListener {

    /**
     * After receiving raw tweet, send to rabbitmq to process
     */
    @Autowired
    private RabbitMQEndpoint rabbitMQ;

    /**
     * Get query
     */
    @Autowired
    private Preferences prefs;

    //------------------keys and tokens------------------
    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.accessToken}")
    private String accessToken;

    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;

    //------------------stream------------------
    private Stream stream = null;
    private final Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private void updateStream() {
        if (stream != null) {
            stopStream();
        }
        if (!users.isEmpty()) {
            startStream();
        }

    }

    public void startStream() {
        if (prefs.getQuery() != null && !prefs.getQuery().isEmpty()) {
            Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            List<StreamListener> list = new ArrayList<>();
            list.add(this);
            stream = twitter.streamingOperations().filter(prefs.getQuery(), list);
            System.out.println("Started stream with query=" + prefs.getQuery());
        }
    }

    public void stopStream() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IndexOutOfBoundsException e) {
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
        prefs.setQuery(query);
        System.out.println("New query: " + query);

        updateStream();
    }

    // ---------- Stream Listener -------------//
    @Override
    public void onTweet(Tweet tweet) {
        System.out.println("Received tweet from Twitter");
        rabbitMQ.sendTweet(tweet);
    }

    @Override
    public void onDelete(StreamDeleteEvent deleteEvent) {
        System.out.println("StreamListener#onDelete");
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
