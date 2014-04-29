emarsys e3 Batch Mailing API Example Code
=========================================

This is a small code example of using the Batch Mailing API of the emarsys e3
broadcasting system.

## Requirements ##

1. Ruby >= 1.8.7
2. builder (gem install builder)

## Newsletter Example ##

The basic newsletter example creates a BatchMailing, uploads a recipients file and 
finally triggers the import of the recipient data. The run date of the batch mailing
will be set to 5 mins into the future and will then be sent. 

The example creates a simple HTML mail (no plain text content) with a single link and three conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.

## Confirmation Mail Example ##

The basic confirmation mail example creates a transactional mailing, publishes the mailing and sends it.
The example creates a simple HTML mail (no plain text content) with a single link and three conditional contents (##SUBJECT##, ##HEADER## and ##FOOTER##) and further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.


## Customizing and Configuration ##

The class containing the logic, that communicates with the API, can be found in APIClient.rb.
The HTML content can be altered in XMLRequests.rb.

Refer to config.yml in order explore or change the configuration of the client.

The recipients for the generated mailings are stored in recipients.csv (and recipients2.csv for the confirmation mail example).
You might change the email addresses to point to actual mailboxes.
Further you can add fields (aka columns) to the recipient data (you also have to do that in the config.yml then).