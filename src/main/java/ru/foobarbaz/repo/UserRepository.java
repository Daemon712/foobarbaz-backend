package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.user.User;

public interface UserRepository extends CrudRepository<User, String> {

}
