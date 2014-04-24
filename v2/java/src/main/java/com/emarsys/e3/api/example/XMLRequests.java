package com.emarsys.e3.api.example;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import org.joda.time.format. DateTimeFormat;
import org.joda.time.DateTime;

import java.lang.String;
import java.text.MessageFormat;


/**
 * XMLRequests provides the XML templates for the REST requests to the emarsys API.
 * </p>
 * Dynamic content is inserted via MessageFormat.format(...).
 *
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
final class XMLRequests {

    private static final String header1 =       "<h1>Header 1</h1>";
    private static final String header2 =       "<h1>Header 2</h1>";
    private static final String otherHeader =   "<h1>Other Header</h1>";

    private static final String footer1 =       "<div>footer1</div>";
    private static final String footer2 =       "<div>footer2</div>";
    private static final String otherFooter =   "<div>other footer</div>";

    private static final String subject1 =       "subject1";
    private static final String subject2 =       "subject2";
    private static final String otherSubject =   "other subject";

    private static final String htmlContent =
       "<html>" +
         "<head>" +
           "<title>$$RCPT_DOMAIN$$</title>" +
           "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></meta>" +
         "</head>" +
         "<body>" +
            "##HEADER##" +
            "<p>This is a test with a <a href=\"http://www.emarsys.com\">link</a></p>" +
            "##FOOTER##" +
         "</body>" +
        "</html>";

    private static final String textContent = "This is the test text content for transactional mail.";

    private static final String confirmationMailRequest =
         "<mailing>" +
            "<!-- These properties can be defaulted on the account, and do not need to be specified each time. -->" +
            "<properties>" +
                "<property key=\"Sender\">default</property>" +
                "<property key=\"Language\">en</property>" +
                "<property key=\"Encoding\">iso-8859-1</property>" +
                "<property key=\"Domain\">{10}</property>" +
            "</properties>" +
            "<recipientFields>" +
                "{11}" +
            "</recipientFields>" +
            "<subject>Emarsys API Test (##subject##)</subject>" +
            "<html>" +
                "{0}" + // html content
            "</html>" +
            "<text>" +
            "{12}" +
            "</text>" +
            "<conditions>" +
                "<condition id=\"HEADER\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 = \"en\") and (not(RCPT_TYPE_v2 in (1,2,3,4)))]]></when>" +
                            "<html>{1}</html>" +  // header1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 = \"de\") and (RCPT_TYPE_v2 in (1,2,3,4))]]></when>" +
                            "<html>{2}</html>" + // header2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<html>{3}</html>" + // otherHeader
                    "</otherwise>" +
                "</condition>" +
                "<condition id=\"FOOTER\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 contains \"n\") and (RCPT_TYPE_v2 < 1)]]></when>" +
                            "<html>{4}</html>" +  // footer1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 contains \"d\") and (RCPT_TYPE_v2 > 1)]]></when>" +
                            "<html>{5}</html>" + // footer2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<html>{6}</html>" + // otherFooter
                    "</otherwise>" +
                "</condition>" +
                "<condition id=\"SUBJECT\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(RCPT_TYPE_v2 equals 0)]]></when>" +
                            "<text>{7}</text>" +  // subject1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(RCPT_TYPE_v2 equals 4)]]></when>" +
                            "<text>{8}</text>" + // subject2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<text>{9}</text>" + // otherSubject
                    "</otherwise>" +
                "</condition>" +
            "</conditions>" +
        "</mailing>";

    private static final String newsletterExampleRequest =
        "<batch>" +
            "<!-- This is the identifier for the batch mailing. -->" +
            "<name>{0}</name>" + // batch name
            "<!--" +
                "When the broadcast is planned. For production broadcasts of over 1 million, please update the" +
                "content with at least 1-2 hours of lead time." +
            "-->" +
            "<runDate>{1}</runDate>" +// run date
            "<!-- These properties can be defaulted on the account, and do not need to be specified each time. -->" +
            "<properties>" +
                "<property key=\"Sender\">default</property>" +
                "<property key=\"Language\">en</property>" +
                "<property key=\"Encoding\">iso-8859-1</property>" +
                "<property key=\"Domain\">{12}</property>" +
                "<property key=\"ImportDelay\">1</property>" +
                "<property key=\"IncludeHeader\">false</property>" +
            "</properties>" +
            "<subject>Emarsys API Test (##subject##)</subject>" +
            "<html>" +
                "{2}" + // html content
            "</html>" +
            "<conditions>" +
                "<condition id=\"HEADER\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 = \"en\") and (not(RCPT_TYPE_v2 in (1,2,3,4)))]]></when>" +
                            "<html>{3}</html>" +  // header1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 = \"de\") and (RCPT_TYPE_v2 in (1,2,3,4))]]></when>" +
                            "<html>{4}</html>" + // header2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<html>{5}</html>" + // otherHeader
                    "</otherwise>" +
                "</condition>" +
                "<condition id=\"FOOTER\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 contains \"n\") and (RCPT_TYPE_v2 < 1)]]></when>" +
                            "<html>{6}</html>" +  // footer1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(LANGUAGE_v2 contains \"d\") and (RCPT_TYPE_v2 > 1)]]></when>" +
                            "<html>{7}</html>" + // footer2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<html>{8}</html>" + // otherFooter
                    "</otherwise>" +
                "</condition>" +
                "<condition id=\"SUBJECT\">" +
                    "<cases>" +
                        "<case>" +
                            "<when><![CDATA[(RCPT_TYPE_v2 equals 0)]]></when>" +
                            "<text>{9}</text>" +  // subject1
                        "</case>" +
                        "<case>" +
                            "<when><![CDATA[(RCPT_TYPE_v2 equals 4)]]></when>" +
                            "<text>{10}</text>" + // subject2
                        "</case>" +
                    "</cases>" +
                    "<otherwise>" +
                        "<text>{11}</text>" + // otherSubject
                    "</otherwise>" +
                "</condition>" +
            "</conditions>" +
        "</batch>";



    /**
     * Returns the XML document which forms the body of the HTTP POST request
     * in order to create a mailing as a String.
     *
     * @param  mailing name
     * @return always a valid XML document as String, never null
     */

    static String createTransactionalMailingRequest( String name, String domain, String recipientFields ) {
        return MessageFormat.format(
            confirmationMailRequest,      // XML template
            escapeHtml( htmlContent ),    // {0}
            escapeHtml( header1 ),        // {1}
            escapeHtml( header2 ),        // {2}
            escapeHtml( otherHeader ),    // {3}
            escapeHtml( footer1 ),        // {4}
            escapeHtml( footer2 ),        // {5}
            escapeHtml( otherFooter ),    // {6}
            escapeHtml( subject1 ),       // {7}
            escapeHtml( subject2 ),       // {8}
            escapeHtml( otherSubject ),   // {9}
            escapeHtml( domain ),         // {10}
            escapeHtml( recipientFields ),// {11}
            escapeHtml( textContent )   // {12}
        );
    }

    /**
     * Returns the XML document which forms the body of the HTTP POST request
     * in order to create a batch mailing as a String.
     *
     * @param batchName
     * @return always a valid XML document as String, never null
     */
    static String createBatchMailingRequest( String batchName, String domain ) {
        return MessageFormat.format(
            newsletterExampleRequest,    // XML template
            batchName,                   // {0}
            getRunDate(),                // {1}
            escapeHtml( htmlContent ),   // {2}
            escapeHtml( header1 ),       // {3}
            escapeHtml( header2 ),       // {4}
            escapeHtml( otherHeader ),   // {5}
            escapeHtml( footer1 ),       // {6}
            escapeHtml( footer2 ),       // {7}
            escapeHtml( otherFooter ),   // {8}
            escapeHtml( subject1 ),      // {9}
            escapeHtml( subject2 ),      // {10}
            escapeHtml( otherSubject ),  // {11}
            escapeHtml( domain )         // {12}
        );
    }

    /**
     * Creates a properly formatted timestamp for the run date of the BatchMailing.
     *
     * @return a String representing a run date 5 mins in the future.
     */
    private static String getRunDate() {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").print( new DateTime().plusMinutes(5) );
    }

    private static final String triggerImportXML =
        "<importRequest>" +
            "<filePath>{0}</filePath>" + //recipients file
            "<properties>" +
                "<property key=\"Delimiter\">,</property>" +
                "<property key=\"Encoding\">UTF-8</property>" +
            "</properties>" +
        "</importRequest>";

    /**
     *
     * @param recipientsFile
     * @return always a valid XML document as String, never null
     */
    static String triggerImportRequest( String recipientsFile )
    {
        return MessageFormat.format( triggerImportXML, recipientsFile );
    }

    private static final String addSenderXML =
        "<sender>" +
            "<name>{0}</name>" +
            "<address>{1}</address>" +
        "</sender>";

    /**
     *
     * @param name, address
     * @return always a valid XML document as String, never null
     */
    static String addSenderRequest( String name, String address )
    {
        return MessageFormat.format( addSenderXML, name, address );
    }

    private static final String addFieldXML =
        "<fields>" +
            "<field name=\"{0}\" type=\"{1}\" />" +
        "</fields>";

    /**
     *
     * @param name, address
     * @return always a valid XML document as String, never null
     */
    static String addFieldRequest( String name, String type )
    {
        return MessageFormat.format( addFieldXML, name, type );
    }

}//class XMLRequests
