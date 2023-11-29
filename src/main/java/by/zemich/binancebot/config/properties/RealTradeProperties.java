package by.zemich.binancebot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "trading")
public class RealTradeProperties {
    private BigDecimal gain;
    private BigDecimal deposit;
    private BigDecimal taker;
    private BigDecimal maker;

    public RealTradeProperties(BigDecimal gain, BigDecimal deposit, BigDecimal taker) {
        this.gain = gain;
        this.deposit = deposit;
        this.taker = taker;
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

    public BigDecimal getMaker() {
        return maker;
    }

    public void setMaker(BigDecimal maker) {
        this.maker = maker;
    }

    public BigDecimal getTaker() {
        return taker;
    }

    public void setTaker(BigDecimal taker) {
        this.taker = taker;
    }
}
