package com.emarsys.e3.api.example;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;


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
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class APIClient {

    //constructor params
    private final ClientConfiguration config;

    //eagerly evaluated values
    private final String fieldRequestURL;
    private final String senderRequestURL;

    //client subsystems
    private final RESTClient restClient;


    /**
     * Constructor.
     *
     * @param config - the config
     */
    public APIClient(ClientConfiguration config) {

        this.config = config;
        this.fieldRequestURL = this.config.getApiBaseURL() + "recipient_fields";
        this.senderRequestURL = this.config.getApiBaseURL() + "senders";
        this.restClient = new RESTClient(config);
    }


    protected void fireFailedRequest(String msg, Status status) throws APIException {
        throw new APIException(this + ": " + msg + ", status " + status);
    }

    protected void fireFailedRequest(String msg, Exception ex) throws APIException {
        throw new APIException(this + ": " + msg + ": " + ex.getMessage(), ex);
    }

    /**
     * Creates the batch mailing via an API call.
     *
     * @throws APIException
     */
    public void createBatchMailing(String name) throws APIException {
        try {

            this.createMissingSender();
            this.createMissingRecipientFields();

            String requestURL = this.config.getApiBaseURL() + "batch_mailings/" + name;
            Response response = this.restClient.doPost(
                requestURL,
                XMLRequests.createBatchMailingRequest(name, this.config.getLinkDomain())
            );

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully created " + name);
            } else {
                fireFailedRequest("failed batch request", response.getStatus());
            }

        } catch (IOException ex) {
            fireFailedRequest("failed batch request!", ex);
        }
    }

    /**
     * Creates the mailing via an API call.
     *
     * @throws APIException
     */
    public void createTransactionalMailing(String name) throws APIException {
        try {

            this.createMissingSender();
            this.createMissingRecipientFields();

            String requestURL = getTransactionalMailingURL( name );
            List<RecipientField> recipientFields = this.config.getFields();
            String fields = "";

            for( RecipientField recipientField : recipientFields ) {
                fields += "<field name=\"" + recipientField.getName() + "\"/>";
            }

            Response response = this.restClient.doPost(
                requestURL,
                XMLRequests.createTransactionalMailingRequest( name, this.config.getLinkDomain(), fields )
            );

            if ( Status.SUCCESS_OK.equals( response.getStatus() ) ) {
                out.println( "successfully created " + this );
            } else {
                throw new APIException( this + ": failed request: " + response.getStatus() );
            }
        } catch (IOException ex) {
            throw new APIException( this + ": failed request: " + ex.getMessage(), ex );
        }
    }

    /**
     * Creates a new revision of the current content of the transaction mailing
     *
     * @return the identifier of the newly created revision.
     */
    public int createRevision(String mailingID) throws APIException {
        try {
            String requestURL = getTransactionalMailingURL( mailingID ) + "/revisions";
            Response response = this.restClient.doPost( requestURL, "" );
            String xml = response.getEntity().getText();
            NodeList nodes = parseXml( xml, "//revision/@id" );

            return Integer.valueOf( nodes.item(0).getNodeValue() ).intValue();
        }
        catch (IOException ex) {
            throw new APIException( this + ": failed request: " + ex.getMessage(), ex );
        }
    }


    /**
     * Compares the Fields already in the Account with those we want to use
     *
     * @throws APIException
     */
    private void createMissingRecipientFields() throws APIException {
        List<RecipientField> recipientFields = this.config.getFields();
        List<String> availableFields = loadAvailableFields();

        for ( RecipientField field :  recipientFields ) {
            if( !availableFields.contains( field.getName() ) ) {
                this.addField( field.getName(), field.getType() );
            }
        }
    }

    /**
     * Compares the Senders already in the Account with the one we want to use
     *
     * @throws APIException
     */
    private void createMissingSender() throws APIException {
        List<String> availableSenders = loadAvailableSenders();
        if ( !availableSenders.contains( this.config.getSenderId() ) ) {
            this.addSender();
        }
    }

    /**
     * Loads all fields currently in the account into the class via an API call.
     *
     * @throws APIException
     */
    private List<String> loadAvailableFields() throws APIException {
        List<String> availableFields = new ArrayList<String>();

        try {
            Response response = this.restClient.doGet(this.fieldRequestURL);
            String xml = response.getEntity().getText();

            NodeList nodes = parseXml( xml, "//field/@name" );

            for (int i = 0; i < nodes.getLength(); i++) {
                availableFields.add(nodes.item(i).getNodeValue().toString());
            }

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully loaded available fields");
            } else {
                fireFailedRequest("failed to load available fields", response.getStatus());
            }
        }catch (Exception ex) {
            fireFailedRequest("failed to load recipient fields: " + ex, ex);
        }

        return availableFields;
    }

    /**
     * Loads all senders currently in the account into the class via an API call.
     *
     * @throws APIException
     */
    private List<String> loadAvailableSenders() throws APIException {
        List<String> availableSenders = new ArrayList<String>();

        try {
            Response response = this.restClient.doGet(this.senderRequestURL);
            String xml = response.getEntity().getText();

            NodeList nodes = parseXml( xml, "//sender/@id" );

            for (int i = 0; i < nodes.getLength(); i++) {
                availableSenders.add(nodes.item(i).getNodeValue().toString());
            }

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully loaded available senders");
            } else {
                fireFailedRequest("failed to load available senders", response.getStatus());
            }
        }catch (Exception ex) {
            fireFailedRequest("failed to load senders: " + ex, ex);
        }

        return availableSenders;
    }

    /**
     * Posts the recipient list for a transactional mailing via an API call.
     *
     * @throws APIException
     * @throws IOException
     */
    public void postTransactionalRecipients( String name, int revision, File recipientFile ) throws APIException, IOException {

        postRecipients( 
            getTransactionalMailingURL( name ) + "/revisions/" + revision + "/recipients",
            name,
            recipientFile
        );
    }

    /**
     * Posts the recipient list for a batch mailing via an API call.
     *
     * @throws APIException
     * @throws IOException
     */
    public void postBatchRecipients( String name, File recipientFile ) throws APIException, IOException {

        postRecipients( 
            getBatchMailingURL( name ) + "/recipients",
            name,
            recipientFile
        );
    }

    /**
     * Finishes the recipient list for a batch mailing via an API call.
     *
     * @throws APIException
     * @throws IOException
     */
    public void finishBatchRecipients( String name ) throws APIException, IOException {

        String requestURL = getBatchMailingURL( name ) + "/recipients/status?status=Finished";
        Response response = this.restClient.doPost( requestURL, "" );
        if( Status.SUCCESS_OK.equals( response.getStatus() ) ) {
            out.println( "Finished the recipient list.");
        }
    }

    /**
     * Posts the recipient list via an API call to a specific URL.
     *
     * @throws APIException
     * @throws IOException
     */
    private void postRecipients( String requestURL, String name, File recipientFile ) throws APIException, IOException {

        String recipients = getRecipients( recipientFile );
        Response response = this.restClient.doPost( requestURL, recipients );

        if( Status.SUCCESS_OK.equals( response.getStatus() ) ) {
            out.println( "Posted recipients:");
            out.println(recipients);
        }
    }

    private String getRecipients(File file) throws IOException {
        BufferedReader br = new BufferedReader( new FileReader( file ) );

        StringBuilder sb = new StringBuilder();
        String line = null;

        while ( ( line = br.readLine() ) != null ) {
            sb.append( line + "\n" );
        }
        String recipients = sb.toString();

        br.close();

        return recipients;
    }

    /**
     * Adds fields via an API call.
     *
     * @throws APIException
     */
    private void addField(String name, String type) throws APIException {
        try {
            Response response = this.restClient.doPost(
                    this.fieldRequestURL,
                    XMLRequests.addFieldRequest(name, type)
            );

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully added field" + name);
            } else {
                fireFailedRequest("failed to add field", response.getStatus());
            }
        }catch (IOException ex) {
            fireFailedRequest("failed to add field!", ex);
        }
    }

    /**
     * Adds senders via an API call.
     *
     * @throws APIException
     */
    private void addSender() throws APIException {
        try {
            Response response = this.restClient.doPut(
                    this.senderRequestURL + "/" + this.config.getSenderId(),
                    XMLRequests.addSenderRequest(this.config.getSenderName(), this.config.getSenderAddress())
            );

            if (Status.SUCCESS_OK.equals(response.getStatus())) {
                out.println("successfully created sender" + this.config.getSenderName());
            } else {
                fireFailedRequest("failed to add sender", response.getStatus());
            }
        } catch (IOException ex) {
            fireFailedRequest("failed to add sender!", ex);
        }
    }

    /**
     * XPath parser for the xml responses
     *
     * @param xml
     * @param XPExpression
     * @return NodeList
     * @throws APIException
     */
    private NodeList parseXml( String xml, String XPExpression ) throws APIException {
        NodeList nodes = null;

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
            nodes = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);
            return nodes;
        } catch (Exception ex) {
            fireFailedRequest("failed to parse the given XML", ex);
        }

        return nodes;
    }

    private String getBatchMailingURL(String name) {
        return this.config.getApiBaseURL() + "batch_mailings/" + name;
    }

    private String getTransactionalMailingURL(String name) {
        return this.config.getApiBaseURL() + "transactional_mailings/" + name;
    }

}//class APIClient
