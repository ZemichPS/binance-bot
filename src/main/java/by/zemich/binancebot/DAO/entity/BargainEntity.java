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

    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_create")
    private Timestamp dtCreate;
    @Version
    @UpdateTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_update")
    private Timestamp dtUpdate;

    @OneToMany(targetEntity = OrderEntity.class,
            cascade = CascadeType.REFRESH,
            fetch = FetchType.EAGER,
            mappedBy = "bargain",
            orphanRemoval = true)
    //@JoinColumn(name = "uuid")
    private List<OrderEntity> orders;

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


    public BargainEntity(UUID uuid,
                         Timestamp dtUpdate,
                         Timestamp dtCreate,
                         BigDecimal percentageResult,
                         BigDecimal financeResult,
                         Long timeInWork,
                         Timestamp finishTime,
                         EBargainStatus status,
                         String symbol,
                         BigDecimal currentResult,
                         BigDecimal currentPercentageResult) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.percentageResult = percentageResult;
        this.financeResult = financeResult;
        this.timeInWork = timeInWork;
        this.finishTime = finishTime;
        this.status = status;
        this.symbol = symbol;
        this.currentFinanceResult = currentResult;
        this.currentPercentageResult = currentPercentageResult;
    }

    public BargainEntity() {
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

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
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

    @Override
    public String toString() {
        return "BargainEntity{" +
                "uuid=" + uuid +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                ", orders=" + orders +
                ", percentageResult=" + percentageResult +
                ", financeResult=" + financeResult +
                ", timeInWork=" + timeInWork +
                ", finishTime=" + finishTime +
                ", status=" + status +
                ", symbol='" + symbol + '\'' +
                ", currentResult=" + currentFinanceResult +
                ", currentPercentageResult=" + currentPercentageResult +
                '}';
    }
}
