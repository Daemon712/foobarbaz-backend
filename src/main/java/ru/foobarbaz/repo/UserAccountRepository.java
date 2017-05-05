package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}