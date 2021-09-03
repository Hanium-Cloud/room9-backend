package com.goomoong.room9backend.exception;

public class NoSuchPostException extends RuntimeException{
    public NoSuchPostException(String massage){
        super(massage);
    }
}
