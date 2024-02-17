package by.zemich.binancebot.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Configuration
public class ModelMapperConfiguration {


    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        Converter<Timestamp, LocalDateTime> timestampLocalDateTimeConverter = new AbstractConverter<>() {
            @Override
            protected LocalDateTime convert(Timestamp timestamp) {
                return timestamp == null ? null : timestamp.toLocalDateTime();
            }
        };


        Converter<LocalDateTime, Timestamp> localDateTimeTimestampConverter = new AbstractConverter<>() {

            @Override
            protected Timestamp convert(LocalDateTime localDateTime) {
                return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
            }
        };

        modelMapper.addConverter(timestampLocalDateTimeConverter);
        modelMapper.addConverter(localDateTimeTimestampConverter);

        return modelMapper;
    }
}
