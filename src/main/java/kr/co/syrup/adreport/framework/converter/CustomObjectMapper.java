package kr.co.syrup.adreport.framework.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by jino on 2017. 2. 18..
 */
@Component
@Primary
public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        setSerializationInclusion(Include.NON_EMPTY);
        setSerializationInclusion(Include.NON_NULL);

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }
}
