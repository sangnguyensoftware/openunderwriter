package com.ail.core.key;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

@Audited
@Entity
@NamedQuery(name = "get.uniquekey.by.id", query = "select key from UniqueKey key where key.id = ?")
public class UniqueKey {
    @Id
    private String Id;
    private Long value;

    UniqueKey() {
    }

    public UniqueKey(String keyIdArg, Long value) {
        this.Id = keyIdArg;
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long nextValue() {
        return value++;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
