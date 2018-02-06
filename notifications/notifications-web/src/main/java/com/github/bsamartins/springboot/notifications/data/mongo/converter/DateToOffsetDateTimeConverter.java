package com.github.bsamartins.springboot.notifications.data.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

public class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(Date source) {
        return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), systemDefault());
    }
}
