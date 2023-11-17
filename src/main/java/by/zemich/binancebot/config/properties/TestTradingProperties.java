package by.zemich.binancebot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "test.trading")
public class TestTradingProperties {
    private BigDecimal gain;
    private BigDecimal deposit;
    private BigDecimal balance;

    private BigDecimal minDeposit;

    public BigDecimal getGain() {
        return gain;
    }

    public void setGain(BigDecimal gain) {
        this.gain = gain;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMinDeposit() {
        return minDeposit;
    }

    public void setMinDeposit(BigDecimal minDeposit) {
        this.minDeposit = minDeposit;
    }
}
