package by.zemich.binancebot.core.dto.binance;

import lombok.*;

import java.util.OptionalLong;


@Builder
public class KlineQueryDto {
    private String symbol;
    private String interval;
    private Long startTime;
    private Long endTime;
    private Integer limit;

    public KlineQueryDto() {
    }

    public KlineQueryDto(String symbol, String interval, Long startTime, Long endTime, Integer limit) {
        this.symbol = symbol;
        this.interval = interval;
        this.startTime = startTime;
        this.endTime = endTime;
        this.limit = limit;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
