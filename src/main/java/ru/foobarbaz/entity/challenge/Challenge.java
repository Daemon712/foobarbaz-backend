package ru.foobarbaz.entity.challenge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.HasAuthor;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.web.view.ChallengeView;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

@Entity
public class Challenge implements HasAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long challengeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonView(ChallengeView.Full.class)
    private ChallengeDetails details;

    @NotNull
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    @JsonView(ChallengeView.Description.class)
    private String shortDescription;

    @NotNull
    @ManyToOne
    @JsonView(ChallengeView.Short.class)
    private User author;

    @NotNull
    @JsonView(ChallengeView.Short.class)
    private Date created;

    @Min(MIN_RATING)
    @Max(MAX_RATING)
    @JsonView(ChallengeView.Short.class)
    private int rating;

    @Min(MIN_DIFFICULTY)
    @Max(MAX_DIFFICULTY)
    @JsonView(ChallengeView.Short.class)
    private int difficulty;

    @ElementCollection
    @CollectionTable
    @Size(max = 5)
    @JsonView(ChallengeView.Short.class)
    private Set<String> tags;

    @Transient
    @JsonView(ChallengeView.Status.class)
    private ChallengeStatus status;

    public Challenge() {
    }

    public Challenge(Long challengeId){
        this.challengeId = challengeId;
    }

    public Challenge(Challenge challenge, ChallengeUserStatus status) {
        BeanUtils.copyProperties(challenge, this);
        this.status = status != null ? status.getStatus() : ChallengeStatus.NOT_STARTED;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public ChallengeDetails getDetails() {
        return details;
    }

    public void setDetails(ChallengeDetails details) {
        this.details = details;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }
}
