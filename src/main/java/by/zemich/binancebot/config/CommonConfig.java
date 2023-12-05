package by.zemich.binancebot.config;

import by.zemich.binancebot.core.dto.binance.SymbolDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommonConfig {

    @Bean
    List<SymbolDto> getSymbolsList() {
        return new ArrayList<SymbolDto>();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES), new JavaTimeModule())
                .deserializers(new NumberDeserializers.BigDecimalDeserializer())
                .featuresToEnable(MapperFeature.AUTO_DETECT_CREATORS)
                .build();
    }


}
