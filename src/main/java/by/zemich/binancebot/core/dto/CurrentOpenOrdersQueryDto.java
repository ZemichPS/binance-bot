package by.zemich.binancebot.core.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CurrentOpenOrdersQueryDto {
    private Long recvWindow;
}
