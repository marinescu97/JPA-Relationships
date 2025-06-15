package com.spring.JpaRelationships.exception;

public class UniqueFieldException extends RuntimeException{
    public UniqueFieldException(String message) {
        super(message);
    }
}
