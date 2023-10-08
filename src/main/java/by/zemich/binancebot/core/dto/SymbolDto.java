package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EOrderType;
import by.zemich.binancebot.core.enums.ETimeInForce;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SymbolDto {
    private String symbol;
    private String pair;
    private String contractType;
    private LocalDateTime deliveryDate;
    private LocalDateTime onboardDate;
    private String status;
    private BigDecimal maintMarginPercent;
    private BigDecimal requiredMarginPercent;
    private String baseAsset;
    private String quoteAsset;
    private String marginAsset;
    private Integer pricePrecision;
    private Integer quantityPrecision;
    private Integer baseAssetPrecision;
    private Integer quotePrecision;
    private String underlyingType;
    private List<String> underlyingSubType;
    private Integer settlePlan;
    private BigDecimal triggerProtect;
    private BigDecimal liquidationFee;
    private BigDecimal marketTakeBound;
    private Integer maxMoveOrderLimit;
    private List<FilterDto> filters;
    private List<EOrderType> orderTypes;
    private List<ETimeInForce> timeInForce;


















}
