package ru.foobarbaz.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.ChallengeList;
import ru.foobarbaz.entity.user.User;

import java.util.List;

public interface ChallengeListRepository extends JpaRepository<ChallengeList, Long> {
    Page<ChallengeList> findAllByNameContainsIgnoreCase(String name, Pageable request);
    List<ChallengeList> findAllByAuthorOrderByCreated(User user);
}
