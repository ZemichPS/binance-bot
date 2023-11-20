package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
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
    @Column(name = "current_finance_result")
    private BigDecimal currentFinanceResult;
    @Column(name = "current_percentage_result")
    private BigDecimal currentPercentageResult;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "uuid")
    private List<OrderEntity> orders;

    public BargainEntity() {
    }

    public BargainEntity(UUID uuid, Timestamp dtCreate, Timestamp dtUpdate, BigDecimal percentageResult, BigDecimal financeResult, Long timeInWork, Timestamp finishTime, EBargainStatus status, String symbol, BigDecimal currentFinanceResult, BigDecimal currentPercentageResult, List<OrderEntity> orders) {
        this.uuid = uuid;
        this.dtCreate = dtCreate;
        this.dtUpdate = dtUpdate;
        this.percentageResult = percentageResult;
        this.financeResult = financeResult;
        this.timeInWork = timeInWork;
        this.finishTime = finishTime;
        this.status = status;
        this.symbol = symbol;
        this.currentFinanceResult = currentFinanceResult;
        this.currentPercentageResult = currentPercentageResult;
        this.orders = orders;
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

    public BigDecimal getCurrentFinanceResult() {
        return currentFinanceResult;
    }

    public void setCurrentFinanceResult(BigDecimal currentFinanceResult) {
        this.currentFinanceResult = currentFinanceResult;
    }

    public BigDecimal getCurrentPercentageResult() {
        return currentPercentageResult;
    }

    public void setCurrentPercentageResult(BigDecimal currentPercentageResult) {
        this.currentPercentageResult = currentPercentageResult;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }
}
