package by.zemich.binancebot.service.api;

import java.math.BigDecimal;

public interface IBalanceManager {
    BigDecimal allocateFundsForTransaction();
    BigDecimal accumulateFounds(BigDecimal amount);
    BigDecimal getBalance();

    boolean bargainPossible();

}
