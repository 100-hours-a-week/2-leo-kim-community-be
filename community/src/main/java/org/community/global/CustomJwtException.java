package org.community.global;

import lombok.Getter;
import org.community.common.user.UserResponseMessage;

@Getter
public class CustomJwtException extends RuntimeException {
    private final UserResponseMessage responseMessage;

    public CustomJwtException(UserResponseMessage responseMessage) {
        super(responseMessage.getMessage());
        this.responseMessage = responseMessage;
    }
}
