package by.zemich.binancebot.core;

import by.zemich.binancebot.core.dto.binance.Asset;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NewBuyOrderRequestDto {
    private Asset assetForBuying;
    private BigDecimal deposit;
}
