package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.core.dto.BarDto;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.converter.Converter;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BarDtoToBaseBarConverter implements Converter<BarDto, BaseBar> {
    @Override
    public BaseBar convert(BarDto source) {
        ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.getCloseTime().getTime()), ZoneId.systemDefault());
        ZonedDateTime beginTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.getOpenTime().getTime()), ZoneId.systemDefault());
        Duration timePeriod = Duration.between(beginTime, endTime);


        return BaseBar.builder(DecimalNum::valueOf, Number.class)
                .timePeriod(Duration.ofMinutes(15))
                .endTime(endTime)
                .openPrice(source.getOpenPrice())
                .highPrice(source.getHighPrice())
                .lowPrice(source.getLowPrice())
                .closePrice(source.getClosePrice())
                .volume(source.getVolume())
                .trades(source.getNumberOfTrades().longValue())
                .build();
    }
}

/*
    public BaseBar convert(BarDto source) {
        ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.closeTime().getTime()), ZoneId.systemDefault());
        ZonedDateTime beginTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.openTime().getTime()), ZoneId.systemDefault());
        Duration timePeriod = Duration.between(beginTime, endTime);

        return BaseBar.builder()
                .timePeriod(timePeriod)
                .endTime(endTime)
                .openPrice(DecimalNum.valueOf(source.openPrice()))
                .closePrice(DecimalNum.valueOf(source.closePrice()))
                .highPrice(DecimalNum.valueOf(source.highPrice()))
                .lowPrice(DecimalNum.valueOf(source.lowPrice()))
                .amount(DecimalNum.valueOf(0))
                .volume(DecimalNum.valueOf(source.volume()))
                .trades(source.numberOfTrades().longValue())
                .build();
    }*/