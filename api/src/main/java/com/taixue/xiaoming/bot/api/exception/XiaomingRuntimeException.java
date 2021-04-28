package com.taixue.xiaoming.bot.api.exception;

public class XiaomingRuntimeException extends RuntimeException {
    private final String message;

    public XiaomingRuntimeException() {
        this.message = null;
    }

    public XiaomingRuntimeException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
