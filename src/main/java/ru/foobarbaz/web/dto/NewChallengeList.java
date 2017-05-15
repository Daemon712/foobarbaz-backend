package ru.foobarbaz.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class NewChallengeList {

    @NotNull
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String description;

    @NotNull
    @Size(min = 3, max = 50)
    private List<Long> challenges;

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

    public List<Long> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Long> challenges) {
        this.challenges = challenges;
    }
}
