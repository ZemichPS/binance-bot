package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EBargainStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BargainDto {
    private UUID uuid;
    private String strategy;
    private LocalDateTime dtCreate;
    private LocalDateTime dtUpdate;
    private BigDecimal percentageResult;
    private BigDecimal financeResult;
    private Long timeInWork;
    private Timestamp finishTime;
    private EBargainStatus status;
    private String symbol;
    private BigDecimal interest;
    private BigDecimal fee;
    private List<OrderDto> orders;
}
