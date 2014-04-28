package com.emarsys.e3.api.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * ConfirmationMailExample implements a simple demo client for the emarsys Confirmation Mailing API.
 *
 * The configuration of the client is done via the Properties file 'client.props' and
 * the recipient data is read from 'recipients.csv' and 'recipients2.csv'.
 *
 * @author Alexander Kraml <kraml@emarsys.com>
 */
public final class ConfirmationMailExample {

    /**
     * PropertiesClientConfig provides the ClientConfiguration for the ConfirmationMailExample
     * based on the passed Properties.
     *
     * @author Michael Kulovits <kulovits@emarsys.com>
     */
    private class PropertiesClientConfig implements ClientConfiguration {

        private final Properties props;

        private PropertiesClientConfig( Properties props ) {
            this.props = props;
        }

        @Override
        public String getApiUsername() {
            return this.props.getProperty( "apiUsername" );
        }

        @Override
        public String getApiPasswordHash() {
            return this.props.getProperty( "apiPasswordHash" );
        }

        @Override
        public String getApiBaseURL() {
            return this.props.getProperty( "apiBaseUrl" );
        }

        @Override
        public String getLinkDomain() {
            return this.props.getProperty( "linkDomain" );
        }

        @Override
        public String getSenderId() {
            return this.props.getProperty( "senderId" );
        }

        @Override
        public String getSenderName() {
            return this.props.getProperty( "senderName" );
        }

        @Override
        public String getSenderAddress() {
            return this.props.getProperty( "senderAddress" );
        }

        @Override
        public String getLocalRecipientFile(int num) {
            String fileName = num == 1 ? "localRecipientFile" : "localRecipientFile" + num;
            return this.props.getProperty( fileName );
        }

        @Override
        public List<RecipientField> getFields() {
            List<RecipientField> fields = new ArrayList<RecipientField>();

            String [] fields_information = this.props.getProperty( "fields" ).split(",");
            for (String information : fields_information) {
                String [] info = information.split(":");
                fields.add( new RecipientField( info ) );
            }
            return fields;
        }
    }//class PropertiesClientConfig

    // members
    private String name;
    private ClientConfiguration config;

    /**
     * Private Constructor.
     *
     * @param name
     * @throws IOException
     */
    private ConfirmationMailExample( String name ) throws IOException {
        Properties props = new Properties();
        props.load( new FileReader( "config.props" ) );

        this.config = new PropertiesClientConfig( props );
        this.name = name;
    }

    /**
     * Main method.
     * <p/>
     * Pass the name of the Batch Mailing as the first command line parameter
     * otherwise a batch name will be generated.
     * @param args
     */
    public static void main(String[] args) {
        try {

            out.println( "starting the emarsys API Confirmation Mailing example. pwd:" + new File(".").getAbsolutePath() );

            ConfirmationMailExample example;

            example = new ConfirmationMailExample( "ConfirmationMailExample" + System.currentTimeMillis() );

            out.println( "Creating a mailing." );

            TransactionalMailing mailing = new TransactionalMailing( example.name, example.config );
            mailing.create();

            mailing.publish();

            out.println( "Posting first group of recipients." );
            mailing.postRecipients( example.config.getLocalRecipientFile( 1 ) );

            out.println( "Posting second group of recipients." );
            mailing.postRecipients( example.config.getLocalRecipientFile( 2 ) );


        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
