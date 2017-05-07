package ru.foobarbaz.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.user.UserAccount;

import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Page<UserAccount> findAllByUsernameContainsIgnoreCaseOrUserNameContainsIgnoreCase(String username, String name, Pageable request);
    List<UserAccount> findTop3By(Sort sort);
}