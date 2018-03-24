package es.unizar.tmdad.lab0.processors;

import java.util.Date;
import org.springframework.social.twitter.api.Tweet;

/**
 * Clase TweetModified
 */
public class TweetModified extends Tweet {

    public TweetModified(Tweet tweet, String newText){
        super(tweet.getId(), tweet.getIdStr(), newText, tweet.getCreatedAt(), tweet.getFromUser(), tweet.getProfileImageUrl(), tweet.getToUserId(), tweet.getFromUserId(), tweet.getLanguageCode(), tweet.getSource());
    }
    
}