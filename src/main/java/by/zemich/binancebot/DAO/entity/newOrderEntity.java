package by.zemich.binancebot.DAO.entity;

import by.zemich.binancebot.core.dto.FillsDto;
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

@Entity
public class newOrderEntity {

    @Id
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
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private Timestamp transactTime;
    private BigDecimal price;
    private BigDecimal origQty;
    private BigDecimal executedQty;
    private BigDecimal cummulativeQuoteQty;
    private EOrderStatus status;
    private ETimeInForce timeInForce;
    private EOrderType type;
    private ESide side;
    private Timestamp workingTime;
    private String selfTradePreventionMode;
  //  private List<FillsDto> fills;
}
