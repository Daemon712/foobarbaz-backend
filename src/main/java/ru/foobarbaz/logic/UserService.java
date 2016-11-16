package ru.foobarbaz.logic;

import ru.foobarbaz.entity.User;

public interface UserService {
    User createUser(String username, String password);
}
