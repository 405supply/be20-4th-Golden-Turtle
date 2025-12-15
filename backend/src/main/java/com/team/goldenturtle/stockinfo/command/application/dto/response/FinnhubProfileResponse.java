package com.team.goldenturtle.stockinfo.command.application.dto.response;

import lombok.Getter;

@Getter
public class FinnhubProfileResponse {
    private String name;
    private String ticker;
    private String exchange;
    private String logo;
    private String country;
    private String ipo;
    private String weburl;
}
