package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bargain")
public class BargainEntity {
    @Id
    private UUID uuid;
    private String strategy;
    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp dtCreate;
    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp dtUpdate;
    private BigDecimal percentageResult;
    private BigDecimal financeResult;
    private Long timeInWork;
    private Timestamp finishTime;
    @Enumerated(EnumType.STRING)
    private EBargainStatus status;
    private String symbol;
    private BigDecimal averagePrice;
    private BigDecimal interest;
    private BigDecimal fee;
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE,
            orphanRemoval = true,
            mappedBy = "bargain"
    )
    @JoinColumn(name = "bargain_uuid")
    private List<OrderEntity> orders;

    public BargainEntity(UUID uuid,
                         String strategy,
                         Timestamp dtCreate,
                         Timestamp dtUpdate,
                         BigDecimal percentageResult,
                         BigDecimal financeResult,
                         Long timeInWork,
                         Timestamp finishTime,
                         EBargainStatus status,
                         String symbol,
                         BigDecimal averagePrice, BigDecimal interest,
                         BigDecimal fee,
                         List<OrderEntity> orders) {
        this.uuid = uuid;
        this.strategy = strategy;
        this.dtCreate = dtCreate;
        this.dtUpdate = dtUpdate;
        this.percentageResult = percentageResult;
        this.financeResult = financeResult;
        this.timeInWork = timeInWork;
        this.finishTime = finishTime;
        this.status = status;
        this.symbol = symbol;
        this.averagePrice = averagePrice;
        this.interest = interest;
        this.fee = fee;
        this.orders = orders;
    }

    public BargainEntity() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Timestamp getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Timestamp dtCreate) {
        this.dtCreate = dtCreate;
    }

    public Timestamp getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(Timestamp dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    public BigDecimal getPercentageResult() {
        return percentageResult;
    }

    public void setPercentageResult(BigDecimal percentageResult) {
        this.percentageResult = percentageResult;
    }

    public BigDecimal getFinanceResult() {
        return financeResult;
    }

    public void setFinanceResult(BigDecimal financeResult) {
        this.financeResult = financeResult;
    }

    public Long getTimeInWork() {
        return timeInWork;
    }

    public void setTimeInWork(Long timeInWork) {
        this.timeInWork = timeInWork;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public EBargainStatus getStatus() {
        return status;
    }

    public void setStatus(EBargainStatus status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
}
