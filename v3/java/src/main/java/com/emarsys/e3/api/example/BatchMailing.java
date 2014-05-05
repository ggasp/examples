package com.emarsys.e3.api.example;

import java.io.File;
import java.io.IOException;

/**
 * BatchMailing forms the primary entry point to the emarsys API.
 * <p>
 * The BatchMailing is a wrapper around the HTTP requests
 * needed in order to communicate with the API.
 *
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class BatchMailing {

    private final String name;
    private final APIClient apiClient;

    private BatchMailing(String name, ClientConfiguration config) {
        this.name = name;
        this.apiClient = new APIClient(config);
    }

    /**
     * Creates the batch mailing via an API call.
     *
     * @param name  the unique id of the batch mailing
     * @param config the config
     * @return a new batch-mailing with the given name
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public static BatchMailing create(String name, ClientConfiguration config) throws IOException {
        BatchMailing mailing = new BatchMailing(name, config);
        mailing.apiClient.createBatchMailing(name);
        return mailing;
    }

    /**
     * Transfers the content of the passed file (the recipients) via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void transferRecipients(File recipientFile) throws IOException {
        apiClient.postBatchRecipients( name, recipientFile );
    }

    /**
     * Closes (finishes) the current list of recipients.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void finishRecipientTransfer() throws IOException {
        apiClient.finishBatchRecipients( name );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + name;
    }
}
