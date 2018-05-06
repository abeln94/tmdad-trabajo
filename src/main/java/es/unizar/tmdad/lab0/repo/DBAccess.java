package es.unizar.tmdad.lab0.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * Acces to database
 * - Retrieves data
 * - Saves data
 */
@Service
public class DBAccess {

    @Autowired
    private DBTweetRepository repoTweets;

    @Autowired
    private DBAdminRepository repoAd;

    @Autowired
    private DBSettingsRepository repoSettings;

    //------------------tweets repository------------------
    public ArrayList<DBTweetTableRow> getSavedTweets() {
        Iterable<DBTweetTableRow> it = repoTweets.findAll();
        ArrayList<DBTweetTableRow> resultSet = new ArrayList<>();
        for (DBTweetTableRow t : it) {
            resultSet.add(t);
        }

        return resultSet;
    }

    public void saveTweet(Tweet tweet) {
        DBTweetTableRow tweetToSave = new DBTweetTableRow();
        tweetToSave.setId(tweet.getIdStr());
        tweetToSave.setText(tweet.getUnmodifiedText());
        tweetToSave.setFromUser(tweet.getFromUser());
        tweetToSave.setQuery("");
        repoTweets.save(tweetToSave);
    }

    //------------------admin repository------------------
    public boolean isAdmin(String user) {
        for (DBAdminTableRow a : repoAd.findAll()) {
            if (user.equals(a.getId())) {
                return true;
            }
        }

        System.out.println("Tu id de fb: " + user);

        return false;
    }

    //------------------settings repository------------------
    public String getSettings(String key) {
        for (DBSettingsTableRow settings : repoSettings.findAll()) {
            if (settings.getKey().equals(key)) {
                return settings.getValue();
            }
        }
        return null;
    }

    public void setSettings(String key, String value) {
        DBSettingsTableRow row = new DBSettingsTableRow();
        row.setKey(key);
        row.setValue(value);
        repoSettings.save(row);
    }

}
