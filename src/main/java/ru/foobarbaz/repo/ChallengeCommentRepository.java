package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.comment.ChallengeComment;

import java.util.List;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeComment, Long> {
    List<ChallengeComment> findAllByChallengeOrderByCreated(Challenge challenge);
}
