package by.zemich.binancebot.DAO.entity;

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
import java.util.Objects;
import java.util.UUID;

@Entity(name = "orders")
public class OrderEntity {

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
    private String symbol;
    @Column(name = "binance_order_id")
    private Long orderId;
    @Column(name = "order_list_id")
    private Long orderListId;
    @Column(name = "client_order_id")
    private String clientOrderId;
    @Column(name = "transact_time")
    private Timestamp transactTime;
    private BigDecimal price;
    @Column(name = "stop_price")
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
    @Column(name = "bargain_uuid")
    @ManyToOne(cascade = CascadeType.REFRESH)
    private BargainEntity bargain;


}
