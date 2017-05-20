package ru.foobarbaz.logic;

import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;

public interface UserService {
    UserAccount createUser(UserAccount userAccount);
    UserAccount modifyUserInfo(UserAccount template);
    User modifyUserPassword(User template);
}
