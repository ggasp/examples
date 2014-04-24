<?php
    class Config {

        //account data
        public $apiUsername =           "<username>";
        public $apiPasswordHash =       "<password>";
        public $apiBaseUrl =            "https://<api-host>/v2";
        public $linkDomain=             "<domain>";

        //sender data
        public $senderId =              "<sender-id>";
        public $senderName =            "<sender-name>";
        public $senderAddress =         "<sender-address>";

        //api access
        public $scpHost =               "<scp-host>";
        public $scpPort =               22;
        public $scpUsername =           "<scp-username>";
        public $scpPassword =           "<scp-password>";

        //The path to the recipients csv file
        public $localRecipientFile =    "recipients.csv";
        public $localRecipientFile2 =    "recipients2.csv";

        public $fields =                array('EMAIL', 'RCPT_TYPE_v2' => 'numeric', 'LANGUAGE_v2' => 'text');
    }
?>
