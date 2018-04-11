package es.unizar.tmdad.lab0.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import es.unizar.tmdad.lab0.repo.Admin;
import es.unizar.tmdad.lab0.repo.AdminRepository;
import es.unizar.tmdad.lab0.repo.TweetRepository;
import es.unizar.tmdad.lab0.repo.TweetSaved;

@Service
public class TweetAccess {

    @Autowired
    private TweetRepository repo;
    
    @Autowired
    private AdminRepository repoAd;

    public Set<String> findQueries() {
        Iterable<TweetSaved> it = repo.findAll();
        Set<String> resultSet = new LinkedHashSet<>();
        for (TweetSaved t : it) {
            resultSet.add(t.getQuery());
        }
        return resultSet;
    }
    
    public ArrayList<String> findAdmins() {
        Iterable<Admin> it = repoAd.findAll();
        ArrayList<String> resultSet = new ArrayList<>();
        for (Admin a : it) {
            	resultSet.add(a.getId());
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
