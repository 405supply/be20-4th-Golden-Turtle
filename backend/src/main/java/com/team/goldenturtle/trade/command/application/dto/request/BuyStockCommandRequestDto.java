package com.team.goldenturtle.trade.command.application.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyStockCommandRequestDto {
    private Long userId;
    private String ticker;
    @NotNull
    @Min(1)
    private Integer quantity;

}
