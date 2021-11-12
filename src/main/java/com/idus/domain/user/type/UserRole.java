package com.idus.domain.user.type;

import lombok.Getter;

@Getter
public enum UserRole  {
    ADMIN("관리자"), USER("회원");

    private String description;

    UserRole(String description) {
        this.description = description;
    }

}
