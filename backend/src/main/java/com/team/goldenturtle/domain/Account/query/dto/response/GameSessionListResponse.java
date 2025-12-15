package com.team.goldenturtle.domain.Account.query.dto.response;

import com.team.goldenturtle.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GameSessionListResponse {
    private List<GameSessionDTO> gameSessions;
    private Pagination pagination;

    @Builder
    public GameSessionListResponse(List<GameSessionDTO> gameSessions, Pagination pagination) {
        this.gameSessions = gameSessions;
        this.pagination = pagination;
    }
}