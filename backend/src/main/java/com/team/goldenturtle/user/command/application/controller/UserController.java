package com.team.goldenturtle.user.command.application.controller;

import com.team.goldenturtle.auth.command.application.service.CustomUserDetails;
import com.team.goldenturtle.common.dto.ApiResponse;
import com.team.goldenturtle.user.command.application.dto.request.ChangePasswordRequest;
import com.team.goldenturtle.user.command.application.dto.request.UpdateUserNickNameRequest;
import com.team.goldenturtle.user.command.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    // 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changeMyPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

    // 닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<String>> changeNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserNickNameRequest updateUserNickNameRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        userService.updateNickname(userId, updateUserNickNameRequest);
        return ResponseEntity.ok(ApiResponse.success("닉네임이 성공적으로 변경되었습니다."));
    }

    // 회원탈퇴
    @DeleteMapping("/me/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴 되었습니다.(상태변경)"));
    }
}
