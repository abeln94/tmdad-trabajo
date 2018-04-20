package es.unizar.tmdad.lab0.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "query", schema = "public")
public class Query {

    private String query;
    
    @Id
    @Column(name = "name")
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String pQuery) {
        query = pQuery;
    }
}