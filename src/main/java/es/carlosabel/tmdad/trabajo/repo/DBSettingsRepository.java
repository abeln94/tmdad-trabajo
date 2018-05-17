package es.carlosabel.tmdad.trabajo.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBSettingsRepository extends CrudRepository<DBSettingsTableRow, Integer> {

}
