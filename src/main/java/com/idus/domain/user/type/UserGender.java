package com.idus.domain.user.type;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum UserGender {
    MALE("남성"), FEMALE("여성");

    private String description;

    UserGender(String description) {
        this.description = description;
    }

    public static UserGender convertFrom(String gender) {
        if (Objects.nonNull(gender)) {
            switch (gender) {
                case "MALE":
                    return MALE;
                case "FEMALE":
                    return FEMALE;
            }
        }

        return null;
    }
}
