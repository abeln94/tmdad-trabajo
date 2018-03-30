package es.unizar.tmdad.lab0.processors;

import java.util.Collections;
import java.util.List;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * Clase NoProcessor
 */
@Service
public class NoProcessor implements Processor {

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        return Collections.singletonList(tweet);
    }

}
