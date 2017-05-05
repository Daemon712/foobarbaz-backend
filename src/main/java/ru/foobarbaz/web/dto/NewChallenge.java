package ru.foobarbaz.web.dto;

import ru.foobarbaz.entity.Challenge;

import javax.validation.constraints.*;
import java.util.Set;

public class NewChallenge {
    @NotNull
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String shortDescription;

    @NotNull
    @Size(min = 100, max = 2000)
    private String fullDescription;

    @Min(Challenge.MIN_DIFFICULTY)
    @Max(Challenge.MAX_DIFFICULTY)
    private int difficulty;

    @NotNull
    private String template;

    @NotNull
    private String unitTest;

    @NotNull
    private String sample;

    private Set<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
