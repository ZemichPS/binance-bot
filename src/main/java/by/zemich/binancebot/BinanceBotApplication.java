package by.zemich.binancebot;


import by.zemich.binancebot.config.properties.PrivateProperties;
import by.zemich.binancebot.config.properties.TelegramProperties;
import by.zemich.binancebot.config.properties.TestTradingProperties;
import by.zemich.binancebot.config.properties.TradeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
		PrivateProperties.class,
		TelegramProperties.class,
		TradeProperties.class,
		TestTradingProperties.class})
@SpringBootApplication
public class BinanceBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BinanceBotApplication.class, args);
	}

}
