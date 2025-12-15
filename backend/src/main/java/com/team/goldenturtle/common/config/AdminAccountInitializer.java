package com.team.goldenturtle.common.config;

import com.team.goldenturtle.common.enums.Role;
import com.team.goldenturtle.entity.User;
import com.team.goldenturtle.user.command.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!prod") // 운영(prod) 프로필이 아닐 때만 실행
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@example.com";
        String adminPassword = "admin"; // 실제 사용할 관리자 비밀번호를 입력하세요.
        String adminNickname = "AdminUser";

        // 해당 이메일의 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.findByUserEmail(adminEmail).isPresent()) {
            User admin = User.builder()
                    .userEmail(adminEmail)
                    .userPassword(passwordEncoder.encode(adminPassword)) // 비밀번호 암호화
                    .userNickname(adminNickname)
                    .userRole(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}
