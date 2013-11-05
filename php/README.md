PHP Example
===========

This example will create a simple batch that is scheduled to be sent a few minutes from now and then
imports a few recipients to which the batch is then sent.

# Files

* config.php            - Contains api configuration like username and password and must be changed
* e3api_php_example.php - A simple client that creates a batch, imports recipients.
* recipients.csv        - The recipients of the test batch, add yourself in order to receive the test
* xml_requests.php      - Contains the html code used when creating the batch as well as methods for creating the xml that is used during the communication.

# Requirements

The only requirement to get the example running is php5 and a libcurl installation supporting http(s).