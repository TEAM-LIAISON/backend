package liaison.linkit.common.exception;

import static liaison.linkit.common.consts.LinkitStatic.FORBIDDEN;
import static liaison.linkit.common.consts.LinkitStatic.UNAUTHORIZED;

import java.lang.reflect.Field;
import java.util.Objects;
import liaison.linkit.common.annotation.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_1", "AccessToken 인증 시간이 만료되었습니다. 인증토큰을 재발급 해주세요"),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_401_2", "유효하지 않은 AccessToken입니다. 재로그인 해주세요"),
    REFRESH_TOKEN_EXPIRED(FORBIDDEN, "AUTH_403_1", "RefreshToken 인증 시간이 만료되었습니다. 재로그인 해주세요."),
    ACCESS_TOKEN_NOT_EXIST(FORBIDDEN, "AUTH_403_2", "올바른 AccessToken을 넣어주세요.");
    
    private final Integer status;
    private final String code;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
