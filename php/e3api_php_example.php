<?php
    set_include_path( get_include_path().PATH_SEPARATOR.'phpseclib');

    //a pure ssh2 and sftp implemenation in php without any dependencies, see folder phpseclib
    include( 'Net/SFTP.php' );
    
    include( "config.php" );
    include( "xml_requests.php" );
    
    
    /*
     * e3APIClient implements (nomen est omen, sic!) a very basic client to 
     * emarsys e3 Batch Mailing API web service.
     * 
     * The simple client scricpt solely implements the sending of _one_ batch mailing.
     * In order to do so it performs three steps:
     * 
     * 	1. Create the BatchMailing via an HTTP POST request, see BatchRequest()
     *  2. Transfer the recipients CSV file to the remote SFTP share, see CopyRecipientsFile()
     *	3. Trigger the import (and thus subsequently also the sending) of the batch, see ImportRequest()
     * 
     * All the other features of the BMAPI are not part of this example code.
     */
    class e3APIClient {
                    
        private $_batch_name = "";
        private $_config = "";
        private $_base_url = "";
        private $_remoteFile = "";
        
        /*
         * Create a new e3APIClient instance.
         *
         * Parameters:
         * 	batch_name - the unique name of the batch
         * 	api_config - an instance of Config, see config.php
         */
        function __construct ( $batch_name, $api_config ) {

            $this->_batch_name = $batch_name;
            $this->_base_url = $api_config->api_protocol.'://'
		.$api_config->api_host.'/bmapi/accounts/'.$api_config->account_id;
            $this->_config = $api_config;
            $this->_remoteFile = $batch_name.".csv";
        }
        
        /*
	 * Perform an HTTP request of the specified method to the passed URL with the
	 * given data payload.
	 * 
	 * This function will throw an Exception (given the response) 
	 * if the HTTP response code does not equal 200 (OK).
	 *  
	 * Note: 'libcurl' is used to perform the HTTP requests, you need to have 
	 * curl available as part of you PHP install. For further details on curl 
	 * please visit: 
	 */
        private function DoRequest( $url, $data, $method ) {

            //init cURL in PHP
            $curl = curl_init( $url );

            // set the method
            curl_setopt ( $curl, $method, true );

            //ask for an xmpl format
            curl_setopt ( $curl, CURLOPT_HTTPHEADER, Array( "Content-Type: application/xml" ) );

            // username and password
            curl_setopt ( $curl, CURLOPT_USERPWD, $this->_config->username.":".$this->_config->password );

            curl_setopt ( $curl, CURLOPT_HEADER, false ); 

            curl_setopt ( $curl, CURLOPT_POSTFIELDS, $data );
            curl_setopt ( $curl, CURLOPT_VERBOSE, 1 );
            curl_setopt ( $curl, CURLOPT_SSL_VERIFYPEER, true );
            curl_setopt ( $curl, CURLOPT_RETURNTRANSFER, true );

            // request URL
            $response = curl_exec( $curl );
            $status = curl_getinfo( $curl, CURLINFO_HTTP_CODE);
            
            curl_close($curl);

            if( $status == 200 )
                return $response;
            else
                throw new Exception( $response );
        }

	/*
	 * Implements an HTTP POST requests.
	 */
        private function DoPost( $url, $data ) {
            
            $this->DoRequest( $url, $data, CURLOPT_POST );   
        }

	/*
	 * Creates a new BatchMailing.
	 */
        function BatchRequest() {
            
	    $xml = XmlRequests::batchXML( $this->_batch_name , $this->_config->domain);
            $this->DoPost( $this->_base_url.'/batches/'.$this->_batch_name, $xml );
            
            echo "Created batch mailing '".$this->_batch_name."'".PHP_EOL;
        }

	/*
	 * Trigger the import of this batch mailing.
	 */
        function ImportRequest() {
            
            $this->DoPost( 
                $this->_base_url.'/batches/'.$this->_batch_name.'/import', 
                XmlRequests::importXML( $this->_remoteFile ) 
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
	function CopyRecipientsFile() {

	    $conn_str =  'sftp://'.$this->_config->import_username.':'
		  .$this->_config->import_password.'@'
		  .$this->_config->import_host.':'.$this->_config->import_port;

	    // create a new sftp client
	    $sftp = new Net_SFTP( $this->_config->import_host, $this->_config->import_port );
	    
	    echo "Connecting to '".$conn_str."'".PHP_EOL;
	    
	    // log into SFTP share
	    $login_success = $sftp->login( $this->_config->import_username, $this->_config->import_password );
	    
	    if( !$login_success ) {
	        throw new Exception( 
		    "Failed to login to '".$conn_str."'".PHP_EOL."SFTP log:".PHP_EOL.$sftp->getLog() );
	    }
	    
	    // copy the recipient_file to SFTP share
	    $sftp->put( $this->_remoteFile, $this->_config->recipient_file, NET_SFTP_LOCAL_FILE );
	    
	    echo "Transfered '".$this->_config->recipient_file."' to '"
		.$this->_remoteFile."' (".$sftp->size( $this->_remoteFile )." bytes)".PHP_EOL;
        }
        
    } // class e3APIClient


    // Create a unique batch name
    $batch_name = "BatchExample".time();
    // Create the config
    $config = new Config();
    
    // Create the client instance
    $client = new e3APIClient( $batch_name, $config );

    // Create the batch mailing
    $client->BatchRequest();
    
    // Copy recipient file via SFTP
    $client->CopyRecipientsFile();

    // Trigger import (and subsequently the launch) of the copied file
    $client->ImportRequest();
?>
