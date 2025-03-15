package dto.user.response;

import common.user.UserResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ApiResponse {
    private final String message;
    private final Object data;

    // response의 body를 구성해주는 메서드
    public static ApiResponse of(UserResponseMessage responseMessage) {
        return ApiResponse.builder()
                .message(responseMessage.getMessage())
                .data(null)
                .build();
    }

    public static ApiResponse of(UserResponseMessage responseMessage, Object data) {
        return ApiResponse.builder()
                .message(responseMessage.getMessage())
                .data(data)
                .build();
    }

    public static ResponseEntity<ApiResponse> response(UserResponseMessage userResponseMessage) {
        return ResponseEntity
                .status(userResponseMessage.getStatusCode())
                .body(of(userResponseMessage));
    }

    public static ResponseEntity<ApiResponse> response(UserResponseMessage userResponseMessage, Object data) {
        return ResponseEntity
                .status(userResponseMessage.getStatusCode())
                .body(of(userResponseMessage, data));
    }
}
