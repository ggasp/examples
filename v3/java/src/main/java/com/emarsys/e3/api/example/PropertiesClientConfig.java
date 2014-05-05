package com.emarsys.e3.api.example;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * PropertiesClientConfig provides the ClientConfiguration for the ConfirmationMailExample
 * based on the passed Properties.
 *
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
class PropertiesClientConfig implements ClientConfiguration {

    private final Properties props;

    PropertiesClientConfig(Properties props) {
        this.props = props;
    }

    @Override
    public String getApiUsername() {
        return props.getProperty( "apiUsername" );
    }

    @Override
    public String getApiPasswordHash() {
        return props.getProperty( "apiPasswordHash" );
    }

    @Override
    public URL getApiBaseURL() {
        try {
            return new URL( props.getProperty( "apiBaseUrl" ) );
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException( e );
        }
    }

    @Override
    public String getLinkDomain() {
        return props.getProperty( "linkDomain" );
    }

    @Override
    public String getSenderId() {
        return props.getProperty( "senderId" );
    }

    @Override
    public String getSenderName() {
        return props.getProperty( "senderName" );
    }

    @Override
    public String getSenderAddress() {
        return props.getProperty( "senderAddress" );
    }

    @Override
    public File getLocalRecipientFile(int num) {
        String fileName = num == 1 ? "localRecipientFile" : "localRecipientFile" + num;
        return new File( props.getProperty( fileName ) );
    }

    @Override
    public List<RecipientField> getFields() {
        List<RecipientField> fields = new ArrayList<RecipientField>();

        for (String information : props.getProperty( "fields" ).split(",")) {
            fields.add( RecipientField.create( information ) );
        }
        return fields;
    }
}
