
package es.unizar.tmdad.lab0.processors;

import java.util.List;
import org.springframework.social.twitter.api.Tweet;


public interface Processor {
    
    public enum level {NONE, LOW, MEDIUM, HIGH,};
    
    public List<Tweet> parseTweet(Tweet tweet);
}
