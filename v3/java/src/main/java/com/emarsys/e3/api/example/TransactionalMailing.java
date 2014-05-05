package com.emarsys.e3.api.example;

import java.io.File;
import java.io.IOException;

/**
 * TransactionalMailing forms the primary entry point to the emarsys API.
 * <p>
 * The TransactionalMailing is a wrapper around the HTTP requests
 * needed in order to communicate with the API.
 *
 * Currently the following functions are supported:
 * <ol>
 *     <li>{@link #create(String,ClientConfiguration) create the mailing}</li>
 *     <li>{@link #postRecipients(File)} post recipients to created a mailing}</li>
 * </ol>
 * <p/>
 * @author Oleksandr Kylymnychenko <oleksandr.kylymnychenko@emarsys.com>
 */
public class TransactionalMailing {

    private final String name;
    private final APIClient apiClient;
    private int revision = -1;

    private TransactionalMailing( String name, ClientConfiguration config ) {

        this.name = name;
        this.apiClient = new APIClient(config);
    }

    /**
     * Creates the mailing via an API call.
     *
     * @param name the unique id of the mailing
     * @param config the config
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public static TransactionalMailing create(
        String name,
        ClientConfiguration config
    ) throws IOException {
        TransactionalMailing mailing = new TransactionalMailing( name, config );
        mailing.apiClient.createTransactionalMailing(name);
        return mailing;
    }

    /**
     * Creates a new revision (publishes the actual content).
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void publish() throws IOException {
        revision = apiClient.createRevision(name);
    }

    /**
     * Posts the recipient list via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void postRecipients( File recipientFile ) throws IOException {

        if( revision < 0 ) throw new IllegalStateException( "No revision created, publish the mailing first" );
        else {
            apiClient.postTransactionalRecipients(name, revision, recipientFile );
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + this.name;
    }
}
