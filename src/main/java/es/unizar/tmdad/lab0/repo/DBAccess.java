package es.unizar.tmdad.lab0.repo;

import java.util.ArrayList;
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

    /**
     * Tweets repository
     */
    @Autowired
    private DBTweetRepository repoTweets;

    /**
     * Admin repository
     */
    @Autowired
    private DBAdminRepository repoAdmin;

    /**
     * Settings repository
     */
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
        repoTweets.save(tweetToSave);
    }

    //------------------admin repository------------------
    public boolean isAdmin(String user) {
        for (DBAdminTableRow a : repoAdmin.findAll()) {
            if (user.equals(a.getId())) {
                return true;
            }
        }

        System.out.println("Tu id de fb: " + user);

        return false;
    }

    //------------------settings repository------------------
    public String getSetting(String key) {
        for (DBSettingsTableRow settings : repoSettings.findAll()) {
            if (settings.getKey().equals(key)) {
                return settings.getValue();
            }
        }
        return null;
    }

    public void setSetting(String key, String value) {
        DBSettingsTableRow row = new DBSettingsTableRow();
        row.setKey(key);
        row.setValue(value);
        repoSettings.save(row);
    }

}
