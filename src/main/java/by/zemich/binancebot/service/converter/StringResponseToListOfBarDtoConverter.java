package by.zemich.binancebot.service.converter;

import by.zemich.binancebot.core.dto.BarDto;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class StringResponseToListOfBarDtoConverter implements Converter<String, List<BarDto>> {
    @Override
    public List<BarDto> convert(String source) {
        List<BarDto> barDtos = new ArrayList<>();
        List<Object> objectList = new JacksonJsonParser().parseList(source);

        objectList.stream().forEach(object -> {
            List<Object> rawCandleList = (List<Object>) object;

            Long openTimeTimestamp = Long.valueOf(rawCandleList.get(0).toString());
            Long closeTimeTimestamp = Long.valueOf(rawCandleList.get(6).toString());

            barDtos.add(
                    new BarDto(
                            Instant.ofEpochMilli(openTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            new BigDecimal(rawCandleList.get(1).toString()),
                            new BigDecimal(rawCandleList.get(2).toString()),
                            new BigDecimal(rawCandleList.get(3).toString()),
                            new BigDecimal(rawCandleList.get(4).toString()),
                            new BigDecimal(rawCandleList.get(5).toString()),
                            Instant.ofEpochMilli(closeTimeTimestamp).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            new BigDecimal(rawCandleList.get(7).toString()),
                            (Integer) rawCandleList.get(8),
                            new BigDecimal(rawCandleList.get(9).toString()),
                            new BigDecimal(rawCandleList.get(10).toString())
                    )
            );

        });

        return barDtos;
    }
}
