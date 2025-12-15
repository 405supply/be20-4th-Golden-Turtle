package com.team.goldenturtle.domain.Account.query.dto.response;

import com.team.goldenturtle.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HoldingListResponse {
    private List<HoldingDTO> holdings;
    private Pagination pagination;
}
