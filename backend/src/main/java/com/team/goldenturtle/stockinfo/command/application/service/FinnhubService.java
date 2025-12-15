package com.team.goldenturtle.stockinfo.command.application.service;

import com.team.goldenturtle.stockinfo.command.application.dto.response.FinnhubProfileResponse;

public interface FinnhubService {
    public FinnhubProfileResponse getProfile(String symbol);
    public void fetchCryptoProfiles();
}
