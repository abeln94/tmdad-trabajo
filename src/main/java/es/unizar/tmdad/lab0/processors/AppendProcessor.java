package es.unizar.tmdad.lab0.processors;

import java.util.ArrayList;
import java.util.List;
import org.springframework.social.twitter.api.Tweet;


public class AppendProcessor implements Processor {

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        List<Tweet> tweets = new ArrayList<>(1);
        tweets.add(new TweetModified(tweet, "It works!!!!!!\n"+tweet.getText()));
        return tweets;
    }

}
