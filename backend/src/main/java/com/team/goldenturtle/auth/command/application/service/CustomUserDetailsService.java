package com.team.goldenturtle.auth.command.application.service;

import com.team.goldenturtle.entity.User;
import com.team.goldenturtle.user.command.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 여기서 username은 우리 시스템의 'userEmail'에 해당합니다.
        return userRepository.findByUserEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }
    // DB에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(User user) {
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUserEmail())
//                .password(user.getUserPassword())
//                .roles(user.getUserRole().name()) // "USER", "ADMIN"
//                .build();
        return new CustomUserDetails(user);
    }
}
