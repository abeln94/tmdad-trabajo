package es.unizar.tmdad.lab0.repo;


import org.springframework.social.twitter.api.Tweet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends CrudRepository<TweetSaved, Integer> {

}