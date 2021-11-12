package com.idus.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.idus.domain.user.type.UserGender;
import com.idus.domain.user.type.UserRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "signUpBuilder")
    public User(String name, String nickname, String password, String phoneNumber, String email, String gender) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = UserGender.convertFrom(gender);
        this.roles = Stream.of(UserRole.USER)
                .collect(Collectors.toSet());
    }

    public String getGenderAsString() {
        String gender = null;

        if (Objects.nonNull(this.gender)) {
            gender = this.gender.name();
        }

        return gender;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }
}
