package com.team.goldenturtle.auth.command.application.service;

import com.team.goldenturtle.auth.command.application.dto.request.PasswordResetRequest;
import com.team.goldenturtle.auth.command.application.dto.request.TokenRefreshRequest;
import com.team.goldenturtle.auth.command.application.dto.response.TokenRefreshResponse;
import com.team.goldenturtle.auth.command.application.dto.response.TokenResponse;
import com.team.goldenturtle.common.enums.Role;
import com.team.goldenturtle.common.enums.UserStatus;
import com.team.goldenturtle.common.exception.BusinessException;
import com.team.goldenturtle.common.exception.ErrorCode;
import com.team.goldenturtle.common.jwt.JwtTokenProvider;
import com.team.goldenturtle.entity.User;
import com.team.goldenturtle.user.command.application.dto.request.UserLoginRequest;
import com.team.goldenturtle.user.command.application.dto.request.UserSignUpRequest;
import com.team.goldenturtle.user.command.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;


    private static final String VERIFIED_EMAIL_PREFIX = "VERIFIED_EMAIL:";
    private static final long VERIFIED_EMAIL_EXPIRATION_MINUTES = 10;

    private static final String PASSWORD_RESET_ELIGIBLE_PREFIX = "PASSWORD_RESET_ELIGIBLE:";
    private static final long PASSWORD_RESET_ELIGIBILITY_EXPIRATION_MINUTES = 5; // 5 minutes for reset eligibility

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidity;

    @Override
    @Transactional
    public void signUp(UserSignUpRequest userSignUpRequest){
        String email = userSignUpRequest.getUserEmail();

        String isVerified = stringRedisTemplate.opsForValue().get(VERIFIED_EMAIL_PREFIX + email);
        if (isVerified == null || !isVerified.equals("true")) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
        }

        if(userRepository.findByUserEmail(userSignUpRequest.getUserEmail()).isPresent()){
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if(userRepository.findByUserNickname(userSignUpRequest.getUserNickname()).isPresent()){
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 유저 정보
        User user = User.builder()
                .userEmail(userSignUpRequest.getUserEmail())
                .userPassword(passwordEncoder.encode(userSignUpRequest.getUserPassword()))
                .userNickname(userSignUpRequest.getUserNickname())
                .userRole(Role.USER) // Role도 기본값을 설정해줍니다.
                .userStatus(UserStatus.ACTIVE) // 'ACTIVE' 상태로 지정합니다.
                .build();
        userRepository.save(user);
    }

    // 인증번호 발송 로직
    @Override
    public void sendVerificationCode(String email) {
        if (userRepository.findByUserEmail(email).isPresent()){
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        emailService.sendVerificationCode(email); // EmailService의 메소드 호출
    }

    // 인증번호 확인 로직
    @Override
    public void verifyEmail(String email, String code) {
        boolean isVerified = emailService.verifyCode(email, code); // EmailService의 메소드 호출
        if (!isVerified) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        // 인증 성공 시, Redis에 '인증 완료' 상태를 10분간 저장
        stringRedisTemplate.opsForValue().set(
                VERIFIED_EMAIL_PREFIX + email,
                "true",
                VERIFIED_EMAIL_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }



    @Override
    public TokenResponse login(UserLoginRequest userLoginRequest) {
        // 이메일 기반으로 사용자 조회
        User user = userRepository.findByUserEmail(userLoginRequest.getUserEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 계정 비활성화 상태 확인
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(userLoginRequest.getUserPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 생성을 위해 userId를 문자열로 변환
        String userId = String.valueOf(user.getUserId());

        // userId를 subject로 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId, user.getUserRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Redis에 Refresh Token 저장
        stringRedisTemplate.opsForValue().set(
                "RT:" + userId,
                refreshToken,
                refreshTokenValidity,
                TimeUnit.SECONDS
        );

        return new TokenResponse(accessToken, refreshToken);
    }


    @Override
    public void sendPasswordResetCode(String email) {
        userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        emailService.sendVerificationCode(email);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        String email = passwordResetRequest.getEmail();
        String passwordResetFlag = stringRedisTemplate.opsForValue().get(PASSWORD_RESET_ELIGIBLE_PREFIX + email);

        if (passwordResetFlag == null || !passwordResetFlag.equals("true")) {
            // Check if the user exists to distinguish between "not found" and "not eligible"
            userRepository.findByUserEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // User not found error

            // If user exists but no eligibility flag, then they are not eligible to reset yet
            throw new BusinessException(ErrorCode.PASSWORD_RESET_NOT_ELIGIBLE);
        }

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // This should ideally not happen if the eligibility check passed after user lookup

        // Clear the eligibility flag after use
        stringRedisTemplate.delete(PASSWORD_RESET_ELIGIBLE_PREFIX + email);

        user.setUserPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        // userRepository.save(user) is not needed if user object is managed by Hibernate/JPA and @Transactional is active
    }

    @Override
    public void verifyPasswordResetCode(String email, String code) {
        // Check if user exists first to prevent unnecessary code verification and provide clearer error
        userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        boolean isVerified = emailService.verifyCode(email, code);
        if (!isVerified) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // If code is verified, set a flag in Redis that this email is eligible to reset password
        stringRedisTemplate.opsForValue().set(
                PASSWORD_RESET_ELIGIBLE_PREFIX + email,
                "true",
                PASSWORD_RESET_ELIGIBILITY_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }

    @Override
    public TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 토큰에서 userId(Subject) 추출
        String userId = jwtTokenProvider.getUsername(refreshToken);

        // Redis에 저장된 토큰과 일치하는지 확인
        String storedRefreshToken = stringRedisTemplate.opsForValue().get("RT:" + userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 새로운 액세스 토큰 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        Role role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> Role.valueOf(
                        grantedAuthority.getAuthority().replace("ROLE_", "")
                ))
                .orElseThrow(() -> new RuntimeException("유저에게 권한이 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);

        return new TokenRefreshResponse(newAccessToken);
    }



    @Override
    public void logout(Long userId) {
        log.info("Attempting to delete refresh token for user: {}", userId); // 정상적으로 사라졌는지 터미널 로그로 확인
        stringRedisTemplate.delete("RT:" + userId);
    }
}
