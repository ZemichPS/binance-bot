package by.zemich.binancebot.core.dto.binance;

import by.zemich.binancebot.core.enums.EOrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class Asset {
    private String symbol;
    private String status;
    private String baseAsset;
    private Integer baseAssetPrecision;
    private String quoteAsset;
    private Integer quotePrecision;
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
    private List<Object> filters;

    public PriceFilter getPriceFilter(){
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) getFilters().get(0);

        return PriceFilter.builder()
                .filterType(map.get("filterType"))
                .minPrice(new BigDecimal(map.get("minPrice")))
                .maxPrice(new BigDecimal(map.get("maxPrice")))
                .tickSize(new BigDecimal(map.get("tickSize")))
                .build();
    }

    public LotSizeFilter getLotSizeFilter(){
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) getFilters().get(1);

        return LotSizeFilter.builder()
                .filterType(map.get("filterType"))
                .minQty(new BigDecimal(map.get("minQty")))
                .maxQty(new BigDecimal(map.get("maxQty")))
                .stepSize(new BigDecimal(map.get("stepSize")))
                .build();
    }





}
