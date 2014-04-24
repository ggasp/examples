package com.emarsys.e3.api.example;

/**
 * Indicates errors during the communication with the emarsys API.
 *
 * @author Oleksandr Kylymnychenko
 */
public class APIException extends Exception {

    public APIException(String s) {
        super(s);
    }

    public APIException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
