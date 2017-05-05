package ru.foobarbaz.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Page<UserAccount> findAllByUsernameContainsIgnoreCase(String username, Pageable request);
}