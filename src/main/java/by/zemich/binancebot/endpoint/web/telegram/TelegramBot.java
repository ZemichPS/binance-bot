package by.zemich.binancebot.endpoint.web.telegram;

import by.zemich.binancebot.config.properties.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class TelegramBot extends TelegramLongPollingBot
{
   private final TelegramProperties properties;

    public TelegramBot(TelegramProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return "MTCCurrencyBot";
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }


}
