package ru.foobarbaz.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    @NotNull
    private Date registrationDate;

    @Size(max = 200)
    private String description;

    public UserAccount() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}