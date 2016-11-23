package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NewUser {
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @NotNull
    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @Size(min = 6, max = 50)
    private String password;

    public NewUser(){
    }

    public NewUser(String username) {
        this.username = username;
    }

    public NewUser(String username, String password) {
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
