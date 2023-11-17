package by.zemich.binancebot.service.impl;

import by.zemich.binancebot.config.properties.TestTradingProperties;
import by.zemich.binancebot.service.api.IBalanceManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceManagerImpl implements IBalanceManager {

    private final TestTradingProperties tradingProperties;
    private BigDecimal balance;
    private final BigDecimal deposit;
    private final BigDecimal minDeposit;

    public BalanceManagerImpl(TestTradingProperties tradingProperties) {
        this.tradingProperties = tradingProperties;
        this.balance = tradingProperties.getBalance();
        this.deposit = tradingProperties.getDeposit();
        this.minDeposit = tradingProperties.getMinDeposit();
    }

    @Override
    public BigDecimal allocateFundsForTransaction() {
        if (balance.doubleValue() >= deposit.doubleValue()) {
            balance = balance.subtract(deposit);
            return deposit;
        }

        if (balance.doubleValue() > minDeposit.doubleValue()) {
            return balance;
        }

        throw new RuntimeException("Deposit is not enough");
    }

    @Override
    public BigDecimal allocateAdditionalFunds(BigDecimal additional) {
        if(balance.doubleValue() >= additional.doubleValue()){
            balance = balance.subtract(additional);
            return additional;
        }

        throw new RuntimeException("there is not enough balance");
    }

    @Override
    public BigDecimal accumulateFounds(BigDecimal amount) {
        if(amount == null) throw new RuntimeException("amount can not be null");
        if(amount.doubleValue() <= 0) throw new RuntimeException("amount must be bigger 0");

        balance = balance.add(amount);
        return balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean bargainPossible() {
        return balance.doubleValue() >= minDeposit.doubleValue();
    }
}
