package es.unizar.tmdad.lab0.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

@Service
public class DBAccess {

    //tweets repository
    @Autowired
    private DBTweetRepository repoTweets;

    public Set<String> findQueries() {
        Iterable<DBTweetTableRow> it = repoTweets.findAll();
        Set<String> resultSet = new LinkedHashSet<>();
        for (DBTweetTableRow t : it) {
            resultSet.add(t.getQuery());
        }
        return resultSet;
    }

    public ArrayList<DBTweetTableRow> findByQuery(String q) {
        Iterable<DBTweetTableRow> it = repoTweets.findAll();
        ArrayList<DBTweetTableRow> resultSet = new ArrayList<>();
        for (DBTweetTableRow t : it) {
            String query = t.getQuery().replace(" ", "");
            if (query.equals(q)) {
                resultSet.add(t);
            }
        }

        return resultSet;
    }

    public void saveTweet(Tweet tweet, String query) {
        DBTweetTableRow tweetToSave = new DBTweetTableRow();
        tweetToSave.setId(tweet.getIdStr());
        tweetToSave.setText(tweet.getUnmodifiedText());
        tweetToSave.setFromUser(tweet.getFromUser());
        tweetToSave.setQuery(query);
        repoTweets.save(tweetToSave);
    }

    //admin repository
    @Autowired
    private DBAdminRepository repoAd;

    public boolean isAdmin(String user) {
        for (DBAdminTableRow a : repoAd.findAll()) {
            if (user.equals(a.getId())) {
                return true;
            }
        }

        System.out.println("Tu id de fb: " + user);

        return false;
    }

    //settings repository
    @Autowired
    private DBSettingsRepository repoSettings;

    public String getSettings(String key) {
        for (DBSettingsTableRow settings : repoSettings.findAll()) {
            if (settings.getName().equals(key)){
                return settings.getLevel();
            }
        }
        return null;
    }

    public void setSettings(String key, String value) {
        DBSettingsTableRow row = new DBSettingsTableRow();
        row.setName(key);
        row.setLevel(value);
        repoSettings.save(row);
    }

}
