package com.emarsys.e3.api.example;

import java.io.IOException;

/**
 * Indicates errors during the communication with the emarsys API.
 *
 * @author Oleksandr Kylymnychenko
 */
public class APIException extends IOException {

    public APIException(String s) {
        super(s);
    }

    public APIException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
