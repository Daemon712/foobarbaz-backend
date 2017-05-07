package ru.foobarbaz.logic;

import ru.foobarbaz.entity.user.UserAccount;

public interface UserService {
    UserAccount createUser(UserAccount userAccount);
}
