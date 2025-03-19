package org.community.global;

import lombok.Getter;
import org.community.common.user.UserResponseMessage;

@Getter
public class CustomException extends RuntimeException {
    private final UserResponseMessage responseMessage;

    public CustomException(UserResponseMessage responseMessage) {
        super(responseMessage.getMessage());
        this.responseMessage = responseMessage;
    }
}
