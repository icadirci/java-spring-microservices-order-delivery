package com.orderplatform.common.event;

import java.util.UUID;

public record UserRegisteredEvent(
        String eventId,
        UUID userId,
        String email,
        String fullName
) {
}
