package by.zemich.binancebot.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountAsset {
    private String asset;
    private BigDecimal initialMargin;
    private BigDecimal maintMargin;
    private BigDecimal marginBalance;
    private BigDecimal maxWithdrawAmount;
    private BigDecimal openOrderInitialMargin;
    private BigDecimal positionInitialMargin;
    private BigDecimal unrealizedProfit;
    private BigDecimal walletBalance;

}
