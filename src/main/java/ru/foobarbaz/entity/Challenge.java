package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

@Entity
@Table(name = "challenges")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Challenge {
    @Id
    @GeneratedValue
    @JsonProperty("id")
    private Long challengeId;

    @OneToOne(fetch = FetchType.LAZY)
    private ChallengeDetails details;

    @NotNull
    @Pattern(regexp = "^[\\wа-яА-Я\\d -]*$")
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String shortDescription;

    @NotNull
    @ManyToOne
    private User author;

    @NotNull
    private Date created;

    @Min(MIN_RATING)
    @Max(MAX_RATING)
    private int rating;

    @Min(MIN_DIFFICULTY)
    @Max(MAX_DIFFICULTY)
    private int difficulty;

    @ElementCollection
    @CollectionTable
    @Size(max = 5)
    private Set<String> tags;

    @Transient
    private int status;

    public Challenge() {
    }

    public Challenge(Long challengeId){
        this.challengeId = challengeId;
    }

    public Challenge(Challenge challenge, ChallengeStatus status) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
