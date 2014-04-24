emarsys Broadcast API Example Code
==================================

There are two small code examples of using the Mailing API of the emarsys
broadcasting system, for batch mailings and transactional mailings.

## Requirements ##

* node.js > 0.8

### node modules ###
* xml2js >=0.2.8
* xmlbuilder >= 0.4.2
* request >= 2.27.0
* dateformat >=1.0.6
* ssh2 >=0.2.12

All required modules can be installed by simply executing `npm install` in the project directory.


## Basic Newsletter Example ##

The basic newsletter example first creates a preview of a mail it intends to
send, and prints the result to standard output. Then it creates a proper
BatchMailing with the same content, uploads a recipients file and
finally triggers the import of the recipient data. The run date of the batch mailing
is set to 5 minutes into the future.

The program can be found in NewsletterExample.js. It uses the node module e3adapter.js.

Refer to config.json in order explore or change the configuration of the client.

The created content is a simple HTML mail (no plain text content) with a single link and
three conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and a
personalization variable ($$RCPT_DOMAIN$$).
The template can be found in newsletter.xml.

The recipients of the generated newletters are stored in recipients.csv.
You might change the email addresses to point to actual mail boxes.
Further you can add fields (aka columns) to the recipient data (you also have to
define the fields in config.json then too).

This simple example does no error handling. In case something unexpected happens it will
print an exception and exit.


## Basic Confirmation Mail Example ##

The basic confirmation mail example creates a transactional mailing, publishes it,
and sends it out to 2 groups of recipients.

The program can be found in ConfirmationMailExample.js. It uses the node module e3adapter.js.

Refer to config.json in order explore or change the configuration of the client.
The sftp settings are ignored since they are not needed for transactional mailing.

The example creates a simple HTML mail (no plain text content) with a single link and three
conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and a
personalization variable ($$RCPT_DOMAIN$$).
The template can be found in confirmation.xml.

The recipients of the generated newletters are stored in recipients.csv and recipients2.csv.
You might change the email addresses to point to actual mail boxes.

This simple example does no error handling. In case something unexpected happens it will
print an exception and exit.
