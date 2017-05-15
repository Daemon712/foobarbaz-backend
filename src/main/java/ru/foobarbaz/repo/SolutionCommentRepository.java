package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.comment.SharedSolutionComment;

import java.util.List;

public interface SolutionCommentRepository extends JpaRepository<SharedSolutionComment, Long> {
    List<SharedSolutionComment> findAllBySharedSolutionOrderByCreated(SharedSolution solution);
}
