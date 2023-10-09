package by.zemich.binancebot.service.converter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class LocalDateTimeToMilliFormatter implements Formatter<LocalDateTime> {
    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
            long milliseconds = Long.parseLong(text);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.toString();
    }
}
