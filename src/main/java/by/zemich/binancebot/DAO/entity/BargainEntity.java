package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "bargain")
public class BargainEntity {
    @Id
    private UUID uuid;

    @Column(name = "strategy")
    private String strategy;

    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_create")
    private Timestamp dtCreate;
    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;

    @Column(name = "percentage_result")
    private BigDecimal percentageResult;
    @Column(name = "finance_result")
    private BigDecimal financeResult;

    @Column(name = "time_in_work")
    private Long timeInWork;

    @Column(name = "finish_time")
    private Timestamp finishTime;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EBargainStatus status;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "interest")
    private BigDecimal interest;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "buy_order_id", referencedColumnName = "uuid")
    private OrderEntity buyOrder;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "sell_order_id", referencedColumnName = "uuid")
    private OrderEntity sellOrder;



    public BargainEntity() {
    }

    public BargainEntity(UUID uuid, String strategy, Timestamp dtCreate, Timestamp dtUpdate, BigDecimal percentageResult, BigDecimal financeResult, Long timeInWork, Timestamp finishTime, EBargainStatus status, String symbol, BigDecimal currentPercentageResult, BigDecimal interest, OrderEntity buyOrder, OrderEntity sellOrder) {
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
        this.interest = interest;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public OrderEntity getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(OrderEntity buyOrder) {
        this.buyOrder = buyOrder;
    }

    public OrderEntity getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(OrderEntity sellOrder) {
        this.sellOrder = sellOrder;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }
}
