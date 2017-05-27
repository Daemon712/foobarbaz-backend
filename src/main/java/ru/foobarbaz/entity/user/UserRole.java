package ru.foobarbaz.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum UserRole implements GrantedAuthority {
    USER,
    MODERATOR,
    ADMINISTRATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
