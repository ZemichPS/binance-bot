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

@Entity(name = "fake_orders")
public class FakeOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private UUID uuid;

    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;

    @CreationTimestamp(source = SourceType.DB)
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

    @Column(name = "duration")
    private Long duration;

    @Column(name = "result")
    private boolean result;

    @Column(name = "current_result")
    private BigDecimal currentResult;

    @Enumerated(EnumType.STRING)
    private EOrderStatus status;

    public FakeOrderEntity() {
    }

    public FakeOrderEntity(UUID uuid, Timestamp dtUpdate, Timestamp dtCreate, LocalDateTime buyTime, LocalDateTime sellTime, BigDecimal buyPrice, BigDecimal sellPrice, String symbol, Long duration, boolean result, BigDecimal currentResult, EOrderStatus status) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.buyTime = buyTime;
        this.sellTime = sellTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.symbol = symbol;
        this.duration = duration;
        this.result = result;
        this.currentResult = currentResult;
        this.status = status;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public EOrderStatus getStatus() {
        return status;
    }

    public void setStatus(EOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(BigDecimal currentResult) {
        this.currentResult = currentResult;
    }
}
