<?php

/*
 * XmlRequests provides functions to create the XML data for requests to the
 * emarsys Batch Mailing API web server.
 */
class XmlRequests  {

    // example HTML - header contents 
    private static $header1 = "<h1>Header1</h1>";
    private static $header2 = "<h1>Header2</h1>";

    // example HTML - footer contents
    private static $footer1 = "<div>footer1</div>";
    private static $footer2 = "<div>footer2</div>";
    
    // example TEXT - subject contents
    private static $subject1 = "subject1";
    private static $subject2 = "subject2";
    
    // example HTML - main content
    private static $html_content = '
       <html>
         <head>
           <title>\$\$RCPT_DOMAIN\$\$</title>    
           <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
         </head>

         <body>
        
           ##HEADER##

           <p>This is a test with a <a href="http://www.emarsys.com">link</a></p>
        
           ##FOOTER##

         </body>
      </html>';

    /*
     * Returns the XML definition of an example Batch Mailing 
     * with the passed unique name.
     */
    public static function batchXML( $batch_name ,$domain ) {

        date_default_timezone_set('Europe/Vienna');

        $xml =  
	  "<batch>
	      <!-- This is the identifier for the batch mailing. -->
	      <name>".$batch_name."</name>
	      <!--
		  When the broadcast is planned. For production broadcasts of over 1 million, please update the
		  content with at least 1-2 hours of lead time.
	      -->
	      <runDate>".date("Y-m-d\TH:i:sO" )."</runDate>
	      <!-- These properties can be defaulted on the account, and do not need to be specified each time. -->
	      <properties>
		  <property key=\"Sender\">default</property>
		  <property key=\"Language\">en</property>
		  <property key=\"Encoding\">iso-8859-1</property>
		  <property key=\"Domain\">".$domain."</property>
		  <property key=\"ImportDelay\">1</property>
	      </properties>
	      <subject> Emarsys API Test (##subject##)</subject>
	      <html>
		  <data>".htmlentities( self::$html_content )."</data>
	      </html>
	      <conditions>
		  <condition name=\"HEADER\" type=\"text/html\">
		      <cases>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>
			      <content><data>".htmlentities( self::$header1 )."</data></content>
                          </case>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>
			      <content><data>".htmlentities( self::$header2 )."</data></content>
			  </case>
		      </cases>
		      <otherwise><data>".htmlentities( self::$header1 )."</data></otherwise>
		  </condition>
		  <condition name=\"FOOTER\" type=\"text/html\">
		      <cases>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>
			      <content><data>".htmlentities( self::$footer1 )."</data></content>
			  </case>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>
			      <content><data>".htmlentities( self::$footer2 )."</data></content>
			  </case>
		      </cases>
                      <otherwise><data>".htmlentities( self::$footer2 )."</data></otherwise>

		  </condition>
		  <condition name=\"SUBJECT\" type=\"text/plain\">
		      <cases>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"1\" /></whens>
			      <content><data>".htmlentities( self::$subject1 )."</data></content>
			  </case>
			  <case>
			      <whens><when propertyName=\"RCPT_TYPE\" operator=\"equals\" value=\"2\" /></whens>
			      <content><data>".htmlentities( self::$subject2 )."</data></content>
			  </case>
		      </cases>
                      <otherwise><data>".htmlentities( self::$subject2 )."</data></otherwise>

		  </condition>
	      </conditions>
	  </batch>";
	
	return $xml;
    }

    /*
     * Returns the XML request data which is needed to trigger the import 
     * of the passed file name.
     */
    public static function importXML( $filename ) {

        $xml = 
	  "<importRequest>
	    <filePath>".$filename."</filePath>
	      <properties>
		<property key=\"Delimiter\">,</property>
		<property key=\"Encoding\">UTF-8</property>
	      </properties>
	  </importRequest>";
	  
	return $xml;
    }

} //class XmlRequests
?>
