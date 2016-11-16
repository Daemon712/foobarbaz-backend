package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

}
