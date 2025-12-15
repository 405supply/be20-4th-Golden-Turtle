package com.team.goldenturtle.domain.Account.query.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private String userEmail;
    private String userNickname;
    private String userPassword;
    private String userRole;
    private String userStatus;
    private String createdAt;
    private String updatedAt;
}
