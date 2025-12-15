package com.team.goldenturtle.user.command.application.service;

import com.team.goldenturtle.user.command.application.dto.request.ChangePasswordRequest;
import com.team.goldenturtle.user.command.application.dto.request.UpdateUserNickNameRequest;

public interface UserService {
    // 비밀번호 변경
    void changePassword(Long userId, ChangePasswordRequest changePasswordRequest);

    // 닉네임 변경
    void updateNickname(Long userId, UpdateUserNickNameRequest newNickname);

    // 회원탈퇴 (status 상태 변경 ACTIVE -> INACTIVE)
    void deleteUser(Long userId);
}
