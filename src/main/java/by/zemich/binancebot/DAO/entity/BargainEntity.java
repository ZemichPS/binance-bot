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
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "buy_order_uuid", referencedColumnName = "uuid")
    private OrderEntity buyOrder;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sell_order_uuid", referencedColumnName = "uuid")
    private OrderEntity sellOrder;

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


    public BargainEntity(UUID uuid,
                         Timestamp dtUpdate,
                         Timestamp dtCreate,
                         OrderEntity buyOrder,
                         OrderEntity sellOrder,
                         BigDecimal percentageResult,
                         BigDecimal financeResult,
                         Long timeInWork,
                         Timestamp finishTime,
                         EBargainStatus status) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.percentageResult = percentageResult;
        this.financeResult = financeResult;
        this.timeInWork = timeInWork;
        this.finishTime = finishTime;
        this.status = status;
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

    @Override
    public String toString() {
        return "BargainEntity{" +
                "uuid=" + uuid +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                ", buyOrder=" + buyOrder +
                ", sellOrder=" + sellOrder +
                ", percentageResult=" + percentageResult +
                ", financeResult=" + financeResult +
                ", timeInWork=" + timeInWork +
                ", finishTime=" + finishTime +
                ", status=" + status +
                '}';
    }
}
