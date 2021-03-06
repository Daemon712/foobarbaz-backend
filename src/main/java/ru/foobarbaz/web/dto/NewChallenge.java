package ru.foobarbaz.web.dto;

import ru.foobarbaz.constant.AccessOption;

import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.util.Set;

import static ru.foobarbaz.constant.ChallengeRatingConst.MAX_DIFFICULTY;
import static ru.foobarbaz.constant.ChallengeRatingConst.MIN_DIFFICULTY;

public class NewChallenge {
    @NotNull
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String shortDescription;

    @NotNull
    @Size(min = 100, max = 5000)
    private String fullDescription;

    @Min(MIN_DIFFICULTY)
    @Max(MAX_DIFFICULTY)
    private int difficulty;

    @NotNull
    @Size(max = 5000)
    private String template;

    @NotNull
    @Size(max = 10000)
    private String unitTest;

    @NotNull
    @Size(max = 5000)
    private String sample;

    @NotNull
    @Enumerated
    private AccessOption commentAccess;

    @NotNull
    @Enumerated
    private AccessOption shareAccess;

    @Size(max = 5)
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

    public AccessOption getCommentAccess() {
        return commentAccess;
    }

    public void setCommentAccess(AccessOption commentAccess) {
        this.commentAccess = commentAccess;
    }

    public AccessOption getShareAccess() {
        return shareAccess;
    }

    public void setShareAccess(AccessOption shareAccess) {
        this.shareAccess = shareAccess;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
