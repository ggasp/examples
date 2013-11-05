package com.emarsys.e3.api.example;

/**
 * Indicates errors during the communication with the emarsys BMAPI.
 *
 * @author Michael Kulovits
 */
public class BMAPIException extends Exception {

    public BMAPIException(String s) {
        super(s);
    }

    public BMAPIException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
