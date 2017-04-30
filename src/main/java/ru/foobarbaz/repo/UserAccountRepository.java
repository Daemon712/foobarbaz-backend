package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.UserAccount;

public interface UserAccountRepository extends CrudRepository<UserAccount, String> {
}