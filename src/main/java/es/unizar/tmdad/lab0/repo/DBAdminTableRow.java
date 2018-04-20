package es.unizar.tmdad.lab0.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "admins", schema = "public")
public class DBAdminTableRow {

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