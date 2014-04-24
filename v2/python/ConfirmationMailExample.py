#!/usr/bin/env python

from datetime import datetime

from APIClient import APIClientFromCLIArgs

if __name__ == "__main__":
    def log(msg):
        print "LOG", datetime.now().isoformat(), msg

    client = APIClientFromCLIArgs()

    log("creating account sender and field definitions if missing")
    client.createMissingSender()
    client.createMissingRecipientFields()

    log("creating a new transactional mailing %s" % client.transactionalMailingName)
    client.createTransactionalMailing()

    log("publishing a revision")
    client.publishRevision()

    log("sending to first group")
    print client.sendTransactionalFromFile('recipients.csv')

    log("sending to second group")
    print client.sendTransactionalFromFile('recipients2.csv')

    log("done")
