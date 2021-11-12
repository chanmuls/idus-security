package com.idus.application.service;

import com.idus.application.request.SignInRequest;
import com.idus.application.request.SignUpRequest;
import com.idus.application.request.TokenRequest;
import com.idus.application.response.OrderResponse;
import com.idus.application.response.UserResponse;
import com.idus.application.security.dto.PrincipalDetails;
import com.idus.core.exception.ApiException;
import com.idus.core.type.ServiceErrorType;
import com.idus.domain.order.entity.Order;
import com.idus.domain.order.service.OrderService;
import com.idus.domain.user.entity.RefreshToken;
import com.idus.domain.user.entity.User;
import com.idus.domain.user.service.UserService;
import com.idus.application.response.TokenResponse;
import com.idus.application.security.provider.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserAppService {
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private OrderService orderService;

    @Transactional
    public TokenResponse signIn(SignInRequest signInRequest) {
        // 1. ID/PW 로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword());

        // 2. 사용자 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3.JWT 토큰 생성
        TokenResponse customToken = tokenProvider.generateToken(authentication);

        // 4. RefreshToken 저장
        userService.createRefreshToken(authentication, customToken.getRefreshToken());

        // 5. 토큰 발급
        return customToken;
    }

    @Transactional
    public TokenResponse reissuance(TokenRequest tokenRequest) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            // 1-1 토큰 삭제
            userService.deleteByRefreshToken(tokenRequest.getRefreshToken());

            throw new ApiException(ServiceErrorType.INVALID_USER_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 인증 정보
        Authentication authentication = tokenProvider.getAuthentication(tokenRequest.getAccessToken());

        // 3. Refresh Token 정보
        RefreshToken refreshToken = userService.getByRefreshToken(tokenRequest.getRefreshToken());

        // 5. 새로운 토큰 생성
        TokenResponse customToken = tokenProvider.generateAccessToken(authentication);

        customToken.setRefreshToken(refreshToken.getToken());

        // 토큰 발급
        return customToken;
    }

    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest) {
        signUpRequest.validation();

        User user = User.signUpBuilder()
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .nickname(signUpRequest.getNickname())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phoneNumber(signUpRequest.getPhoneNumber())
                .gender(signUpRequest.getGender())
                .build();

        User newUser = userService.save(user);

        return UserResponse.builder()
                .id(newUser.getId())
                .gender(newUser.getGenderAsString())
                .email(newUser.getEmail())
                .name(newUser.getName())
                .nickname(newUser.getNickname())
                .phoneNumber(newUser.getPhoneNumber())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    @Transactional
    public void signOut(PrincipalDetails principal) {
        userService.deleteByUserId(principal.getUser().getId());
    }

    public UserResponse me(PrincipalDetails principal) {
        User user = userService.getById(principal.getUser().getId());

        return UserResponse.builder()
                .id(user.getId())
                .gender(user.getGenderAsString())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public Page<UserResponse> getByPageable(PrincipalDetails principal, String name, String email, Pageable pageable) {
        Page<User> userPage = userService.getSearchByPageable(name, email, pageable);

        if (0 == userPage.getTotalElements()) {
            return new PageImpl<>(new ArrayList<>(), userPage.getPageable(), userPage.getTotalElements());
        }

        List<Long> userIds = userPage.stream().map(User::getId)
                .collect(Collectors.toList());
        List<Order> orders = orderService.getAllByUserIds(userIds);

        Map<Long, OrderResponse> orderResponseMap = orders.stream()
                .collect(Collectors.toMap(Order::getUserId, order -> OrderResponse.builder()
                        .productName(order.getProductName())
                        .payedAt(order.getPayedAt())
                        .id(order.getId())
                        .build()));

        List<UserResponse> userResponses = userPage.stream().map(user -> {
            OrderResponse orderResponse = null;

            if (orderResponseMap.containsKey(user.getId())) {
                orderResponse = orderResponseMap.get(user.getId());
            }

            return UserResponse.builder()
                    .id(user.getId())
                    .gender(user.getGenderAsString())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .phoneNumber(user.getPhoneNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .lastOrder(orderResponse)
                    .build();
        }).collect(Collectors.toList());

        return new PageImpl<>(userResponses, userPage.getPageable(), userPage.getTotalElements());
    }
}
