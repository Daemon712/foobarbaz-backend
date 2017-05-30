package ru.foobarbaz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import ru.foobarbaz.entity.HasAuthor;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.entity.user.UserRole;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.UserRepository;

import java.io.Serializable;
import java.util.Optional;

public class FoobarbazPermissionEvaluator implements PermissionEvaluator {
    private UserRepository userRepository;
    private ChallengeRepository challengeRepository;

    @Autowired
    @Required
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Required
    public void setChallengeRepository(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
        return authentication.getAuthorities().contains(UserRole.ADMINISTRATOR)
                || authentication.getAuthorities().contains(UserRole.MODERATOR) && permission.equals("modify")
                || checkDomainPermission(authentication, domainObject);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication.getAuthorities().contains(UserRole.ADMINISTRATOR)) return true;
        if (authentication.getAuthorities().contains(UserRole.MODERATOR) && permission.equals("modify")) return true;

        Optional<?> domainObject = Optional.empty();
        if (User.class.getSimpleName().equals(targetType)) domainObject = userRepository.findById((String) targetId);
        if (Challenge.class.getSimpleName().equals(targetType)) domainObject = challengeRepository.findById((long) targetId);
        return hasPermission(authentication, domainObject.orElseThrow(ResourceNotFoundException::new), permission);
    }

    private boolean checkDomainPermission(Authentication authentication, Object domainObject){
        User relatedUser;
        if (domainObject instanceof User){
            relatedUser = (User) domainObject;
        } else if (domainObject instanceof UserAccount) {
            relatedUser = ((UserAccount) domainObject).getUser();
        } else if (domainObject instanceof HasAuthor){
            relatedUser = ((HasAuthor) domainObject).getAuthor();
        } else return true;
        return authentication.getName().equals(relatedUser.getUsername());
    }
}
