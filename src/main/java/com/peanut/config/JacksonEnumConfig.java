package com.peanut.config;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.peanut.common.deserializer.EnumDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: peanut
 * @date: 2026/3/30
 * @version:1.0
 */
@Configuration
public class JacksonEnumConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer enumCustomizer() {
        return builder -> {
            // 注册一个枚举反序列化策略（对所有的枚举类都适用）
            builder.deserializerByType(Enum.class, new EnumDeserializer(Enum.class));
            // 定制Module（让所有具体Enum sub-type也生效）
            builder.modulesToInstall(caseInsensitiveEnumModule());
        };
    }

    public SimpleModule caseInsensitiveEnumModule() {
        SimpleModule module = new SimpleModule();
        // 这里使用泛型，有特殊的类型自动触发
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyEnumDeserializer(
                    DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                Class<?> raw = type.getRawClass();
                if (Enum.class.isAssignableFrom(raw)) {
                    return new EnumDeserializer(raw);
                }
                return deserializer;
            }
        });
        return module;
    }
}