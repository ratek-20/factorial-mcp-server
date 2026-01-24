package com.mcp.factorialmcpserver.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonConfigTest {

    @Test
    void testObjectMapperBean() {
        JacksonConfig jacksonConfig = new JacksonConfig();
        ObjectMapper objectMapper = jacksonConfig.objectMapper();

        assertNotNull(objectMapper);
        assertTrue(objectMapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getCanonicalName())
                || objectMapper.getRegisteredModuleIds().stream().anyMatch(id -> id.toString().contains("jackson-datatype-jsr310")));
    }

    @Test
    void testJavaTimeModuleFunctionality() throws Exception {
        JacksonConfig jacksonConfig = new JacksonConfig();
        ObjectMapper objectMapper = jacksonConfig.objectMapper();

        LocalDateTime now = LocalDateTime.now();
        String json = objectMapper.writeValueAsString(now);
        assertNotNull(json);

        LocalDateTime deserialized = objectMapper.readValue(json, LocalDateTime.class);
        assertNotNull(deserialized);
    }
}
