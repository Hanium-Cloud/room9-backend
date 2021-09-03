package com.goomoong.room9backend.exception;

public class NoCommunityAuthException extends RuntimeException {
    public NoCommunityAuthException(String message) {
        super(message);
    }
}
