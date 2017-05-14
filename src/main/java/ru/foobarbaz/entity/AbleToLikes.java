package ru.foobarbaz.entity;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.foobarbaz.entity.user.User;

import java.util.Set;

public interface AbleToLikes {
    User getAuthor();
    Set<User> getLikes();
    void setLikes(Set<User> likes);

    default int getRating() {
        return getLikes().size();
    }

    default boolean isLiked() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getLikes().contains(new User(username));
    }
}
