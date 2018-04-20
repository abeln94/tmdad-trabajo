package es.unizar.tmdad.lab0.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

@Service
public class DBAccess {

    @Autowired
    private DBTweetRepository repo;

    @Autowired
    private DBAdminRepository repoAd;

    @Autowired
    private DBQueryRepository repoQuery;

    public Set<String> findQueries() {
        Iterable<DBTweetTableRow> it = repo.findAll();
        Set<String> resultSet = new LinkedHashSet<>();
        for (DBTweetTableRow t : it) {
            resultSet.add(t.getQuery());
        }
        return resultSet;
    }

    public ArrayList<DBTweetTableRow> findByQuery(String q) {
        Iterable<DBTweetTableRow> it = repo.findAll();
        ArrayList<DBTweetTableRow> resultSet = new ArrayList<>();
        for (DBTweetTableRow t : it) {
            String query = t.getQuery().replace(" ", "");
            if (query.equals(q)) {
                resultSet.add(t);
            }
        }

        return resultSet;
    }

    public boolean isAdmin(String user) {
        for (DBAdminTableRow a : repoAd.findAll()) {
            if (user.equals(a.getId())) {
                return true;
            }
        }

        System.out.println("Tu id de fb: " + user);

        return false;
    }

    public void saveTweet(Tweet tweet, String query) {
        DBTweetTableRow tweetToSave = new DBTweetTableRow();
        tweetToSave.setId(tweet.getIdStr());
        tweetToSave.setText(tweet.getUnmodifiedText());
        tweetToSave.setFromUser(tweet.getFromUser());
        tweetToSave.setQuery(query);
        repo.save(tweetToSave);
    }

    public String loadQuery(){
        for(DBQueryTableRow query : repoQuery.findAll()){
            return query.getQuery();
        }
        return "";
    }
    
    public void saveQuery(String query){
        repoQuery.deleteAll();
        DBQueryTableRow data = new DBQueryTableRow();
        data.setQuery(query);
        repoQuery.save(data);
    }
    
}
