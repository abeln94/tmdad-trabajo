package es.unizar.tmdad.lab0.service;

import es.unizar.tmdad.lab0.processors.PrependProcessor;
import es.unizar.tmdad.lab0.processors.AppendProcessor;
import es.unizar.tmdad.lab0.processors.Processor;
import es.unizar.tmdad.lab0.processors.TweetModified;
import es.unizar.tmdad.lab0.repo.TweetRepository;
import es.unizar.tmdad.lab0.repo.TweetSaved;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.SearchMetadata;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.util.MimeTypeUtils;

@Service
public class TwitterLookupService implements StreamListener{
    
    @Autowired
    private TweetRepository repo;
    @Autowired
    private SimpMessageSendingOperations smso;
    
    
    @Value("${twitter.consumerKey}")
    private String consumerKey;

    @Value("${twitter.consumerSecret}")
    private String consumerSecret;

    @Value("${twitter.accessToken}")
    private String accessToken;

    @Value("${twitter.accessTokenSecret}")
    private String accessTokenSecret;

    public SearchResults emptyAnswer() {
        return new SearchResults(Collections.emptyList(), new SearchMetadata(0, 0));
    }
        
    
    private Stream stream = null;
    private String query = "";
    private Processor processor = new AppendProcessor();
    
    private int users = 0;
    
    public void changeQuery(String newQuery) {
        query = newQuery;
        updateStream();
    }
    
    private void updateStream(){
        if(stream!=null){
            stopStream();
        }
        if(users>0){
            startStream();
        }
        
    }
    
    public void startStream(){
        if(!query.isEmpty()){
            Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
            List<StreamListener> list = new ArrayList<>();
            list.add(this);
            stream = twitter.streamingOperations().filter(query, list);
            System.out.println("Started stream with query="+query);
        }
    }
    
    public void stopStream(){
        if(stream!=null){
            stream.close();
            System.out.println("Stream closed");
        }
        stream = null;
    }

    public void subscribeUser() {
        users += 1;
        
        if(users == 1){
            updateStream();
        }
    }

    public void unSubscribeUser() {
        users -= 1;
        if(users==0){
            updateStream();
        }
    }

    public void changeProcessor(String processorname) {
        switch(processorname){
            case "prependProcessor":
                processor = new AppendProcessor();
                break;
            case "appendProcessor":
                processor = new PrependProcessor();
                break;
        }
        updateStream();
    }
    
    

    
    //----------   Stream Listener -------------//
    
    
    @Override
    public void onTweet(Tweet tweet) {
        System.out.println("Received tweet");
        
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE,MimeTypeUtils.APPLICATION_JSON);
        
        for (Tweet tweetToSend : processor.parseTweet(tweet)) {
        //**********************************************************
        	//				GUARDAR EN BD
        //**********************************************************
        	TweetSaved tweetToSave = new TweetSaved();
        	tweetToSave.setId(tweetToSend.getIdStr());
        	tweetToSave.setText(tweetToSend.getUnmodifiedText());
        	tweetToSave.setFromUser(tweetToSend.getFromUser());
        	tweetToSave.setQuery(query);
        	repo.save(tweetToSave);
       	//**********************************************************
            smso.convertAndSend("/topic/search", tweetToSend, map);
        }
        
    }

    @Override
    public void onDelete(StreamDeleteEvent deleteEvent) { }

    @Override
    public void onLimit(int numberOfLimitedTweets) {
        System.out.println("StreamListener#onLimit");
    }

    @Override
    public void onWarning(StreamWarningEvent warningEvent) {
        System.out.println("StreamListener#onWarning");
    }

    
    
}

