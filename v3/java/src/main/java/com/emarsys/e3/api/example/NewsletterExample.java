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
 * NewsletterExample implements a simple demo client for the emarsys Batch Mailing API.
 * <p/>
 * The actual mail generation and sending process is triggerd in {@link #sendNewsletter()}.
 * <p/>
 * The configuration of the client is done via the Properties file 'client.props' and
 * the recipient data is read from 'recipients.csv'.
 * <p/>
 * See {@link BatchMailing} for the detailed logic needed in order to set up and trigger a
 * batch mailing but basically it's solely three action needed:
 * <ol>
 *     <li>create the batch mailing</li>
 *     <li>transfer the recipients import file</li>
 *     <li>trigger import</li>
 * </ol>
 * </p>
 * The NewsletterExample currently does not contain any processing of results, i.e.
 * id does not handle exports. Please refer to the documentation for those kind of use cases.
 *
 * @author Alex Kraml <kraml@emarsys.com>
 */
public final class NewsletterExample {

    /**
     * PropertiesClientConfig provides the ClientConfiguration for the NewsletterExample
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
            return this.props.getProperty( "localRecipientFile" );
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

    /**
     * Main method.
     * <p/>
     * Pass the name of the Batch Mailing as the first command line parameter
     * otherwise a batch name will be generated.
     * @param args
     */
    public static void main(String[] args) {
        try {

            out.println( "Starting emarsys BMAPI newsletter example. pwd:" + new File(".").getAbsolutePath() );

            NewsletterExample example;
            example = new NewsletterExample("BatchExample" + System.currentTimeMillis());

            out.println("creating batch mailing!");

            BatchMailing batchMailing = new BatchMailing(example.batchName, example.config);
            batchMailing.create();
            batchMailing.transferRecipientList(example.config.getLocalRecipientFile(0));
            batchMailing.finishRecipientList();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}//class NewsletterExample
