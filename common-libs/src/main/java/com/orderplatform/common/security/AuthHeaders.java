package com.orderplatform.common.security;

/**
 * Identity headers set by the gateway after JWT validation.
 * Downstream services trust these headers, so the gateway must strip
 * any client-supplied header starting with {@link #PREFIX}.
 */
public final class AuthHeaders {

    public static final String PREFIX = "X-Auth-";

    public static final String USER_ID = PREFIX + "UserId";
    public static final String EMAIL = PREFIX + "Email";
    public static final String ROLE = PREFIX + "Role";

    private AuthHeaders() {
    }
}
