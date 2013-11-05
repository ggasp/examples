emarsys e3 Batch Mailing API Example Code
=========================================

This is a small code example of using the Batch Mailing API of the emarsys e3
broadcasting system.

## Requirements ##

1. Java 6

## Building The API ##

Run the following code from a console:

1. `./sbt.sh update`
2. `./sbt.sh run`

The build system (SBT, http://code.google.com/p/simple-build-tool/) will 
download all required dependencies when you call `update`.

## Basic Newsletter Example ##

The basic newsletter example creates a BatchMailing, uploads a recipients file and 
finally triggers the import of the recipient data. The run date of the batch mailing
will be set to 5 mins into the future and will then be sent. 

The example creates a simple HTML mail (no plain text content) with single link and two conditional contents 
(##HEADER##, ##FOOTER##) and further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.

## Customizing and Configuration ##

The program executed can be found in `src/main/scala/com/emarsys/e3/api/NewsletterExample.java`.
The central object which implements the logic and communication with the BMAPI is BatchMailing.java. 
The HTML content can be altered in XMLRequests.java. 

Refer to client.props in order explore or change the configuration of the client.

The recipients of the generated newletters are stored in recipients.csv. 
You might change the email addresses to point to actual mail boxes.
Further you can add fields (aka columns) to the recipient data.