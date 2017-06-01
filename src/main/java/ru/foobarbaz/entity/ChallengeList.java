package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class ChallengeList implements HasAuthor, AbleToLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long challengeListId;

    @NotNull
    @Size(min = 4, max = 50)
    private String name;

    @NotNull
    @Size(min = 50, max = 300)
    private String description;

    @NotNull
    @ManyToOne
    private User author;

    @NotNull
    private Date created;

    @ManyToMany
    @OrderColumn
    @NotNull
    @Size(min = 3, max = 50)
    private List<Challenge> challenges;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> likes;

    public Long getChallengeListId() {
        return challengeListId;
    }

    public void setChallengeListId(Long challengeListId) {
        this.challengeListId = challengeListId;
    }

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

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }
}
