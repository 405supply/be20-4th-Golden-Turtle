package com.team.goldenturtle.user.command.application.service;

import com.team.goldenturtle.entity.User;
import com.team.goldenturtle.user.command.application.dto.request.ChangePasswordRequest;
import com.team.goldenturtle.user.command.application.dto.request.UpdateUserNickNameRequest;
import com.team.goldenturtle.user.command.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.team.goldenturtle.common.exception.BusinessException;
import com.team.goldenturtle.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 비밀번호 변경
    @Transactional
    @Override
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST); // Or a more specific error code if available
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }

    // 닉네임 변경
    @Transactional
    @Override
    public void updateNickname(Long userId, UpdateUserNickNameRequest updateUserNickNameRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(userRepository.findByUserNickname(updateUserNickNameRequest.getNewNickname()).isPresent()){
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        user.updateUserNickname(updateUserNickNameRequest.getNewNickname());
    }

    // 회원탈퇴
    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.deleteUser();
    }
}
