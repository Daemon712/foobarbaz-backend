package ru.foobarbaz.entity.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.entity.challenge.Challenge;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class ChallengeComment extends BaseComment {
    @NotNull
    @ManyToOne
    @JsonIgnore
    private Challenge challenge;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
