<?php
    set_include_path( get_include_path().PATH_SEPARATOR.'phpseclib');

    include( "config.php" );
    include( "newsletter.php" );
    include( "RESTClient.php" );
    include( "NewsletterRecipientDataTransferer.php" );

    /*
     * batchMailing implements a very basic client to
     * emarsys e3 Batch Mailing API web service.
     *
     * The simple client script solely implements the sending of _one_ batch mailing.
     * In order to do so it performs five steps:
     *
     * 	1. Check for Recipient Fields, see createMissingRecipientFields()
     *  2. Check for Senders, see createMissingSenders()
     *  3. Create the BatchMailing via an HTTP POST request, see create()
     *  4. Transfer the recipients CSV file to the remote SFTP share, see transferRecipientData()
     *	5. Trigger the import (and thus subsequently also the sending) of the batch, see triggerImport()
     *
     * All the other features of the BMAPI are not part of this example code.
     */

    class BatchMailing {

        private $_base_url = "";
        private $_batch_name = "";
        private $_config = "";
        private $_remoteFile = "";
        private $_restclient = "";

        /*
         * Create a new batchMailing instance.
         *
         * Parameters:
         * 	batch_name - the unique name of the batch
         * 	api_config - an instance of Config, see config.php
         */
        function __construct( $batch_name, $api_config ) {


            $this->_batch_name = $batch_name;
            $this->_config = $api_config;
            $this->_base_url = $this->_config->apiBaseUrl;
            $this->_remoteFile = $batch_name.".csv";
            $this->_restclient = new RESTClient( $this->_config->apiUsername, $this->_config->apiPasswordHash );
        }

        /*
         * Creates a new BatchMailing.
         */
        function create() {

            $this->createMissingSenders();
            $this->createMissingRecipientFields();

            $xml = newsletter::xml( $this->_batch_name, $this->_config->linkDomain);
            $this->_restclient->doPost( $this->_base_url.'/batches/'.$this->_batch_name, $xml);
            echo "Created batch mailing '".$this->_batch_name."'".PHP_EOL;
        }

        /*
         * Trigger the import of this batch mailing.
         */
        function triggerImport() {

            $this->_restclient->doPost(
                $this->_base_url.'/batches/'.$this->_batch_name.'/import',
                newsletter::importXML( $this->_remoteFile )
            );

            echo "Triggered import for file '".$this->_remoteFile."'".PHP_EOL;
        }

        /*
         * Transferes the recipients' file to the remote SFTP share.
         *
         * The implementation uses 'phpseclib' which is part of the example code,
         * see the corresponding subfolder where you'll find the code as well
         * as some documentation. For further details you might visit:
         *   http://phpseclib.sourceforge.net/
         */
        function transferRecipientData() {

            $transfer = new NewsletterRecipientDataTransferer( $this->_config );
            $transfer->login();

            // copy the recipientFile to SFTP share
            $transfer->put( $this->_remoteFile );

            echo "Transfered '".$this->_config->localRecipientFile."' to '"
                .$this->_remoteFile."'".PHP_EOL;
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

            $xml = $this->_restclient->doGet( $this->_base_url.'/fields' );

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

            $xml = newsletter::fieldXML( $name, $type );
            $this->_restclient->DoPost( $this->_base_url.'/fields', $xml );
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

            $xml = newsletter::senderXML( $this->_config->senderName, $this->_config->senderAddress );
            $this->_restclient->doPut( $this->_base_url.'/senders/'.$senderId, $xml );
            echo "Created sender '".$senderId."'".PHP_EOL;
        }


    } // class batchMailing

    // Create a unique batch name
    $batch_name = "BatchExample".time();

    // Create the config
    $config = new Config();

    // Create the client instance
    $batchMailing = new BatchMailing( $batch_name, $config );

    // Create the batch mailing
    $batchMailing->create();

    // Copy recipient file via SFTP
    $batchMailing->transferRecipientData();

    // Trigger import (and subsequently the launch) of the copied file
    $batchMailing->triggerImport();

?>
