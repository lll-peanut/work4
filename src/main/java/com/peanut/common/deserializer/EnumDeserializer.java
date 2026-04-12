package com.peanut.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * @author: peanut
 * @date: 2026/3/30
 * @version:1.0
 */
public class EnumDeserializer<T extends Enum<T>> extends StdDeserializer<T> {
    private final Class<T> enumType;

    public EnumDeserializer(Class<T> vc) {
        super(vc);
        this.enumType = vc;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text != null) {
            for (T constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(text)) {
                    return constant;
                }
            }
        }
        // 保持原Jackson提示
        return (T) ctxt.handleWeirdStringValue(enumType, text,
                "Unknown enum value. Allowed values: %s", java.util.Arrays.asList(enumType.getEnumConstants()));
    }
}