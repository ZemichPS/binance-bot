package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.core.dto.BarDto;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.converter.Converter;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class StringResponseToListOfBaseBarConverter implements Converter<String, List<BaseBar>> {
    @Override
    public List<BaseBar> convert(String source) {

        List<BaseBar> baseBarList = new ArrayList<>();


        List<Object> objectList = new JacksonJsonParser().parseList(source);

        objectList.stream().forEach(object -> {
            List<Object> rawCandleList = (List<Object>) object;

            Long openTimeTimestamp = Long.valueOf(rawCandleList.get(0).toString());
            Long closeTimeTimestamp = Long.valueOf(rawCandleList.get(6).toString());

            ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(closeTimeTimestamp), ZoneId.systemDefault());
            ZonedDateTime beginTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(openTimeTimestamp), ZoneId.systemDefault());
            Duration timePeriod = Duration.between(beginTime, endTime);
            Num openPrice = DecimalNum.valueOf(rawCandleList.get(1).toString());
            Num closePrice = DecimalNum.valueOf(rawCandleList.get(4).toString());
            Num highPrice = DecimalNum.valueOf(rawCandleList.get(2).toString());
            Num lowPrice = DecimalNum.valueOf(rawCandleList.get(3).toString());
            Num amount = DecimalNum.valueOf(rawCandleList.get(7).toString());
            Num volume = DecimalNum.valueOf(rawCandleList.get(5).toString());
            long trades = Long.valueOf(rawCandleList.get(8).toString());

            BaseBar baseBar = BaseBar.builder()
                    .timePeriod(timePeriod)
                    .endTime(endTime)
                    .openPrice(openPrice)
                    .closePrice(closePrice)
                    .highPrice(highPrice)
                    .lowPrice(lowPrice)
                    .amount(amount)
                    .volume(volume)
                    .trades(trades)
                    .build();

            baseBarList.add(baseBar);
        });

        return baseBarList;
    }
}
