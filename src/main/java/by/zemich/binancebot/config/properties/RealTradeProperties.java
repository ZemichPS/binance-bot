package by.zemich.binancebot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "trading")
public class RealTradeProperties {
    private BigDecimal gain;
    private BigDecimal deposit;

    public RealTradeProperties(BigDecimal gain, BigDecimal deposit) {
        this.gain = gain;
        this.deposit = deposit;
    }

    public RealTradeProperties() {
    }

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
}
