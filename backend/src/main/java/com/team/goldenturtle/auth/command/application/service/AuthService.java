package com.team.goldenturtle.auth.command.application.service;

import com.team.goldenturtle.auth.command.application.dto.request.PasswordResetRequest;
import com.team.goldenturtle.auth.command.application.dto.request.TokenRefreshRequest;
import com.team.goldenturtle.auth.command.application.dto.response.TokenRefreshResponse;
import com.team.goldenturtle.auth.command.application.dto.response.TokenResponse;
import com.team.goldenturtle.user.command.application.dto.request.UserLoginRequest;
import com.team.goldenturtle.user.command.application.dto.request.UserSignUpRequest;

public interface AuthService {
    void signUp(UserSignUpRequest userSignUpRequest);
    TokenResponse login(UserLoginRequest userLoginRequest);
    void sendVerificationCode(String email);
    void verifyEmail(String email, String code);
    void sendPasswordResetCode(String email);
    void resetPassword(PasswordResetRequest passwordResetRequest);
    void verifyPasswordResetCode(String email, String code);
    void logout(Long userId);
    TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request);
}
