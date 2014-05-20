<?php
    set_include_path( get_include_path().PATH_SEPARATOR.'phpseclib');

    include( "config.php" );
    include( "confirmation.php" );
    include( "RESTClient.php" );

    /*
     * transactionalMailing implements a very basic client to
     * emarsys e3 Transactional Mailing API web service.
     *
     * The simple client script solely implements the sending of _one_ transactional mailing.
     * In order to do so it performs five steps:
     *
     *  1. Check for Recipient Fields, see createMissingRecipientFields()
     *  2. Check for Senders, see createMissingSenders()
     *  3. Create the TransactionalMailing via an HTTP POST request, see create()
     *  4. Publish the TransactionalMailing
     *  5. Send one _transactional_ mailing.
     *
     * All the other features of the TXMMAPI are not part of this example code.
     */

    class TransactionalMailing {
        private $_base_url = "";
        private $_mailings_base_url = "";
        private $_mailing_id = "";
        private $_config = "";
        private $_restclient = "";

        /*
         * Create a new TransactionalMailing instance.
         *
         * Parameters:
         * 	mailing_id - the unique name of the transactional mailing
         * 	api_config - an instance of Config, see config.php
         */
        function __construct( $mailing_id, $api_config ) {
            $this->_mailing_id = $mailing_id;
            $this->_config = $api_config;
            $this->_base_url = $this->_config->apiBaseUrl;
            $this->_mailings_base_url = "$this->_base_url/transactional_mailings";
            $this->_restclient = new RESTClient( $this->_config->apiUsername, $this->_config->apiPasswordHash );
        }

        /*
         * Creates a new TransactionalMailing.
         */
        function create() {
            $this->createMissingSenders();
            $this->createMissingRecipientFields();

            $xml = confirmation::xml( $this->_config->linkDomain );
            //echo "----------------".PHP_EOL;
            //echo $xml.PHP_EOL;
            //echo "----------------".PHP_EOL;

            $this->_restclient->doPost( $this->_mailings_base_url.'/'.$this->_mailing_id, $xml );
            echo "Created transactional mailing '".$this->_mailing_id."'".PHP_EOL;
        }

        /*
         * Publishes the transactional mailing
         */
        function publish() {
            $body = $this->_restclient->doPost(
                $this->_mailings_base_url.'/'.$this->_mailing_id.'/revisions',
                "<nothing/>"
            );

            echo "Published transactional mailing '".$this->_mailing_id."'".PHP_EOL;

            return $this->_restclient->getLocationRef();
        }

        /*
         * Send a transactional mailing
         */
        function send( $revisionPath, $recipients ) {
            echo "Send transactional mailing '".$this->_mailing_id."'".PHP_EOL;

            $recipientsPath = $revisionPath."/recipients";

            $sendResult = $this->_restclient->doPost(
                $recipientsPath, $recipients
            );

            echo "Result:".PHP_EOL;
            echo $sendResult.PHP_EOL;
        }


        /*
         * Verifies if all fields exist, if not, creates it
         */
        private function createMissingRecipientFields() {
            $availableFields = $this->loadAvailableFields();

            //loop through the fields we want to use and check if the already exits, if not, add them
            foreach ( $this->_config->fields as $name => $type ) {

                // if the no type was given for a field in config.php, this field has a numeric index (key)
                // ergo the name is in the value ($type) and the type is set to the default 'text';
                if ( is_numeric( $name ) ) {
                    $name = $type;
                    $type = 'text';
                }

                if ( !in_array( $name, array_keys( $availableFields ) ) )
                    $this->addField( $name, $type );
            }
        }

        /*
         * Gets all the fields currently available for the account
         */
        private function loadAvailableFields() {

            $xml = $this->_restclient->doGet( $this->_base_url.'/recipient_fields' );

            //convert the output to a SimpleXML
            $xml = new SimpleXMLElement( $xml );

            foreach( $xml->children() as $child ) {
                $attrs = $child->attributes();
                $type = (string) $attrs['type'];
                $name = (string) $attrs['name'];
                $fields[$name] = $type;
            }

            return $fields;
        }

        /*
         * Add a new field to the account.
         */
        private function addField( $name, $type = 'text' ) {
            $xml = confirmation::fieldXML( $name, $type );
            $this->_restclient->DoPost( $this->_base_url.'/recipient_fields', $xml );
            echo "Created Field '".$name."'".PHP_EOL;
        }

        /*
         * Verifies if the Sender already exists, if not, creates it
         */
        private function createMissingSenders() {
            $availableSenders = $this->loadAvailableSenders();

            if( !in_array( $this->_config->senderId, $availableSenders ) )
                $this->addSender( $this->_config->senderId );
        }

        /*
         * Loads all the senders currently available for the account
         */
        private function loadAvailableSenders() {
            $xml = $this->_restclient->doGet( $this->_base_url.'/senders' );

            //convert the output to a SimpleXMl
            $xml = new SimpleXMLElement( $xml);

            foreach( $xml->children() as $child) {
                $attrs = $child->attributes();
                $id = (string) $attrs['id'];

                $senders[] = $id;
            }

            return $senders;
        }


        /*
         * Adds a sender to the available senders for the account
         */
        private function addSender( $senderId) {
            $xml = confirmation::senderXML( $this->_config->senderName, $this->_config->senderAddress );
            $this->_restclient->doPut( $this->_base_url.'/senders/'.$senderId, $xml );
            echo "Created sender '".$senderId."'".PHP_EOL;
        }


    } // class TransactionalMailing


    // Create a unique name for the transactional mailing
    $mailing_id = "ConfirmationMailExample".time();

    // Create the config
    $config = new Config();

    // Create the client instance
    $transactionalMailing = new TransactionalMailing( $mailing_id, $config );

    // Create the transactional mailing
    $transactionalMailing->create();

    // Publish the transactional mailing
    $revisionPath = $transactionalMailing->publish();
    echo "Revision path: ".$revisionPath.PHP_EOL;

    // Send a transactional mailing
    $recipients = file_get_contents( $config->localRecipientFile );
    $transactionalMailing->send( $revisionPath, $recipients );

    $recipients = file_get_contents( $config->localRecipientFile2 );
    $transactionalMailing->send( $revisionPath, $recipients );
?>
