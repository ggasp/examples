package com.emarsys.e3.api.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

        public int getScpPort() {
            return new Integer( this.props.getProperty( "scpPort" ) );
        }

        public String getScpRemoteHost() {
            return this.props.getProperty( "scpRemoteHost" );
        }

        public String getScpRemoteRootDirectory() {
            return this.props.getProperty( "scpRemoteRootDir" );
        }

        public String getLocalRecipientsFilePath() {
            return this.props.getProperty( "localRecipientsFilePath" );
        }

        public String getBMAPIUsername() {
            return this.props.getProperty( "bmapiUsername" );
        }

        public String getBMAPIPasswordHash() {
            return this.props.getProperty( "bmapiPwdHash" );
        }

        public String getBMAPIBaseURL() {
            return this.props.getProperty( "bmapiBaseUrl" ) + this.props.getProperty( "accountId" );
        }

        public String getScpUsername() {
            return this.props.getProperty( "scpUsername" );
        }

        public String getScpPassword() {
            return this.props.getProperty( "scpPassword" );
        }
        
        public String getDomain() {
            return this.props.getProperty( "domain" );
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
        props.load( new FileReader( "client.props" ) );

        this.config = new PropertiesClientConfig( props );
        this.batchName = batchName;
    }

    /**
     * Sends a newsletter by issueing the following steps:
     * <p/>
     * <ol>
     *     <li>create the batch mailing</li>
     *     <li>transfer the recipients import file</li>
     *     <li>trigger import</li>
     * </ol>
     * <p/>
     * @throws BMAPIException
     * @throws java.io.IOException
     */
    private void sendNewsletter() throws IOException, BMAPIException {
        out.println("creating batch mailing!");

        BatchMailing batchMailing = new BatchMailing(this.batchName, this.config);
        batchMailing.create();
        batchMailing.transferRecipientData();
        batchMailing.triggerImport();
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

            out.println( "starting emarsys BMAPI newsletter example. pwd:" + new File(".").getAbsolutePath() );

            NewsletterExample example;

            if (args.length > 0 && args[0] != null && args[0].length() > 0) {
                example = new NewsletterExample(args[0]);
            } else {
                example = new NewsletterExample("BatchExample" + System.currentTimeMillis());
            }

            example.sendNewsletter();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}//class NewsletterExample
