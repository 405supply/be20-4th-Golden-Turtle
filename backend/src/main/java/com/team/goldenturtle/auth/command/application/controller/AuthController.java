package com.team.goldenturtle.auth.command.application.controller;

import com.team.goldenturtle.auth.command.application.dto.request.EmailRequest;
import com.team.goldenturtle.auth.command.application.dto.request.PasswordResetRequest;
import com.team.goldenturtle.auth.command.application.dto.request.TokenRefreshRequest;
import com.team.goldenturtle.auth.command.application.dto.request.VerificationRequest;
import com.team.goldenturtle.auth.command.application.dto.response.TokenRefreshResponse;
import com.team.goldenturtle.auth.command.application.dto.response.TokenResponse;
import com.team.goldenturtle.auth.command.application.service.AuthService;
import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.user.command.application.dto.request.UserLoginRequest;
import com.team.goldenturtle.user.command.application.dto.request.UserSignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@RequestBody UserSignUpRequest userSignUpRequest) {
        authService.signUp(userSignUpRequest);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    // 이메일 중복 확인 및 인증번호 발송
    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestBody @Valid EmailRequest emailRequest) {
        authService.sendVerificationCode(emailRequest.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다."));
    }

    // 이메일 인증번호 확인
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody @Valid VerificationRequest verificationRequest) {
        authService.verifyEmail(verificationRequest.getEmail(), verificationRequest.getCode());
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다. 회원가입을 계속 진행해주세요."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody UserLoginRequest userLoginRequest) {
        // 나중에 요청만 받고 예외는 서비스 쪽으로 리펙토링 예정
        TokenResponse tokenResponse = authService.login(userLoginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    // 비밀번호 재설정용 인증번호 발송
    @PostMapping("/password/send-code")
    public ResponseEntity<ApiResponse<String>> sendPasswordResetCode(@RequestBody @Valid EmailRequest emailRequest) {
        authService.sendPasswordResetCode(emailRequest.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다."));
    }

    // 비밀번호 재설정용 인증번호 확인 (신규 엔드포인트)
    @PostMapping("/password/verify-code")
    public ResponseEntity<ApiResponse<String>> verifyPasswordResetCode(@RequestBody @Valid VerificationRequest verificationRequest) {
        authService.verifyPasswordResetCode(verificationRequest.getEmail(), verificationRequest.getCode());
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 인증이 완료되었습니다. 새 비밀번호를 설정해주세요."));
    }

    // 비밀번호 재설정
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshAccessToken(@RequestBody @Valid TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }
}
