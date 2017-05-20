package ru.foobarbaz.config;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import ru.foobarbaz.entity.HasAuthor;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;

import java.io.Serializable;

public class FoobarbazPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        User relatedUser;
        if (targetDomainObject instanceof User){
            relatedUser = (User) targetDomainObject;
        } else if (targetDomainObject instanceof UserAccount) {
            relatedUser = ((UserAccount) targetDomainObject).getUser();
        } else if (targetDomainObject instanceof HasAuthor){
            relatedUser = ((HasAuthor) targetDomainObject).getAuthor();
        } else return true;
        return authentication.getName().equals(relatedUser.getUsername());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return !(User.class.getSimpleName().equals(targetType) || UserAccount.class.getSimpleName().equals(targetType)) || authentication.getName().equals(targetId);
    }
}
