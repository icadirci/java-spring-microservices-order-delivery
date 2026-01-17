package com.orderplatform.common.event;

public record UserRegisteredEvent(
        String eventId,
        Long userId,
        String email,
        String fullName
) {
}
