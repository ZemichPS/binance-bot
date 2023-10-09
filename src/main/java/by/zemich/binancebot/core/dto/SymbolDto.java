package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EOrderType;
import lombok.Data;
import java.util.List;

@Data
public class SymbolDto {
    private String symbol;
    private String status;
    private String baseAsset;
    private Integer baseAssetPrecision;
    private String quoteAsset;
    private Integer quoteAssetPrecision;
    private List<EOrderType> orderTypes;
    private boolean icebergAllowed;
    private boolean ocoAllowed;
    private boolean quoteOrderQtyMarketAllowed;
    private boolean allowTrailingStop;
    private boolean cancelReplaceAllowed;
    private boolean isSpotTradingAllowed;
    private boolean isMarginTradingAllowed;

    private List<String> permissions;
    private String defaultSelfTradePreventionMode;
    private List<String> allowedSelfTradePreventionModes;

}
