package es.unizar.tmdad.lab0.repo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "admins", schema = "public")
public class Admin {

    String id;
    
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }
    
    public void setId(String pId) {
        id = pId;
    }
}