package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;


public class BargainDto {

    private UUID uuid;

    private Timestamp dtCreate;

    private Timestamp dtUpdate;

    private OrderDto buyOrder;

    private OrderDto sellOrder;

    private BigDecimal percentageResult;

    private BigDecimal financeResult;

    private Long timeInWork;

    private Timestamp finishTime;

    private EBargainStatus status;


    public BargainDto(UUID uuid, Timestamp dtUpdate, Timestamp dtCreate, OrderDto buyOrder, OrderDto sellOrder, BigDecimal percentageResult, BigDecimal financeResult, Long timeInWork, Timestamp finishTime, EBargainStatus status) {
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

    public BargainDto() {
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

    public OrderDto getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(OrderDto buyOrder) {
        this.buyOrder = buyOrder;
    }

    public OrderDto getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(OrderDto sellOrder) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BargainDto that = (BargainDto) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(dtCreate, that.dtCreate) && Objects.equals(dtUpdate, that.dtUpdate) && Objects.equals(buyOrder, that.buyOrder) && Objects.equals(sellOrder, that.sellOrder) && Objects.equals(percentageResult, that.percentageResult) && Objects.equals(financeResult, that.financeResult) && Objects.equals(timeInWork, that.timeInWork) && Objects.equals(finishTime, that.finishTime) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, dtCreate, dtUpdate, buyOrder, sellOrder, percentageResult, financeResult, timeInWork, finishTime, status);
    }


}
