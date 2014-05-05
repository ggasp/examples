package com.emarsys.e3.api.example;

import static java.lang.System.out;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfirmationMailExample implements a simple demo client for the emarsys Confirmation Mailing API.
 *
 * The configuration of the client is done via the Properties file 'client.props' and
 * the recipient data is read from 'recipients.csv' and 'recipients2.csv'.
 *
 * @author Alexander Kraml <kraml@emarsys.com>
 */
public final class ConfirmationMailExample {

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

            out.println( "Starting the emarsys API Confirmation Mailing example. pwd:" + new File(".").getAbsolutePath() );

            ConfirmationMailExample example;

            example = new ConfirmationMailExample( "ConfirmationMailExample" + System.currentTimeMillis() );

            out.println( "Creating a transactional mailing..." );
            TransactionalMailing mailing = TransactionalMailing.create(example.name, example.config);

            out.println( "Publishing a new revision..." );
            mailing.publish();

            out.println( "Posting first group of recipients..." );
            mailing.postRecipients( example.config.getLocalRecipientFile( 1 ) );

            out.println( "Posting another group of recipients..." );
            mailing.postRecipients( example.config.getLocalRecipientFile( 2 ) );

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
