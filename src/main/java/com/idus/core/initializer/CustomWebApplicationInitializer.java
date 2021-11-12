package com.idus.core.initializer;

import com.idus.domain.user.entity.User;
import com.idus.domain.user.repository.UserRepository;
import com.idus.domain.user.type.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class CustomWebApplicationInitializer {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            private UserRepository userRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                User user = User.signUpBuilder()
                        .email("user@idus.com")
                        .password(passwordEncoder.encode("abcde가A1!"))
                        .name("백승호")
                        .phoneNumber("01012345678")
                        .nickname("abcd")
                        .build();

                userRepository.save(user);
            }
        };
    }
}
