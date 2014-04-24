<?php
    Class RESTClient {

        private $_apiUsername = '';
        private $_apiPasswordHash = '';
        private $_header = '';

        public function __construct( $apiUsername, $apiPasswordHash ) {
            $this->_apiUsername = $apiUsername;
            $this->_apiPasswordHash = $apiPasswordHash;
        }

        /*
         * Implements an HTTP GET requests.
         */
        public function doGet( $url ) {
            return $this->doRequest( $url, 'GET' );
        }

        /*
         * Implements an HTTP POST requests.
         */
        public function doPost( $url, $data ) {
            return $this->doRequest( $url, 'POST', $data );
        }

        /*
         * Implements an HTTP PUT requests.
         */
        public function doPut( $url, $data ) {
            $this->doRequest( $url, 'PUT', $data );
        }

        /*
         * Can be used to retrieve the whole header received with the last
         * request.
         */
        public function getHeader() {
            return $this->$_header;
        }

        /*
         * Returns the location entry contained in the received header of the
         * last request.
         */
        public function getLocationRef() {
            $headerLines = explode("\r\n", $this->_header);
            foreach( $headerLines as $line ) {
                if ( strpos($line, "Location: ", 0) === 0 ) {
                    return str_replace( "Location: ", "", $line );
                }
            }

            throw new Exception( "Header does not contain a location reference" );
        }

        /*
         * Perform an HTTP request of the specified method to the passed URL with the
         * given data payload.
         *
         * This function will throw an Exception (given the response)
         * if the HTTP response code does not equal 200 (OK).
         *
         * Note: 'libcurl' is used to perform the HTTP requests, you need to have
         * curl available as part of you PHP install.
         */
        private function doRequest( $url, $method, $data = 0 ) {

            //init cURL in PHP
            $curl = curl_init( $url );
            curl_setopt( $curl, CURLOPT_SSL_VERIFYPEER, true );

            // set the method
            switch ( $method )
            {
                case 'GET':
                    break;
                case 'POST':
                    curl_setopt ( $curl, CURLOPT_POST, true );
                    break;
                case 'PUT':
                case 'DELETE':
                    curl_setopt ( $curl, CURLOPT_CUSTOMREQUEST, $method );
                    break;
            }

            if ( $data ) {
                curl_setopt ( $curl, CURLOPT_POSTFIELDS, $data );

                //ask for an xmpl format
                curl_setopt ( $curl, CURLOPT_HTTPHEADER, Array( "Content-Type: application/xml" ) );
            }

            // username and password
            curl_setopt ( $curl, CURLOPT_USERPWD, $this->_apiUsername.":".$this->_apiPasswordHash );

            curl_setopt ( $curl, CURLOPT_HEADER, 1 );
            curl_setopt ( $curl, CURLOPT_VERBOSE, 1 );
            curl_setopt ( $curl, CURLOPT_RETURNTRANSFER, true );

            // request URL
            $response = curl_exec( $curl );

            // split the received response into the header and body part and
            // save the header for further use
            $header_size = curl_getinfo($curl, CURLINFO_HEADER_SIZE);
            $this->_header = substr($response, 0, $header_size);
            $body = substr($response, $header_size);

            $status = curl_getinfo( $curl, CURLINFO_HTTP_CODE );

            curl_close($curl);

            if( $status == 200 ) {
                return $body;
            }
            else {
                throw new Exception( $response );
            }
        }
    }
?>
