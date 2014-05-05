package com.emarsys.e3.api.example;

/**
 * A simple helper class representing a recipient related field.
 */
public class RecipientField {
    private final String name;
    private final String type;

    public RecipientField (String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static RecipientField create( String value ) {

        String[] info = value.split(":");

        return new RecipientField(
            info[0],
            (info.length > 1) ? info[1] : "TEXT"
        );
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
