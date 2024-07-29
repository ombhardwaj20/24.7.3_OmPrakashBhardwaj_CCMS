package com.card.Credit_card.exception;

public class LimitExceededException extends Exception{
    public LimitExceededException(String message) {
        super(message);
    }
}
