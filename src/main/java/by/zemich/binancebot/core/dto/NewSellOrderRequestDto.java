package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.ETimeInForce;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class NewSellOrderRequestDto {
    private String assetSymbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private ETimeInForce timeInForce;

}
