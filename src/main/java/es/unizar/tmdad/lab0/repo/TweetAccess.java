package es.unizar.tmdad.lab0.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

@Service
public class TweetAccess {

    @Autowired
    private TweetRepository repo;

    @Autowired
    private AdminRepository repoAd;

    @Autowired
    private QueryRepository repoQuery;

    public Set<String> findQueries() {
        Iterable<TweetSaved> it = repo.findAll();
        Set<String> resultSet = new LinkedHashSet<>();
        for (TweetSaved t : it) {
            resultSet.add(t.getQuery());
        }
        return resultSet;
    }

    public ArrayList<TweetSaved> findByQuery(String q) {
        Iterable<TweetSaved> it = repo.findAll();
        ArrayList<TweetSaved> resultSet = new ArrayList<>();
        for (TweetSaved t : it) {
            String query = t.getQuery().replace(" ", "");
            if (query.equals(q)) {
                resultSet.add(t);
            }
        }

        return resultSet;
    }

    public boolean isAdmin(String user) {
        for (Admin a : repoAd.findAll()) {
            if (user.equals(a.getId())) {
                return true;
            }
        }

        System.out.println("Tu id de fb: " + user);

        return false;
    }

    public void saveTweet(Tweet tweet, String query) {
        TweetSaved tweetToSave = new TweetSaved();
        tweetToSave.setId(tweet.getIdStr());
        tweetToSave.setText(tweet.getUnmodifiedText());
        tweetToSave.setFromUser(tweet.getFromUser());
        tweetToSave.setQuery(query);
        repo.save(tweetToSave);
    }

    public String getQuery(){
        for(Query query : repoQuery.findAll()){
            return query.getQuery();
        }
        return "";
    }
    
    public void setQuery(String query){
        repoQuery.deleteAll();
        Query data = new Query();
        data.setQuery(query);
        repoQuery.save(data);
    }
    
}
