package com.xck.config;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

/**
 * @author xuchengkun
 * @date 2022/03/19 23:58
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(JSONNull.class, new JsonSerializer<JSONNull>(){
            @Override
            public void serialize(JSONNull jsonNull, JsonGenerator jsonGenerator
                    , SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeNull();
            }
        });
        simpleModule.addDeserializer(JSONNull.class, new JsonDeserializer<JSONNull>() {
            @Override
            public JSONNull deserialize(JsonParser jsonParser
                    , DeserializationContext deserializationContext) {
                return null;
            }
        });
        return new ObjectMapper().registerModule(simpleModule);
    }
}
