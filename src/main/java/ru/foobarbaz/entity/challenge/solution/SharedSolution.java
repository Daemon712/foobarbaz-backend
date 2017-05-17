package ru.foobarbaz.entity.challenge.solution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import ru.foobarbaz.entity.AbleToLikes;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.web.view.SharedSolutionView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedSolution extends BaseSolution implements AbleToLikes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long sharedSolutionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ChallengeDetails challengeDetails;

    @Transient
    @JsonView(SharedSolutionView.ChallengeInfo.class)
    private Challenge challenge;

    @NotNull
    @ManyToOne
    @JsonView(SharedSolutionView.Author.class)
    private User author;

    @NotNull
    private Date created;

    @NotNull
    @Size(max = 300)
    private String comment;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> likes;

    public SharedSolution() {
    }

    public SharedSolution(Long sharedSolutionId) {
        this.sharedSolutionId = sharedSolutionId;
    }

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

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }
}
