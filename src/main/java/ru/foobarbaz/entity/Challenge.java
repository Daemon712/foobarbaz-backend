package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "challenges")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Challenge {
    public static final int MAX_RATING = 5;
    public static final int MIN_RATING = 1;
    public static final int MAX_DIFFICULTY = 5;
    public static final int MIN_DIFFICULTY = 1;

    @Id
    @GeneratedValue
    private Long id;

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

    @Transient
    private int status;

    public Challenge() {
    }

    public Challenge(long id){
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
