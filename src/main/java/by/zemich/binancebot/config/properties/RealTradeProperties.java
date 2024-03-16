package by.zemich.binancebot.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "trading")

public class RealTradeProperties {
    private BigDecimal bargainPercentageGain;
    private BigDecimal depositPerBargain;
    private BigDecimal takerFee;
    private BigDecimal makerFee;
    private int criticalLostTimeForBuying;
    private BigDecimal criticalLostPercentage;

    public RealTradeProperties(BigDecimal bargainPercentageGain,
                               BigDecimal depositPerBargain,
                               BigDecimal takerFee,
                               BigDecimal makerFee,
                               int criticalLostTimeForBuying,
                               BigDecimal criticalLostPercentage) {
        this.bargainPercentageGain = bargainPercentageGain;
        this.depositPerBargain = depositPerBargain;
        this.takerFee = takerFee;
        this.makerFee = makerFee;
        this.criticalLostTimeForBuying = criticalLostTimeForBuying;
        this.criticalLostPercentage = criticalLostPercentage;
    }

    public BigDecimal getBargainPercentageGain() {
        return bargainPercentageGain;
    }

    public void setBargainPercentageGain(BigDecimal bargainPercentageGain) {
        this.bargainPercentageGain = bargainPercentageGain;
    }

    public BigDecimal getDepositPerBargain() {
        return depositPerBargain;
    }

    public void setDepositPerBargain(BigDecimal depositPerBargain) {
        this.depositPerBargain = depositPerBargain;
    }

    public BigDecimal getTakerFee() {
        return takerFee;
    }

    public void setTakerFee(BigDecimal takerFee) {
        this.takerFee = takerFee;
    }

    public BigDecimal getMakerFee() {
        return makerFee;
    }

    public void setMakerFee(BigDecimal makerFee) {
        this.makerFee = makerFee;
    }

    public int getCriticalLostTimeForBuying() {
        return criticalLostTimeForBuying;
    }

    public void setCriticalLostTimeForBuying(int criticalLostTimeForBuying) {
        this.criticalLostTimeForBuying = criticalLostTimeForBuying;
    }

    public BigDecimal getCriticalLostPercentage() {
        return criticalLostPercentage;
    }

    public void setCriticalLostPercentage(BigDecimal criticalLostPercentage) {
        this.criticalLostPercentage = criticalLostPercentage;
    }
}
