package ru.foobarbaz.entity.challenge.solution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedSolution extends BaseSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharedSolutionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ChallengeDetails challengeDetails;

    @Transient
    private Challenge challenge;

    @NotNull
    @ManyToOne
    private User author;

    @NotNull
    private Date created;

    @NotNull
    @Size(max = 300)
    private String comment;

    @Min(0)
    private int rating;

    public Long getSharedSolutionId() {
        return sharedSolutionId;
    }

    public void setSharedSolutionId(Long sharedSolutionId) {
        this.sharedSolutionId = sharedSolutionId;
    }

    public ChallengeDetails getChallengeDetails() {
        return challengeDetails;
    }

    public void setChallengeDetails(ChallengeDetails challengeDetails) {
        this.challengeDetails = challengeDetails;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
