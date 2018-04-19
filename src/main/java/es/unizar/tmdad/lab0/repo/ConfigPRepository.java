package es.unizar.tmdad.lab0.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigPRepository extends CrudRepository<ConfigProcessors, Integer> {

}
