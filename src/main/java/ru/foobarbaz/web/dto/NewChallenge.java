package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NewChallenge{
    @NotNull
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String template;

    @NotNull
    private String unitTest;

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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getUnitTest() {
        return unitTest;
    }

    public void setUnitTest(String unitTest) {
        this.unitTest = unitTest;
    }

}
