package by.zemich.binancebot.core.dto.binance;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.List;

@Data
public class AccountInformationResponseDto {

    private BigDecimal makerCommission;
    private BigDecimal takerCommission;
    private BigDecimal buyerCommission;
    private BigDecimal sellerCommission;
    private CommissionRateDto commissionRates;
    private boolean canTrade;
    private boolean canWithdraw;
    private boolean canDeposit;
    private boolean brokered;
    private boolean requireSelfTradePrevention;
    private boolean preventSor;
    private Timestamp updateTime;
    private String accountType;
    private List<BalanceDto> balances;
    private List<String> permissions;
    private Long uid;








}
