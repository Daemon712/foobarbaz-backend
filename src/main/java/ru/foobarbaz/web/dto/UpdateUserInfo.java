package ru.foobarbaz.web.dto;

import javax.validation.constraints.Size;

public class UpdateUserInfo {
    @Size(max = 50)
    private String name;

    @Size(max = 200)
    private String description;

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
