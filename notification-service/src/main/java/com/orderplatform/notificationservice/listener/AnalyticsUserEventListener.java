package com.orderplatform.notificationservice.listener;

import com.orderplatform.common.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsUserEventListener {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsUserEventListener.class);

    @RabbitListener(queues = "analytics.user.registered")
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info(
                "[ANALYTICS] user registered: {}",
                event.userId()
        );
    }
}
