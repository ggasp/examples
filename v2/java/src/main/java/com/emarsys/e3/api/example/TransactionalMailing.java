package com.emarsys.e3.api.example;

import org.restlet.data.Response;
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
import java.io.FileNotFoundException;
import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.lang.String;

import static java.lang.System.out;

/**
 * TransactionalMailing forms the primary entry point to the emarsys API.
 * <p/>
 * The TransactionalMailing is a wrapper around the HTTP requests
 * needed in order to communicate with the API.
 * <p/>
 * Currently the following functions are supported:
 * <ol>
 *     <li>{@link #create() create the mailing}</li>
 *     <li>{@link #postRecipients()} post recipients to created a mailing}</li>
 * </ol>
 * <p/>
 * @author Oleksandr Kylymnychenko <oleksandr.kylymnychenko@emarsys.com>
 */
public class TransactionalMailing {

    private final String name;
    private final APIClient apiClient;
    private int revision = -1;
    private boolean created = false;
    
    /**
     * Constructor.
     *
     * @param name   - the unique id of the mailing
     * @param config - the config
     */
    public TransactionalMailing( String name, ClientConfiguration config ) {

        this.name = name;
        this.apiClient = new APIClient(config);
    }

    /**
     * Creates the mailing via an API call.
     *
     * @throws APIException
     */
    public void create() throws APIException {

        apiClient.createTransactionalMailing(name);
    }

    public void publish() throws APIException {

        revision = apiClient.createRevision(name);
    }

    /**
     * Posts the recipient list via an API call.
     *
     * @throws APIException
     */
    public void postRecipients( String recipientFile ) throws APIException, IOException {

        if( revision < 0 ) throw new IllegalStateException( "No revision created, publish the mailing first" );
        else {
            try {

                apiClient.postRecipients(name, revision, new File( recipientFile ));

            } catch( FileNotFoundException fe ) {

                out.println( "Specify a correct file with recipients.: " +fe.getMessage() );
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.name;
    }
}