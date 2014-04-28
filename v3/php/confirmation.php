<?php

/*
 * XmlRequests provides functions to create the XML data for requests to the
 * emarsys Transactional Mailing API web server.
 */
class confirmation {

    // example HTML - header contents
    private static $header1 = "<h1>Header1</h1>";
    private static $header2 = "<h1>Header2</h1>";
    private static $otherHeader = "<h1>other Header</h1>";

    // example HTML - footer contents
    private static $footer1 = "<div>Footer1</div>";
    private static $footer2 = "<div>Footer2</div>";
    private static $otherFooter = "<div>other Footer</div>";

    // example TEXT - subject contents
    private static $subject1 = "Subject1";
    private static $subject2 = "Subject2";
    private static $otherSubject = "other Subject";

    // example HTML - main content
    private static $html_content = '
        <html>
            <head>
                <title>$$RCPT_DOMAIN$$</title>
                <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
            </head>

            <body>

                ##HEADER##

                <p>This is a test with a <a href="http://www.emarsys.com">link</a></p>

                ##FOOTER##

            </body>
        </html>
        ';

    /*
     * Returns the XML definition of an example Transactional Mailing definition
     * with the passed unique name.
     */
    public static function xml( $domain ) {

        date_default_timezone_set('Europe/Vienna');


        $xml = "
            <transactional_mailing>
                <!-- These properties can be defaulted on the account, and do not need to be specified each time. -->
                <properties>
                    <property key=\"Sender\">default</property>
                    <property key=\"Language\">en</property>
                    <property key=\"Encoding\">ISO-8859-1</property>
                    <property key=\"Domain\">$domain</property>
                    <property key=\"ImportDelay\">1</property>
                    <property key=\"IncludeHeader\">false</property>
                </properties>
                <!-- The specification of the recipient fields is mandatory. -->
                <recipient_fields>
                    <field name=\"EMAIL\"/>
                    <field name=\"RCPT_TYPE_v3\"/>
                    <field name=\"LANGUAGE_v3\"/>
                </recipient_fields>
                <subject>Emarsys TXM API Test (##SUBJECT##)</subject>
                <html>
                    <![CDATA[".self::$html_content."]]>
                </html>
                <conditional_contents>
                    <conditional_content id=\"HEADER\">
                        <cases>
                            <case>
                                <when><![CDATA[(LANGUAGE_v3 = \"en\") and (not(RCPT_TYPE_v3 in (1,2,3,4)))]]></when>
                                <html><![CDATA[".self::$header1."]]></html>
                            </case>
                            <case>
                                <when><![CDATA[(LANGUAGE_v3 = \"de\") and (RCPT_TYPE_v3 in (1,2,3,4))]]></when>
                                <html><![CDATA[".self::$header2."]]></html>
                            </case>
                        </cases>
                        <otherwise>
                            <html><![CDATA[".self::$otherHeader."]]></html>
                        </otherwise>
                    </conditional_content>
                    <conditional_content id=\"FOOTER\">
                        <cases>
                            <case>
                                <when><![CDATA[(LANGUAGE_v3 contains \"n\") and (RCPT_TYPE_v3 < 1)]]></when>
                                <html><![CDATA[".self::$footer1."]]></html>
                            </case>
                            <case>
                                <when><![CDATA[(LANGUAGE_v3 contains \"d\") and (RCPT_TYPE_v3 > 1)]]></when>
                                <html><![CDATA[".self::$footer2."]]></html>
                            </case>
                        </cases>
                        <otherwise>
                            <html><![CDATA[".self::$otherFooter."]]></html>
                        </otherwise>
                    </conditional_content>
                    <conditional_content id=\"SUBJECT\">
                        <cases>
                            <case>
                                <when><![CDATA[(RCPT_TYPE_v3 equals 0)]]></when>
                                <text><![CDATA[".self::$subject1."]]></text>
                            </case>
                            <case>
                                <when><![CDATA[(RCPT_TYPE_v3 equals 4)]]></when>
                                <text><![CDATA[".self::$subject2."]]></text>
                            </case>
                        </cases>
                        <otherwise>
                            <text><![CDATA[".self::$otherSubject."]]></text>
                        </otherwise>
                    </conditional_content>
                </conditional_contents>
            </transactional_mailing>
            ";

        return $xml;
    }

    public static function fieldXML ( $name, $type ) {
        $xml = "
            <recipient_fields>
                <field name=\"$name\" type=\"$type\" />
            </recipient_fields>
            ";

        return $xml;
    }

    public static function senderXML( $name ,$address ) {

        $xml = "
            <sender>
                <name>$name</name>
                <address>$address</address>
            </sender>
            ";

        return $xml;
    }

} //class XmlRequests

?>
