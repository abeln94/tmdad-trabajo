package es.carlosabel.tmdad.trabajo.repo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration", schema = "public")
public class DBSettingsTableRow {

    private String key;
    private String value;

    @Id
    @Column(name = "key")
    public String getKey() {
        return key;
    }

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setKey(String pKey) {
        key = pKey;
    }

    public void setValue(String pValue) {
        value = pValue;
    }
}
