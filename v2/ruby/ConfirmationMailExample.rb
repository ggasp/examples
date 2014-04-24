require 'APIClient.rb'

#load config
config = YAML.load_file("config.yml")

transactionalMailing = APIClient.new(config, "ConfirmationMailExample")

# Create the transactional mailing
transactionalMailing.createTransactionalMailing

# Publish the transactional mailing
transactionalMailing.publish

# Send the transactional mailing
transactionalMailing.send(config["localRecipientFile"])

# Send another mailing, with different recipients but the same mail content
# Note that it's not necessary to publish a new content everytime
transactionalMailing.send(config["localRecipientFile2"])