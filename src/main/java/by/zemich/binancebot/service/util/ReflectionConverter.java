package by.zemich.binancebot.service.util;

import by.zemich.binancebot.service.api.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
@Component("reflectionConverter")
public class ReflectionConverter implements Converter {
    @Override
    public Map<String, Object> dtoToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(object) == null) continue;

                if (field.getType().isEnum()) {
                    map.put(field.getName(), field.get(object).toString());
                } else map.put(field.getName(), field.get(object));

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

}
