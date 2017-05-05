package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NewUser {
    @NotNull
    @Pattern(regexp = "^[\\w\\d-._]*$")
    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @Size(min = 6, max = 50)
    private String password;

    @Size(min = 2, max = 30)
    private String name;

    private String description;

    public NewUser(){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
