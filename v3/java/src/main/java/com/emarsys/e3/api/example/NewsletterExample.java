package com.emarsys.e3.api.example;

import static java.lang.System.out;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * NewsletterExample implements a simple demo client for the emarsys Batch Mailing API.
 *
 * The configuration of the client is done via the Properties file 'client.props' and
 * the recipient data is read from 'recipients.csv'.
 * <p/>
 * See {@link BatchMailing} for the detailed logic needed in order to set up and trigger a
 * batch mailing but basically it's solely three action needed:
 * <ol>
 *     <li>create the batch mailing</li>
 *     <li>transfer the recipients</li>
 * </ol>
 * </p>
 * The NewsletterExample currently does not contain any processing of results, i.e.
 * id does not handle exports. Please refer to the documentation for those kind of use cases.
 *
 * @author Alexander Kraml <kraml@emarsys.com>
 */
public final class NewsletterExample {

    private String batchName;
    private ClientConfiguration config;

    /**
     * Private Constructor.
     *
     * @param batchName
     * @throws IOException
     */
    private NewsletterExample( String batchName ) throws IOException {
        Properties props = new Properties();
        props.load( new FileReader( "config.props" ) );

        this.config = new PropertiesClientConfig( props );
        this.batchName = batchName;
    }

    public static void main(String[] args) {
        try {

            out.println( "Starting emarsys BMAPI newsletter example. pwd:" + new File(".").getAbsolutePath() );

            NewsletterExample example;
            example = new NewsletterExample("BatchExample" + System.currentTimeMillis());

            out.println("Creating batch mailing...");
            BatchMailing batchMailing = BatchMailing.create(example.batchName, example.config);

            out.println("Transferring recipient list...");

            File recipientFile = example.config.getLocalRecipientFile(1);
            out.println("Recipients:\n" + FileUtils.readFileToString( recipientFile ));

            // The method can be called multiple time, with different recipient
            // files. This allows to transfer the recipients when they are available.
            batchMailing.transferRecipients( recipientFile );


            // When all recipients have been transferred, the recipient-transfer
            // MUST be finished.
            out.println("Finishing recipient-transfer...");
            batchMailing.finishRecipientTransfer();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
