package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.dto.NewOrderFullResponseDto;
import by.zemich.binancebot.core.enums.EOrderStatus;
import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ESide;
import by.zemich.binancebot.core.enums.ETimeInForce;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    private String symbol;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "order_list_id")
    private Long orderListId;
    @Column(name = "client_order_id")
    private String clientOrderId;
    @Column(name = "transact_time")
    private Timestamp transactTime;
    private BigDecimal price;
    private BigDecimal stopPrice;
    @Column(name = "orig_qty")
    private BigDecimal origQty;
    @Column(name = "executed_qty")
    private BigDecimal executedQty;
    @Column(name = "cummulative_quote_qty")
    private BigDecimal cummulativeQuoteQty;
    @Enumerated(EnumType.STRING)
    private EOrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_in_force")
    private ETimeInForce timeInForce;
    @Enumerated(EnumType.STRING)
    private EOrderType type;
    @Enumerated(EnumType.STRING)
    private ESide side;
    @Column(name = "is_working")
    private boolean isWorking;
    @Column(name = "working_time")
    private Timestamp workingTime;
    @Column(name = "self_trade_prevention_mode")
    private String selfTradePreventionMode;


    //  private List<FillsDto> fills;


    public OrderEntity(UUID uuid, Timestamp dtUpdate, Timestamp dtCreate, String symbol, Long orderId, Long orderListId, String clientOrderId, Timestamp transactTime, BigDecimal price, BigDecimal stopPrice, BigDecimal origQty, BigDecimal executedQty, BigDecimal cummulativeQuoteQty, EOrderStatus status, ETimeInForce timeInForce, EOrderType type, ESide side, boolean isWorking, Timestamp workingTime, String selfTradePreventionMode) {
        this.uuid = uuid;
        this.dtUpdate = dtUpdate;
        this.dtCreate = dtCreate;
        this.symbol = symbol;
        this.orderId = orderId;
        this.orderListId = orderListId;
        this.clientOrderId = clientOrderId;
        this.transactTime = transactTime;
        this.price = price;
        this.stopPrice = stopPrice;
        this.origQty = origQty;
        this.executedQty = executedQty;
        this.cummulativeQuoteQty = cummulativeQuoteQty;
        this.status = status;
        this.timeInForce = timeInForce;
        this.type = type;
        this.side = side;
        this.isWorking = isWorking;
        this.workingTime = workingTime;
        this.selfTradePreventionMode = selfTradePreventionMode;
    }

    public OrderEntity() {
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderListId() {
        return orderListId;
    }

    public void setOrderListId(Long orderListId) {
        this.orderListId = orderListId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public Timestamp getTransactTime() {
        return transactTime;
    }

    public void setTransactTime(Timestamp transactTime) {
        this.transactTime = transactTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    public BigDecimal getOrigQty() {
        return origQty;
    }

    public void setOrigQty(BigDecimal origQty) {
        this.origQty = origQty;
    }

    public BigDecimal getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(BigDecimal executedQty) {
        this.executedQty = executedQty;
    }

    public BigDecimal getCummulativeQuoteQty() {
        return cummulativeQuoteQty;
    }

    public void setCummulativeQuoteQty(BigDecimal cummulativeQuoteQty) {
        this.cummulativeQuoteQty = cummulativeQuoteQty;
    }

    public EOrderStatus getStatus() {
        return status;
    }

    public void setStatus(EOrderStatus status) {
        this.status = status;
    }

    public ETimeInForce getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(ETimeInForce timeInForce) {
        this.timeInForce = timeInForce;
    }

    public EOrderType getType() {
        return type;
    }

    public void setType(EOrderType type) {
        this.type = type;
    }

    public ESide getSide() {
        return side;
    }

    public void setSide(ESide side) {
        this.side = side;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public Timestamp getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Timestamp workingTime) {
        this.workingTime = workingTime;
    }

    public String getSelfTradePreventionMode() {
        return selfTradePreventionMode;
    }

    public void setSelfTradePreventionMode(String selfTradePreventionMode) {
        this.selfTradePreventionMode = selfTradePreventionMode;
    }
}
