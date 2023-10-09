package by.zemich.binancebot.config;
import by.zemich.binancebot.service.converter.LocalDateTimeToMilliFormatter;
import by.zemich.binancebot.service.converter.StringResponseToListOfBarDtoConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringResponseToListOfBarDtoConverter());
     //   registry.addFormatter(new LocalDateTimeToMilliFormatter());
    }
}
