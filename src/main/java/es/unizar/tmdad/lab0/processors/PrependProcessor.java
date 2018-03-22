
package es.unizar.tmdad.lab0.processors;

import java.util.ArrayList;
import java.util.List;
import org.springframework.social.twitter.api.Tweet;

public class PrependProcessor implements Processor {

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        List<Tweet> tweets = new ArrayList<>(1);
        tweets.add(new TweetModified(tweet, tweet.getText()+"\nIt works!!!!!!"));
        return tweets;
    }

}
