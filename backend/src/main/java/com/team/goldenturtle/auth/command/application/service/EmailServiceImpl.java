package com.team.goldenturtle.auth.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "VERIFICATION_CODE:";
    private static final long VERIFICATION_CODE_EXPIRATION_SECONDS = 180; // 3분

    @Override
    public String sendVerificationCode(String email) {
        String code = generateVerificationCode();
        String subject = "[Golden Turtle] 이메일 인증 코드입니다.";
        String text = "인증 코드는 " + code + " 입니다. 3분 내에 입력해주세요.";
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                code,
                VERIFICATION_CODE_EXPIRATION_SECONDS,
                TimeUnit.SECONDS
                );
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);

        return code;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(VERIFICATION_CODE_PREFIX + email); // 인증 성공 시 코드 삭제
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            int number = random.nextInt(900000) + 100000; // 100000 ~ 999999
            return String.valueOf(number);
        } catch (NoSuchAlgorithmException e) {
            int number = (int) (Math.random() * 900000) + 100000;
            return String.valueOf(number);
        }
    }
}
