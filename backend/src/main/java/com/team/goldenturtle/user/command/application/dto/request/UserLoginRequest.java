package com.team.goldenturtle.user.command.application.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequest {
    private String userEmail;
    private String userPassword;
}
