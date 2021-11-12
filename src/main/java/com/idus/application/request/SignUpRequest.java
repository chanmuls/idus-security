package com.idus.application.request;

import com.idus.core.annotation.EnumValid;
import com.idus.core.exception.ApiException;
import com.idus.core.type.ServiceErrorType;
import com.idus.domain.user.type.UserGender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private static final String PHONE_NUMBER_REGEX = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
    private static final String USER_NAME_REGEX = "^[a-zA-Z가-힣]*$";
    private static final String USER_NICKNAME_REGEX = "^[a-z]*$";
    private static final String USER_PASSWORD_REGEX = "(?=.*\\d{1,50})(?=.*[~`!@#$%\\^&*()-+=]{1,50})(?=.*[a-z]{1,50})(?=.*[A-Z]{1,50}).{8,50}$";

    @NotNull(message = "이름은 필수 항목입니다.")
    private String name;
    @NotNull(message = "별명은 필수 항목입니다.")
    private String nickname;
    @NotNull(message = "패스워드는 필수 항목입니다.")
    private String password;
    @NotNull(message = "전화번호는 필수 항목입니다.")
    private String phoneNumber;
    @Email(message = "이메일 형식이 아닙니다.")
    @NotNull(message = "이메일은 필수 항목입니다.")
    private String email;
    @EnumValid(enumClass = UserGender.class, message = "성별은 'MALE' 또는 'FEMALE'으로 표현해 주세요.")
    private String gender;

    public void validation() {
        if (ObjectUtils.isEmpty(this.name)
                || !this.name.matches(USER_NAME_REGEX)) {
            throw new ApiException(ServiceErrorType.INVALID_USER_NAME);
        }

        if (ObjectUtils.isEmpty(this.nickname)
                || !this.nickname.matches(USER_NICKNAME_REGEX)) {
            throw new ApiException(ServiceErrorType.INVALID_USER_NICKNAME);
        }

        if (ObjectUtils.isEmpty(this.password)
                || !this.password.matches(USER_PASSWORD_REGEX)) {
            throw new ApiException(ServiceErrorType.INVALID_USER_PASSWORD);
        }

        if (ObjectUtils.isEmpty(this.phoneNumber)
                || !this.phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            throw new ApiException(ServiceErrorType.INVALID_USER_PHONE_NUMBER);
        }
    }
}
