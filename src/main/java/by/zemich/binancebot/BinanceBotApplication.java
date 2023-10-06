package by.zemich.binancebot;

import by.zemich.binancebot.config.properties.PrivateProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({PrivateProperties.class})
@SpringBootApplication
public class BinanceBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BinanceBotApplication.class, args);
	}

}
