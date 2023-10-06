package by.zemich.binancebot.core.dto;

import by.zemich.binancebot.core.enums.EInterval;
import by.zemich.binancebot.core.enums.ESymbol;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class KlineParamsDto {
    private ESymbol symbol;
    private EInterval interval;
    private Long startTime;
    private Long endTime;
    @Max(1000)
    private Integer limit;

    public KlineParamsDto(ESymbol symbol, EInterval interval, Long startTime, Long endTime, Integer limit) {
        this.symbol = symbol;
        this.interval = interval;
        this.startTime = startTime;
        this.endTime = endTime;
        this.limit = limit;
    }

    public KlineParamsDto() {
    }

    public Map<String, Object> getParameterMap() {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("symbol", symbol.name());
        parameterMap.put("interval", interval.toString());

        if (startTime != null) {
            parameterMap.put("startTime", startTime);
        }
        if (endTime != null) {
            parameterMap.put("endTime", endTime);
        }
        if (limit != null) {
            parameterMap.put("limit", limit);
        }

        return parameterMap;
    }


}
