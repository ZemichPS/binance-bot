package by.zemich.binancebot.core.dto;

import lombok.Data;

@Data
public class CurrentOpenOrdersDto {
    private Long recvWindow;
    private Long timestamp;
}
