package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.DAO.entity.OrderEntity;
import by.zemich.binancebot.core.enums.EBargainStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class BargainDto {
    private UUID uuid;
    private Timestamp dtCreate;
    private Timestamp dtUpdate;
    private List<OrderDto> orders;
    private BigDecimal percentageResult;
    private BigDecimal financeResult;
    private Long timeInWork;
    private Timestamp finishTime;
    private EBargainStatus status;
    private String symbol;
    private BigDecimal currentFinanceResult;
    private BigDecimal currentPercentageResult;

}
