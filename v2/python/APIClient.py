#!/usr/bin/env python

import json, sys, time
from contextlib import closing
from datetime import datetime
from xml.etree import ElementTree

import paramiko      # ssh library
import restclient    # simple REST library


class APIClient(object):
    """A Batch Mailing Client."""

    def __init__(self, config):
        """Init with config."""

        def createAPI():
            transport = restclient.HTTPLib2Transport()
            transport.add_credentials(config['username'], config['password'])
            transport.http.disable_ssl_certificate_validation = True
            return restclient.Resource(config['api_url'], transport)

        def createBatchMailingXML():
            with open('newsletter.xml') as fd:
                rundate = datetime.utcfromtimestamp(self.now + 5*60)
                # Note: does not escape properly.
                return fd.read().format(rundate=rundate.isoformat() + '+0000', **self.config)

        def createTransactionalMailingXML():
            with open('confirmation.xml') as fd:
                # Note: does not escape properly.
                return fd.read().format(name=self.transactionalMailingName, **self.config)

        self.now = int(time.time())
        self.config = config
        self.api = createAPI()

        self.batchMailingName = 'BatchExample%s' % self.now
        self.batchMailingFileName = self.batchMailingName + '.csv'
        self.batchMailingXML = createBatchMailingXML()

        self.transactionalMailingName = 'TXExample%s' % self.now
        self.transactionalMailingXML = createTransactionalMailingXML()
        self.lastRevision = None

    def preview(self, email, fields):
        """Return a preview (string) for newsletter.xml and given recipient and data fields."""

        def createPreviewXML():
            root = ElementTree.Element('previewRequest')
            recipient = _appendXML(root, 'recipient', email=email)
            for (k, v) in fields.items():
                _appendXML(recipient, 'field', v, name=k)
            tree = ElementTree.fromstring(self.batchMailingXML)
            tree.remove(tree[0])  # remove runDate
            root.append(tree)
            return ElementTree.tostring(root)

        return self.api.post('/../beta/preview', createPreviewXML())

    def createBatchMailing(self):
        """Create a new batch mailing using the newsletter.xml file as template."""

        self.api.post('/batches/%s' % self.batchMailingName, self.batchMailingXML)

    def transferRecipientData(self, filename):
        """Upload recipients.csv via sftp."""

        with closing(paramiko.SSHClient()) as sshClient:
            sshClient.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            sshClient.connect(
                hostname=self.config['scp_host'],
                port=self.config['scp_port'],
                username=self.config['scp_username'],
                password=self.config['scp_password'],
                )
            with closing(sshClient.open_sftp()) as sftpClient:
                sftpClient.put(filename, self.batchMailingFileName)

    def triggerImport(self):
        """Trigger import of previously uploaded recipient CSV."""

        def createImportXML():
            root = ElementTree.Element('importRequest')
            _appendXML(root, 'filePath', self.batchMailingFileName)
            return ElementTree.tostring(root)

        self.api.post("batches/%s/import" % self.batchMailingName, createImportXML())

    def getStatus(self):
        return self.api.get("batches/%s/status" % self.batchMailingName)

    def createTransactionalMailing(self):
        """Create a new tx mailing using the confirmation.xml file as template."""

        self.api.post(
            '/transactional_mailings/%s' % self.transactionalMailingName,
            self.transactionalMailingXML,
            )

    def publishRevision(self):
        """Publish a revision. Remember it in self.lastRevision."""
        revisions = self.api(
            '/transactional_mailings/%s/revisions' % self.transactionalMailingName)
        response = revisions.post()
        xml = ElementTree.fromstring(response)
        self.lastRevision = revisions('/' + xml.get('id'))

    def sendTransactional(self, recipients):
        """Send transactional mails to recipients using last published revision."""
        assert self.lastRevision is not None, "nothing published"
        return self.lastRevision.post('/recipients', recipients)

    def sendTransactionalFromFile(self, filename):
        """Send mails to recipients from file using last published revision."""
        with open(filename) as fd:
            return self.sendTransactional(fd.read())

    def createMissingSender(self):
        """Create the sender if the account does not have it already."""

        def createSenderXML():
            root = ElementTree.Element('sender')
            _appendXML(root, 'name', senderName)
            _appendXML(root, 'address', senderAddress)
            return ElementTree.tostring(root)

        senderId = self.config['senderId']
        senderName = self.config['senderName']
        senderAddress = self.config['senderAddress']
        response = self.api.get('/senders')
        xml = ElementTree.fromstring(response)
        senders = [sender.get('id') for sender in xml.findall('sender')]
        if senderId not in senders:
            self.api.put('/senders/%s' % senderId, createSenderXML())

    def createMissingRecipientFields(self):
        """Create missing account field definitions."""

        def createFieldXML(name, type='text'):
            root = ElementTree.Element('fields')
            _appendXML(root, 'field', name=name, type=type)
            return ElementTree.tostring(root)

        fields = self.config['fields']
        response = self.api.get('/fields')
        xml = ElementTree.fromstring(response)
        fieldNames = [field.get('name').upper() for field in xml.findall('field')]
        for fieldName, fieldType in fields.items():
            fieldName = fieldName.upper()
            if fieldName not in fieldNames:
                self.api.post('/fields', createFieldXML(fieldName, fieldType))


def APIClientFromConfigFile(filename):
    """APIClient factory from a JSON encoded config file (by name)."""
    with open(filename) as fd:
        return APIClient(json.load(fd))

def APIClientFromCLIArgs():
    """APIClient factory from command line."""
    try:
        filename = sys.argv[1]
    except IndexError:
        filename = 'config.json'
    return APIClientFromConfigFile(filename)

def _appendXML(element, tag, text=None, **attrib):
    """XML tree building helper."""
    new = ElementTree.Element(tag, attrib)
    if text:
        new.text = text
    element.append(new)
    return new
