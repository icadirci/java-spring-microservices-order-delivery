package com.orderplatform.notificationservice.listener;

import com.orderplatform.common.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class UserEventListener {
    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);
    private final RedisTemplate<String, String> redisTemplate;


    public UserEventListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RabbitListener(queues = "notification.user.registered")
    public void onUserRegistered(UserRegisteredEvent event) {

        if (event.email().endsWith("@fail.com")) {
            log.error("Simulated failure");
            throw new RuntimeException("Simulated failure");
        }

        boolean alreadyProcessed =
                redisTemplate.hasKey("event:" + event.eventId());

        if (alreadyProcessed) {
            log.info("Event already processed: {}", event.eventId());
            return;
        }

        log.info("""
            [USER REGISTERED]
            userId={}
            email={}
            fullName={}
        """, event.userId(), event.email(), event.fullName());
        redisTemplate.opsForValue()
                .set("event:" + event.eventId(), "1", Duration.ofDays(1));
    }
}
