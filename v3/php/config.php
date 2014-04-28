<?php
    class Config {

        //account data
        public $apiUsername =           "<username>";
        public $apiPasswordHash =       "<password>";
        public $apiBaseUrl =            "https://<api-host>/v3";
        public $linkDomain=             "<domain>";

        //sender data
        public $senderId =              "<sender-id>";
        public $senderName =            "<sender-name>";
        public $senderAddress =         "<sender-address>";

        //The path to the recipients csv file
        public $localRecipientFile =    "recipients.csv";
        public $localRecipientFile2 =    "recipients2.csv";

        public $fields =                array('EMAIL', 'RCPT_TYPE_v3' => 'numeric', 'LANGUAGE_v3' => 'text');
    }
?>
