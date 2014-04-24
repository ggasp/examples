Emarsys API Example
===================

This is a small code example of using the Emarsys API

## Requirements ##

1. Java 6

## Building The API ##

Run the following code from a console:

1. `./bin/sbt update`
2. `./bin/sbt run`

The build system (SBT, http://www.scala-sbt.org) will
download all required dependencies when you call `update`.

When you run `run` you will be asked which example should be run:


`[1] com.emarsys.e3.api.example.ConfirmationMailExample`
`[2] com.emarsys.e3.api.example.NewsletterExample`

## Basic Newsletter Example ##

The basic newsletter example creates a batch mailing, sets the run date to 5 minutes into the future and adds the recipients contained in the file `recipients.csv`. After 5 minutes the mailing will be sent.

The program executed can be found in `src/main/scala/com/emarsys/e3/api/NewsletterExample.java`

Refer to config.props in order explore or change the configuration of the client.

The example creates a simple HTML mail (no plain text content) with a single link and three conditional contents
(##SUBJECT##, ##HEADER## and ##FOOTER##) and further demos the usage of personalization variables ($$RCPT_DOMAIN$$), too.

The recipients of the generated newletters are stored in `recipients.csv`. You might want to change the email addresses to point to actual mail boxes.
Further you can add fields (aka columns) to the recipient data (you also have to do that in the config.props then).

## Basic Confirmation Mail Example ##

The basic confirmation mail example creates a transactional mailing, publishes it, and sends it out to 2 groups of recipients.

The program executed can be found in `src/main/scala/com/emarsys/e3/api/ConfirmationMail.java`

Refer to `config.json` in order explore or change the configuration of the client.

The example creates a simple HTML mail (no plain text content) with a single link and three conditional contents
(`##SUBJECT##`, `##HEADER##` and `##FOOTER##`) and further demos the usage of personalization variables (`$$RCPT_DOMAIN$$`), too.

The recipients of the generated newletters are stored in `recipients.csv` and `recipients2.csv`. You might want to change the email addresses to point to actual mail boxes.