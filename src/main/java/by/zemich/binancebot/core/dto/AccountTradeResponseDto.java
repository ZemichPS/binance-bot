package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountTradeResponseDto {
    List<AccountTradeDto> accountTrades;
}
