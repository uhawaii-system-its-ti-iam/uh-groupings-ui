package edu.hawaii.its.api.type;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Proxy(lazy = false)
public class Person {
    @Id
    @Column
    private String username;

    @Column(name = "FullName")
    private String name;

    @Column
    private String uuid;

    public Person() {
        //empty
    }

    // Constructor.
    public Person(String name, String uuid, String username) {
        this.name = name;
        this.uuid = uuid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", uuid=" + uuid + ", username=" + username + "]";
    }
}
