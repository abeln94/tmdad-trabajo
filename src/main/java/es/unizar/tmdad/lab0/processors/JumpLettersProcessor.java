package es.unizar.tmdad.lab0.processors;

import es.unizar.tmdad.lab0.settings.Preferences;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * Clase JumpLettersProcessor
 */
@Service
public class JumpLettersProcessor implements Processor {

    @Autowired
    private Preferences preferences;

    private final Random random = new Random();

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        String originalText = tweet.getText();

        String newText = Pattern.compile("\\b").splitAsStream(originalText)
                .map(t -> dislexionate(t))
                .collect(Collectors.joining());

        return Collections.singletonList(new TweetModified(tweet, newText));
    }

    private String dislexionate(String t) {
        if (t.length() <= 2) {
            return t;
        }

        char[] chars = t.toCharArray();

        int repeat;

        switch (preferences.getProcessorLevel()) {
            case LOW:
                repeat = 1;
                break;
            case MEDIUM:
                repeat = chars.length / 2;
                break;
            case HIGH:
                repeat = chars.length;
                break;
            case NONE:
            default:
                repeat = 0;
        }

        for (int i = 0; i < repeat; i++) {
            int a = 1 + random.nextInt(chars.length - 2);
            int b = 1 + random.nextInt(chars.length - 2);

            char temp = chars[a];
            chars[a] = chars[b];
            chars[b] = temp;
        }

        return new String(chars);

    }

}
