package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @Size(min = 2, max = 30)
    @Column(name = "username", length = 30, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 6, max = 60)
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
