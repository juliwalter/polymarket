package com.tschayjay.base.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author julianwalter
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
            .withZone(ZoneId.systemDefault());

    public static String format(Instant timestamp) {
        return FORMATTER.format(timestamp);
    }
}
