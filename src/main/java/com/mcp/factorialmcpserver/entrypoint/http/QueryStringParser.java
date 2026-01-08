package com.mcp.factorialmcpserver.entrypoint.http;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class QueryStringParser {

    private static final String QUERY_PARAMS_SEPARATOR = "&";
    private static final char KEY_VALUE_SEPARATOR = '=';

    public Map<String, String> parse(String rawQuery) {
        if (Objects.isNull(rawQuery) || rawQuery.isBlank()) {
            return Map.of();
        }
        return Stream.of(rawQuery.split(QUERY_PARAMS_SEPARATOR))
                .filter(keyValueQueryParam -> !keyValueQueryParam.isBlank())
                .map(this::splitPair)
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private Optional<Map.Entry<String, String>> splitPair(String pair) {
        final int equalsPosition = pair.indexOf(KEY_VALUE_SEPARATOR);
        final String rawKey = (equalsPosition >= 0) ? pair.substring(0, equalsPosition) : pair;
        final String rawValue = (equalsPosition >= 0) ? pair.substring(equalsPosition + 1) : "";
        final String key = decode(rawKey);
        return key.isBlank()
                ? Optional.empty()
                : Optional.of(new AbstractMap.SimpleImmutableEntry<>(key, decode(rawValue)));
    }

    private String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}

