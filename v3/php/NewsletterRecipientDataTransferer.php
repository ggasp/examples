<?php
    //a pure ssh2 and sftp implemenation in php without any dependencies, see folder phpseclib
    include( 'Net/SFTP.php' );

    class NewsletterRecipientDataTransferer {

        private $host = "";
        private $port = 0;
        private $username = "";
        private $password = "";
        private $connString = "";

        private $recipientFile = "";

        private $sftp = "";

        function __construct( $config ) {

            $this->host = $config->scpHost;
            $this->port = $config->scpPort;
            $this->username = $config->scpUsername;
            $this->password = $config->scpPassword;
            $this->connString = "sftp://{$this->username}:{$this->password}@{$this->host}:{$this->port}";

            $this->recipientFile = $config->localRecipientFile;

            // create a new sftp client
            $this->sftp = new Net_SFTP( $this->host, $this->port );

            echo "Connecting to '".$this->connString."'".PHP_EOL;
        }

        public function login() {

            $success = $this->sftp->login( $this->username, $this->password );
            if ( !$success ) {
                throw new Exception( "Failed to login to '".$this->connString."'".PHP_EOL."SFTP log:".PHP_EOL.$this->sftp->getLog() );
            }

            return true;
        }

        public function put( $remoteFile ) {

            $success = $this->sftp->put( $remoteFile, $this->recipientFile, NET_SFTP_LOCAL_FILE );
            if ( !$success ) {
                throw new Exception( "Failed to copy to '".$this->connString."'".PHP_EOL."SFTP log:".PHP_EOL.$this->sftp->getLog() );
            }

            return true;
        }

    }
?>
