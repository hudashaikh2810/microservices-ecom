package com.example.auth_service.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rolename;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return rolename;
    }

    public void setName(String rolename) {
        this.rolename = rolename;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", rolename='" + rolename + '\'' +
                ", users=" + users +
                '}';
    }
}
