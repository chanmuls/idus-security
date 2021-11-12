package com.idus.application.request;

import com.idus.core.exception.ApiException;
import com.idus.core.type.ServiceErrorType;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestTest {
    @ParameterizedTest
    @ValueSource(strings = {"a가A1", "a가A!"})
    void name_validation_fail(String name) {
        SignUpRequest signUpRequest =
                new SignUpRequest(name, null, null, null, null, null);

        ApiException apiException = assertThrows(ApiException.class, signUpRequest::validation);

        assertEquals(apiException.getServiceErrorType(), ServiceErrorType.INVALID_USER_NAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a가A", "aA", "A가", "a가", "a!"})
    void nickname_validation_fail(String nickname) {
        SignUpRequest signUpRequest =
                new SignUpRequest("a가", nickname, null, null, null, null);

        ApiException apiException = assertThrows(ApiException.class, signUpRequest::validation);

        assertEquals(apiException.getServiceErrorType(), ServiceErrorType.INVALID_USER_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab가A1!", "abcde가A1", "abcde가A!", "abcde가1!"})
    void password_validation_fail(String password) {
        SignUpRequest signUpRequest =
                new SignUpRequest("a가", "a", password, null, null, null);

        ApiException apiException = assertThrows(ApiException.class, signUpRequest::validation);

        assertEquals(apiException.getServiceErrorType(), ServiceErrorType.INVALID_USER_PASSWORD);
    }

}