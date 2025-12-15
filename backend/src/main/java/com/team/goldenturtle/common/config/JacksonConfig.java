package com.team.goldenturtle.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 타입(LocalDateTime 등) 처리를 위해 모듈 등록 (선택사항이지만 추천)
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}