package com.emarsys.e3.api.example;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import org.joda.time.format. DateTimeFormat;
import org.joda.time.DateTime;
import java.text.MessageFormat;


/**
 * XMLRequests provides the XML templates for the REST requests to the emarsys BMAPI.
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

    private static final String createBatchXML =
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
            "</properties>" +
            "<subject>Emarsys API Test (##subject##)</subject>" +
            "<html>" +
                "<data>{2}</data>" + // html content
            "</html>" +
            "<conditions>" +
                "<condition name=\"HEADER\" type=\"text/html\">" +
                    "<cases>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>" +
                            "<content><data>{3}</data></content>" +  // header1
                        "</case>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>" +
                            "<content><data>{4}</data></content>" + // header2
                        "</case>" +
                    "</cases>" +
                    "<otherwise><data>{5}</data></otherwise>" + // otherHeader
                "</condition>" +
                "<condition name=\"FOOTER\" type=\"text/html\">" +
                    "<cases>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>" +
                            "<content><data>{6}</data></content>" +  // footer1
                        "</case>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>" +
                            "<content><data>{7}</data></content>" + // footer2
                        "</case>" +
                    "</cases>" +
                    "<otherwise><data>{8}</data></otherwise>" + // footer2
                "</condition>" +
                "<condition name=\"SUBJECT\" type=\"text/plain\">" +
                    "<cases>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>" +
                            "<content><data>{9}</data></content>" +  // subject1
                        "</case>" +
                        "<case>" +
                            "<whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>" +
                            "<content><data>{10}</data></content>" + // subject2
                        "</case>" +
                    "</cases>" +
                    "<otherwise><data>{11}</data></otherwise>" + // otherSubject
                "</condition>" +
            "</conditions>" +
        "</batch>";

    /**
     * Returns the XML document which forms the body of the HTTP POST request
     * in order to create a batch mailing as a String.
     *
     * @param batchName
     * @return always a valid XML document as String, never null
     */
    static String createBatchRequest( String batchName, String domain ) {
        return MessageFormat.format( 
            createBatchXML,              // XML template
            batchName,                   // {0}
            getRunDate(),                // {1}
            escapeHtml( htmlContent ),   // {2}
            escapeHtml( header1 ),       // {3}
            escapeHtml( otherHeader ),   // {4}
            escapeHtml( header2 ),       // {5}
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
                "<!-- This property is only required on our presentation account. -->" +
                "<property key=\"externalID\">00002</property>" +
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

}//class XMLRequests
