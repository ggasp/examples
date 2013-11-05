emarsys Broadcast API Example Code
==================================

There are two small code examples of using the Mailing API of the emarsys
broadcasting system, for batch mailings and transactional mailings.

## Requirements ##

1. PHP 5.x
2. libcurl installation supporting http(s).

## Basic Newsletter Example ##

The basic newsletter example creates a BatchMailing, uploads a recipients file
and finally triggers the import of the +recipient data. The run date of the
batch mailing will be set to 5 mins into the future and will then be sent.

The example creates a simple HTML mail (no plain text content) with a single
link and three conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and
further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.

## Basic Confirmation Mail Example ##

The basic confirmation mail example creates a transactional mailing, publishes
the mailing and sends it.

The example creates a simple HTML mail (no plain text content) with a single
link and three conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and
further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.

## Customizing and Configuration ##

The program executed can be found in NewsletterExample.php, which contains the
class BatchMailing. Within BatchMailing is the logic and communication with the
BMAPI. The HTML content can be altered in XMLRequests.php.

Refer to config.php in order explore or change the configuration of the client.

The recipients of the generated newletters are stored in recipients.csv. You
might change the email addresses to point to actual mail boxes. Further you can
add fields (aka columns) to the recipient data (you also have to do that in the
config.php then).
