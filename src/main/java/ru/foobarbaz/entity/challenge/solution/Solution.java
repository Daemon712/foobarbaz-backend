package ru.foobarbaz.entity.challenge.solution;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;

import javax.persistence.*;

@Entity
public class Solution extends BaseSolution {
    @EmbeddedId
    @JsonProperty("solutionNum")
    private SolutionPK pk;

    @JoinColumns({
            @JoinColumn(name="username", referencedColumnName="username", insertable = false, updatable = false),
            @JoinColumn(name="challengeId", referencedColumnName="challengeId", insertable = false, updatable = false)
    })
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private ChallengeUserDetails holder;

    public Solution() {
    }

    public Solution(SolutionPK pk) {
        this.pk = pk;
    }

    public Solution(String username, Long challengeId, Integer solutionNum) {
        this.pk = new SolutionPK(username, challengeId, solutionNum);
    }

    public SolutionPK getPk() {
        return pk;
    }

    public void setPk(SolutionPK pk) {
        this.pk = pk;
    }

    public ChallengeUserDetails getHolder() {
        return holder;
    }

    public void setHolder(ChallengeUserDetails holder) {
        this.holder = holder;
    }

    @PreRemove
    private void remove() {
        getHolder().getSolutions().remove(this);
    }
}
