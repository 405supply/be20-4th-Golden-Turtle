package com.team.goldenturtle.trade.query.dto.response;

import com.team.goldenturtle.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class UserHoldingListResponse {
    private List<UserHoldingResponse> holdings;
    private Pagination pagination;
}
