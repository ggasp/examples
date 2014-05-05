package com.emarsys.e3.api.example;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * BatchMailing forms the primary entry point to the emarsys API.
 * <p>
 * The BatchMailing is a wrapper around the HTTP requests
 * needed in order to communicate with the API.
 *
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class APIClient {

    private final ClientConfiguration config;
    private final URL fieldRequestURL;
    private final URL senderRequestURL;

    private final RESTClient restClient;


    /**
     * Create a new APIClient object.
     *
     * @param config the client configuration config
     */
    public APIClient(ClientConfiguration config) {
        try {
            this.config = config;
            this.fieldRequestURL = new URL(config.getApiBaseURL(), "recipient_fields");
            this.senderRequestURL = new URL(config.getApiBaseURL(), "senders");
            this.restClient = new RESTClient(config);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL.", e);
        }
    }

    /**
     * Creates the batch mailing via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void createBatchMailing(String name) throws IOException {
        this.createMissingSender();
        this.createMissingRecipientFields();

        URL requestURL = new URL(config.getApiBaseURL(), "batch_mailings/" + name);
        Response response = restClient.doPostXML(
            requestURL,
            XMLRequests.createBatchMailingRequest(name, this.config.getLinkDomain())
        );

        if (!Status.SUCCESS_OK.equals(response.getStatus())) {
            throw new APIException("Creation of Batch mailing failed: " + response.getStatus());
        }
    }

    /**
     * Creates the mailing via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void createTransactionalMailing(String name) throws IOException {
        createMissingSender();
        createMissingRecipientFields();

        URL requestURL = getTransactionalMailingURL( name );
        List<RecipientField> recipientFields = config.getFields();
        String fields = "";

        for ( RecipientField recipientField : recipientFields ) {
            fields += "<field name=\"" + recipientField.getName() + "\"/>";
        }

        Response response = restClient.doPostXML(
            requestURL,
            XMLRequests.createTransactionalMailingRequest(name, config.getLinkDomain(), fields)
        );

        if ( !Status.SUCCESS_OK.equals( response.getStatus() ) ) {
            throw new APIException("Creation of TXM failed: " + response.getStatus() );
        }
    }

    /**
     * Creates a new revision of the current content of the transaction mailing.
     *
     * @return the identifier of the newly created revision.
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public int createRevision(String mailingID) throws IOException {
        URL requestURL = new URL(getTransactionalMailingURL( mailingID ), "revisions");
        Response response = this.restClient.doPostXML(requestURL, "");
        String xml = response.getEntity().getText();
        NodeList nodes = parseXml( xml, "//revision/@id" );

        return Integer.parseInt( nodes.item(0).getNodeValue() );
    }


    /**
     * Compares the Fields already in the Account with those we want to use.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private void createMissingRecipientFields() throws IOException {
        List<RecipientField> recipientFields = config.getFields();
        List<String> availableFields = loadAvailableFields();

        for ( RecipientField field :  recipientFields ) {
            if( !availableFields.contains( field.getName() ) ) {
                addField(field.getName(), field.getType());
            }
        }
    }

    /**
     * Compares the Senders already in the Account with the one we want to use.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private void createMissingSender() throws IOException {
        List<String> availableSenders = loadAvailableSenders();
        if ( !availableSenders.contains( config.getSenderId() ) ) {
            addSender();
        }
    }

    /**
     * Loads all fields currently in the account into the class via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private List<String> loadAvailableFields() throws IOException {
        List<String> availableFields = new ArrayList<String>();

        Response response = restClient.doGet(this.fieldRequestURL);
        String xml = response.getEntity().getText();

        NodeList nodes = parseXml( xml, "//field/@name" );
        for (int i = 0; i < nodes.getLength(); i++) {
            availableFields.add(nodes.item(i).getNodeValue());
        }

        if (!Status.SUCCESS_OK.equals(response.getStatus())) {
            throw new APIException("Loading available fields failed: " + response.getStatus());
        }

        return availableFields;
    }

    /**
     * Loads all senders currently in the account into the class via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private List<String> loadAvailableSenders() throws IOException {
        List<String> availableSenders = new ArrayList<String>();

        Response response = restClient.doGet(senderRequestURL);
        String xml = response.getEntity().getText();

        NodeList nodes = parseXml( xml, "//sender/@id" );
        for (int i = 0; i < nodes.getLength(); i++) {
            availableSenders.add(nodes.item(i).getNodeValue());
        }

        if (!Status.SUCCESS_OK.equals(response.getStatus())) {
            throw new APIException("Loading available senders failed: " + response.getStatus());
        }

        return availableSenders;
    }

    /**
     * Posts the recipient list for a transactional mailing via an API call.
     *
     * @throws IOException if some IO error occurs
     */
    public void postTransactionalRecipients( String name, int revision, File recipientFile )
        throws IOException
    {
        postRecipients(
            new URL(getTransactionalMailingURL( name ), "revisions/" + revision + "/recipients"),
            name,
            recipientFile
        );
    }

    /**
     * Posts the recipient list for a batch mailing via an API call.
     *
     * @throws IOException if some IO error occurs
     */
    public void postBatchRecipients( String name, File recipientFile )
        throws IOException
    {
        postRecipients(
            new URL(getBatchMailingURL( name ), "recipients"),
            name,
            recipientFile
        );
    }

    /**
     * Finishes the recipient list for a batch mailing via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    public void finishBatchRecipients( String name ) throws IOException {
        URL requestURL = new URL(getBatchMailingURL( name ), "recipients/status?status=Finished");
        Response response = this.restClient.doPostXML(requestURL, "");

        if( !Status.SUCCESS_OK.equals( response.getStatus() ) ) {
            throw new APIException("Finishing recipients list failed: " + response.getStatus());
        }
    }

    /**
     * Posts the recipient list via an API call to a specific URL.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private void postRecipients( URL requestURL, String name, File recipientFile )
        throws IOException
    {
        String recipients = FileUtils.readFileToString(recipientFile, "UTF-8");
        Response response = this.restClient.doPost(requestURL, recipients, MediaType.TEXT_CSV);

        if ( !Status.SUCCESS_OK.equals( response.getStatus() ) ) {
            throw new APIException("Adding recipients list failed: " + response.getStatus());
        }
    }

    /**
     * Adds fields via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private void addField(String name, String type) throws IOException {
        Response response = restClient.doPostXML(
            fieldRequestURL,
            XMLRequests.addFieldRequest(name, type)
        );

        if (!Status.SUCCESS_OK.equals(response.getStatus())) {
            throw new APIException("Adding field " + name + " failed: " +response.getStatus());
        }
    }

    /**
     * Adds senders via an API call.
     *
     * @throws APIException if the status code of the response != 200
     * @throws IOException if some IO error occurs
     */
    private void addSender() throws IOException {
        Response response = restClient.doPutXML(
            new URL(senderRequestURL, config.getSenderId()),
            XMLRequests.addSenderRequest(config.getSenderName(), config.getSenderAddress())
        );

        if ( !Status.SUCCESS_OK.equals(response.getStatus()) ) {
            throw new APIException(
                "Adding sender " + config.getSenderId() + " failed: " + response.getStatus()
            );
        }
    }

    /**
     * XPath parser for the xml responses
     */
    private NodeList parseXml( String xml, String XPExpression ) throws APIException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader reader = new StringReader( xml );
            InputSource inputSource = new InputSource( reader );
            Document doc = db.parse( inputSource );
            reader.close();

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();
            XPathExpression xpe = xp.compile( XPExpression );
            return (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new APIException("Failed to parse the given XML.", e);
        }
    }

    private URL getBatchMailingURL(String name) throws MalformedURLException {
        return new URL(config.getApiBaseURL(), "batch_mailings/" + name);
    }

    private URL getTransactionalMailingURL(String name) throws MalformedURLException {
        return new URL(config.getApiBaseURL(), "transactional_mailings/" + name);
    }

}
