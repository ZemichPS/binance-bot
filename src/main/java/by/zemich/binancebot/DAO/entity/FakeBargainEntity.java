package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.enums.EOrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "fake_bargains")
public class FakeBargainEntity {
    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Version
    @UpdateTimestamp(source = SourceType.VM)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;

    @CreationTimestamp(source = SourceType.VM)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_create")
    private Timestamp dtCreate;

    @Column(name = "buy_time")
    private LocalDateTime buyTime;
    @Column(name = "sell_time")
    private LocalDateTime sellTime;
    @Column(name = "buy_price")
    private BigDecimal buyPrice;

    @Column(name = "sell_price")
    private BigDecimal sellPrice;
    @Column(name = "symbol")
    private String symbol;
    @Column(name = "asset_amount")
    private BigDecimal assetAmount;
    @Column(name = "spend_on_purchase")
    private BigDecimal totalSpent;
    @Column(name = "duration")
    private Long duration;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EOrderStatus status;
    @Column(name = "maker_fee")
    private BigDecimal makerFee;
    @Column(name = "taker_fee")
    private BigDecimal takerFee;
    @Column(name = "finance_result")
    private BigDecimal financeResult;
    @Column(name = "current_finance_result")
    private BigDecimal currentFinanceResult;
    @Column(name = "price_percent_difference")
    private BigDecimal pricePercentDifference;
    @Column(name = "strategy_name")
    private String strategyName;

    @Column(name = "indicator_values")
    private String indicatorValue;



    public FakeBargainEntity(UUID uuid,
                             Timestamp dtUpdate,
                             Timestamp dtCreate,
                             LocalDateTime buyTime,
                             LocalDateTime sellTime,
                             BigDecimal buyPrice,
                             BigDecimal sellPrice,
                             String symbol,
                             BigDecimal assetAmount,
                             BigDecimal totalSpent,
                             Long duration,
                             EOrderStatus status,
                             BigDecimal makerFee,
                             BigDecimal takerFee,
                             BigDecimal financeResult,
                             BigDecimal currentFinanceResult,
                             BigDecimal pricePercentDifference,
                             String strategyName, String indicatorValue) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.buyTime = buyTime;
        this.sellTime = sellTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.symbol = symbol;
        this.assetAmount = assetAmount;
        this.totalSpent = totalSpent;
        this.duration = duration;
        this.status = status;
        this.makerFee = makerFee;
        this.takerFee = takerFee;
        this.financeResult = financeResult;
        this.currentFinanceResult = currentFinanceResult;
        this.pricePercentDifference = pricePercentDifference;
        this.strategyName = strategyName;
        this.indicatorValue = indicatorValue;
    }

    public FakeBargainEntity() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Timestamp getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(Timestamp dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    public Timestamp getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Timestamp dtCreate) {
        this.dtCreate = dtCreate;
    }

    public LocalDateTime getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(LocalDateTime buyTime) {
        this.buyTime = buyTime;
    }

    public LocalDateTime getSellTime() {
        return sellTime;
    }

    public void setSellTime(LocalDateTime sellTime) {
        this.sellTime = sellTime;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getAssetAmount() {
        return assetAmount;
    }

    public void setAssetAmount(BigDecimal assetAmount) {
        this.assetAmount = assetAmount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal spendOnPurchase) {
        this.totalSpent = spendOnPurchase;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public EOrderStatus getStatus() {
        return status;
    }

    public void setStatus(EOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getMakerFee() {
        return makerFee;
    }

    public void setMakerFee(BigDecimal makerFee) {
        this.makerFee = makerFee;
    }

    public BigDecimal getTakerFee() {
        return takerFee;
    }

    public void setTakerFee(BigDecimal taker_Fee) {
        this.takerFee = taker_Fee;
    }

    public BigDecimal getFinanceResult() {
        return financeResult;
    }

    public void setFinanceResult(BigDecimal financeResult) {
        this.financeResult = financeResult;
    }

    public BigDecimal getPricePercentDifference() {
        return pricePercentDifference;
    }

    public void setPricePercentDifference(BigDecimal percentDifference) {
        this.pricePercentDifference = percentDifference;
    }

    public BigDecimal getCurrentFinanceResult() {
        return currentFinanceResult;
    }

    public void setCurrentFinanceResult(BigDecimal currentFinanceResult) {
        this.currentFinanceResult = currentFinanceResult;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getIndicatorValue() {
        return indicatorValue;
    }

    public void setIndicatorValue(String indicatorValue) {
        this.indicatorValue = indicatorValue;
    }
}
