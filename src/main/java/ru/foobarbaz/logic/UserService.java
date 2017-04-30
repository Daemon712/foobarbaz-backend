package ru.foobarbaz.logic;

import ru.foobarbaz.entity.UserAccount;

public interface UserService {
    UserAccount createUser(UserAccount userAccount);
}
