package com.washgo.common.security;

public final class GatewayConstants {

    private GatewayConstants() {
    }

    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Key";

    public static final String USER_ID_HEADER = "X-User-Id";

    public static final String FIREBASE_UID_HEADER = "X-Firebase-Uid";

    public static final String USER_ROLE_HEADER = "X-User-Role";
}