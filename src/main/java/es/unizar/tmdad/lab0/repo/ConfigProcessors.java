package es.unizar.tmdad.lab0.repo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "searchconfiguration", schema = "public")
public class ConfigProcessors {

    String query;
    String processor;
    String level;
    
    @Id
    @Column(name = "query")
    public String getQuery() {
        return query;
    }

    @Column(name = "processor")
    public String getProcessor() {
        return processor;
    }

    @Column(name = "level")
    public String getLevel() {
        return level;
    }
    
    public void setQuery(String pQuery) {
        query = pQuery;
    }
    
    public void setProcessor(String pProcessor) {
        processor = pProcessor;
    }
    
    public void setLevel(String pLevel) {
        level = pLevel;
    }
}