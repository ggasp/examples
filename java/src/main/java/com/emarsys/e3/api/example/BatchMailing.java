package com.emarsys.e3.api.example;

import org.restlet.data.Response;
import org.restlet.data.Status;

import java.io.IOException;

import static java.lang.System.out;

/**
 * BatchMailing forms the primary entry point to the emarsys BMAPI.
 * <p/>
 * The BatchMailing is a wrapper around the HTTP and SFTP requests
 * needed in order to communicate with the BMAPI.
 * <p/>
 * Currently the following functions are supported:
 * <ol>
 *     <li>{@link #create() create the batch mailing}</li>
 *     <li>{@link #transferRecipientData()}  transfer the recipients import file}</li>
 *     <li>{@link #triggerImport() trigger import}</li>
 * </ol>
 * <p/>
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class BatchMailing {

    //constructor params
    private final String name;
    private final ClientConfiguration config;

    //eagerly evaluated values
    private final String batchRequestURL;
    private final String importRequestURL;

    //state
    private boolean created = false;

    //client subsystems
    private final RESTClient restClient;
    private final RecipientDataTransferer dataTransferer;


    /**
     * Constructor.
     *
     * @param name   - the unique id of the batch mailing
     * @param config - the config
     */
    public BatchMailing(String name, ClientConfiguration config) {
        this.name = name;
        this.config = config;

        this.batchRequestURL =
                this.config.getBMAPIBaseURL() + "/batches/" + this.name;
        this.importRequestURL =
                this.batchRequestURL + "/import";

        this.restClient = new RESTClient(config);
        this.dataTransferer = new RecipientDataTransferer(this.config, this.name);
    }


    protected void fireFailedRequest(String msg, Status status) throws BMAPIException {
        throw new BMAPIException(this + ": " + msg + ", status " + status);
    }

    protected void fireFailedRequest(String msg, Exception ex) throws BMAPIException {
        throw new BMAPIException(this + ": " + msg + ": " + ex.getMessage(), ex);
    }

    /**
     * Creates the batch mailing via an API call.
     *
     * @throws BMAPIException
     */
    public void create() throws BMAPIException {
        if (this.created) {
            throw new IllegalStateException(
                    "cannot create " + this + " twice!");
        }

        try {

            Response response = this.restClient.postRequest(
                    this.batchRequestURL,
                    XMLRequests.createBatchRequest(this.name, this.config.getDomain()));

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                this.created = true;
                out.println("successfully created " + this);
            } else {
                fireFailedRequest("failed batch request", response.getStatus());
            }

        } catch (IOException ex) {
            fireFailedRequest("failed batch request!", ex);
        }
    }

    /**
     * Transfers the batch's corresponding recipient data via SFTP.
     *
     * @throws IOException
     */
    public void transferRecipientData() throws IOException {
        this.dataTransferer.transferRecipientData(this.config.getLocalRecipientsFilePath());

        out.println("transferred recipient data for " + this);
    }

    /**
     * Triggers the import process via an HTTP call.
     *
     * @throws BMAPIException
     */
    public void triggerImport() throws BMAPIException {
        if (!this.created) {
            throw new IllegalArgumentException(
                    "must not trigger import for non-existent " + this + "!");
        }

        try {

            Response response = this.restClient.postRequest(
                    this.importRequestURL,
                    XMLRequests.triggerImportRequest(this.dataTransferer.getRemoteFileName()));

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully triggered import for " + this);
            } else {
                fireFailedRequest("failed to trigger import", response.getStatus());
            }
        } catch (IOException ioe) {
            fireFailedRequest("failed import request", ioe);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.name;
    }
}//class BatchMailing
