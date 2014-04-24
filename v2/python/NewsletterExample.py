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

    log("creating preview")
    print client.preview('test@example.com', {'RCPT_TYPE_v2': '0', 'LANGUAGE_v2': 'en'})

    log("creating new batch mailing %s" % client.batchMailingName)
    client.createBatchMailing()

    log("uploading recipients")
    client.transferRecipientData('recipients.csv')

    log("triggering import")
    client.triggerImport()

    log("done - mails will be sent in 5 minutes")
