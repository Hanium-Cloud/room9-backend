package com.goomoong.room9backend.exception;

public class NoSuchReplyException extends RuntimeException{
    public NoSuchReplyException(String massage){
        super(massage);
    }
}
