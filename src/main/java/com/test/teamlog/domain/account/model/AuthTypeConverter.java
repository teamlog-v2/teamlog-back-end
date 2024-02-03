package com.test.teamlog.domain.account.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class AuthTypeConverter implements AttributeConverter<AuthType, String> {
    @Override
    public String convertToDatabaseColumn(AuthType authType) {
        return authType != null ? authType.name() : null;
    }

    @Override
    public AuthType convertToEntityAttribute(String name) {
        if (name == null) return null;

        return Stream.of(AuthType.values())
                .filter(c -> c.name().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
