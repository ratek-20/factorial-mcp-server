package com.mcp.factorialmcpserver.entrypoint.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryStringParserTest {
    
    private QueryStringParser parser;

    @BeforeEach
    void setUp() {
        parser = new QueryStringParser();
    }

    @Test
    void itShouldReturnEmptyMapWhenRawQueryIsNull() {
        Map<String, String> parsedParams = parser.parse(null);

        assertTrue(parsedParams.isEmpty());
    }

    @Test
    void itShouldReturnEmptyMapWhenRawQueryIsBlank() {
        Map<String, String> parsedParams = parser.parse("   ");

        assertTrue(parsedParams.isEmpty());
    }

    @Test
    void itShouldReturnParsedMapWhenValidQueryStringIsProvided() {
        String key = "key";
        String value = "value";
        String rawQuery = key + "=" + value;

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals(value, parsedParams.get(key));
    }

    @Test
    void itShouldReturnParsedMapWithMultipleParametersWhenValidQueryStringIsProvided() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        String rawQuery = key1 + "=" + value1 + "&" + key2 + "=" + value2;

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(2, parsedParams.size());
        assertEquals(value1, parsedParams.get(key1));
        assertEquals(value2, parsedParams.get(key2));
    }

    @Test
    void itShouldReturnDecodedValuesWhenEncodedQueryStringIsProvided() {
        String rawQuery = "name=John%20Doe&city=New%20York";

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(2, parsedParams.size());
        assertEquals("John Doe", parsedParams.get("name"));
        assertEquals("New York", parsedParams.get("city"));
    }

    @Test
    void itShouldReturnEmptyValueWhenParameterHasNoEquals() {
        String key = "key";
        String rawQuery = key;

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals("", parsedParams.get(key));
    }

    @Test
    void itShouldReturnEmptyValueWhenParameterHasEqualsButNoValue() {
        String key = "key";
        String rawQuery = key + "=";

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals("", parsedParams.get(key));
    }

    @Test
    void itShouldIgnoreEmptyParametersWhenQueryStringHasMultipleSeparators() {
        String key = "key";
        String value = "value";
        String rawQuery = "&&" + key + "=" + value + "&&";

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals(value, parsedParams.get(key));
    }

    @Test
    void itShouldIgnoreParametersWithBlankKeys() {
        String rawQuery = "=value&key=value2";

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals("value2", parsedParams.get("key"));
    }

    @Test
    void itShouldReturnDecodedKeyWhenKeyIsEncoded() {
        String rawQuery = "first%20name=John";

        Map<String, String> parsedParams = parser.parse(rawQuery);

        assertEquals(1, parsedParams.size());
        assertEquals("John", parsedParams.get("first name"));
    }
}