package com.emarsys.e3.api.example;

/**
 * A simple helper class representing a recipient related field.
 */
public class RecipientField {
    private String name;
    private String type = "TEXT";

    public RecipientField (String[] info) {
        this.name = info[0];
        if ( info.length > 1 ) {
            this.type = info[1];
        }
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
