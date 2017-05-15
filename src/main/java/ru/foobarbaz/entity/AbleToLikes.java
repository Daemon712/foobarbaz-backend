package ru.foobarbaz.entity;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.foobarbaz.entity.user.User;

import java.util.Set;

public interface AbleToLikes {
    User getAuthor();
    Set<User> getLikes();

    default int getRating() {
        return getLikes() != null ? getLikes().size() : 0;
    }

    default boolean isLiked() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getLikes() != null && getLikes().contains(new User(username));
    }
}
