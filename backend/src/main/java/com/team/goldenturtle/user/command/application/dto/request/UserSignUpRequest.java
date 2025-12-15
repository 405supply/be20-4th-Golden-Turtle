package com.team.goldenturtle.user.command.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignUpRequest {
    private String userEmail;
    private String userPassword;
    private String userNickname;
}
