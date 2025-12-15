package com.team.goldenturtle.common.config;

import com.team.goldenturtle.gamesession.command.application.service.listener.SessionExpireListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final SessionExpireListener sessionExpireListener;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Redis TTL 만료 이벤트
        container.addMessageListener(
                sessionExpireListener,
                new PatternTopic("__keyevent@0__:expired")
        );

        return container;
    }
}
