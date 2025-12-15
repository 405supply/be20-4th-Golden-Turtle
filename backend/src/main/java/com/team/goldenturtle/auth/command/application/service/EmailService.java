package com.team.goldenturtle.auth.command.application.service;

public interface EmailService {
    String sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
}
