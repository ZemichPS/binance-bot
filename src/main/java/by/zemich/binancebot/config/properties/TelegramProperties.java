package by.zemich.binancebot.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
