package com.emarsys.e3.api.example;

import org.restlet.Response;
import org.restlet.data.Status;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.lang.Object;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

/**
 * BatchMailing forms the primary entry point to the emarsys API.
 * <p/>
 * The BatchMailing is a wrapper around the HTTP requests
 * needed in order to communicate with the API.
 * <p/>
 *
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class BatchMailing {

    //constructor params
    private final String name;
    //state
    private boolean created = false;

    //client subsystems
    private final APIClient apiClient;

    /**
     * Constructor.
     *
     * @param name   - the unique id of the batch mailing
     * @param config - the config
     */
    public BatchMailing(String name, ClientConfiguration config) {

        this.name = name;
        this.apiClient = new APIClient(config);
    }

    /**
     * Creates the batch mailing via an API call.
     *
     * @throws APIException
     */
    public void create() throws APIException {

        apiClient.createBatchMailing(name);
        created = true;
    }

    /**
     * Transfers the content of the passed file (the recipients) via an API call.
     *
     * @throws APIException
     * @throws IOException
     */
    public void transferRecipientList(String recipientFile) throws APIException, IOException {
        apiClient.postBatchRecipients( this.name, new File( recipientFile ) );
    }

    /**
     * Closes (finishes) the current list of recipients.
     *
     * @throws APIException
     * @throws IOException
     */
    public void finishRecipientList() throws APIException, IOException {
        apiClient.finishBatchRecipients( this.name );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.name;
    }
}//class BatchMailing
