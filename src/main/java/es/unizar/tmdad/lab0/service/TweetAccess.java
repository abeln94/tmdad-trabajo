package es.unizar.tmdad.lab0.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import es.unizar.tmdad.lab0.repo.TweetRepository;
import es.unizar.tmdad.lab0.repo.TweetSaved;

@Service
public class TweetAccess {

    @Autowired
    private TweetRepository repo;

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

}
