package es.unizar.tmdad.lab0.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "processorconf", schema = "public")
public class DBSettingsTableRow {

    private String name;
    private String level;

    @Id
    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "level")
    public String getLevel() {
        return level;
    }

    public void setName(String pName) {
        name = pName;
    }

    public void setLevel(String pLevel) {
        level = pLevel;
    }
}
