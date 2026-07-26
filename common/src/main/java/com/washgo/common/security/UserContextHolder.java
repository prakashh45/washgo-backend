package com.washgo.common.security;

public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * Store the current authenticated user.
     */
    public static void setContext(UserContext userContext) {
        CONTEXT.set(userContext);
    }

    /**
     * Get the current authenticated user.
     */
    public static UserContext getContext() {
        return CONTEXT.get();
    }

    /**
     * Remove the current user after the request completes.
     * Prevents ThreadLocal memory leaks.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}