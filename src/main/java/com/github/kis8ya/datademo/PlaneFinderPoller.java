package com.github.kis8ya.datademo;

import com.github.kis8ya.datademo.model.Aircraft;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@Component
public class PlaneFinderPoller {
    private final WebClient client = WebClient.create("http://localhost:7634/aircraft");

    private final RedisConnectionFactory connectionFactory;
    private final RedisOperations<String, Aircraft> redisOperations;

    public PlaneFinderPoller(
            RedisConnectionFactory connectionFactory,
            RedisOperations<String, Aircraft> redisOperations
    ) {
        this.connectionFactory = connectionFactory;
        this.redisOperations = redisOperations;
    }

    @Scheduled(fixedRate = 3000)
    private void poll() {
        connectionFactory.getConnection().serverCommands().flushDb();

        client.get()
                .retrieve()
                .bodyToFlux(Aircraft.class)
                .filter(aircraft -> !aircraft.getReg().isEmpty())
                .toStream()
                .forEach(aircraft -> redisOperations.opsForValue().set(aircraft.getReg(), aircraft));

        redisOperations.opsForValue()
                .getOperations()
                .keys("*")
                .forEach(key -> System.out.println(redisOperations.opsForValue().get(key)));
    }
}