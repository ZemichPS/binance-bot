package by.zemich.binancebot.config;

import by.zemich.binancebot.core.dto.binance.SymbolDto;
import by.zemich.binancebot.service.api.IStockMarketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommonConfig {

    @Bean
    List<SymbolDto> getSymbolsList() {
        return new ArrayList<SymbolDto>();
    }

}
