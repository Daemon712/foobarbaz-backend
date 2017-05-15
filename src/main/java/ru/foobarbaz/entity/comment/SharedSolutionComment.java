package ru.foobarbaz.entity.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class SharedSolutionComment extends BaseComment {
    @NotNull
    @ManyToOne
    @JsonIgnore
    private SharedSolution sharedSolution;

    public SharedSolutionComment() {
    }

    public SharedSolutionComment(String text, SharedSolution sharedSolution) {
        super(text);
        this.sharedSolution = sharedSolution;
    }

    public SharedSolution getSharedSolution() {
        return sharedSolution;
    }

    public void setSharedSolution(SharedSolution sharedSolution) {
        this.sharedSolution = sharedSolution;
    }
}
