require './APIClient.rb'

#load config
config = YAML.load_file("config.yml")

batchMailing = APIClient.new(config, "BatchMailExample")

# Create the batch
batchMailing.createBatchMailing

# Add some recipients to the recipient list
batchMailing.addRecipients(config["localRecipientFile"])

# Finish the recipient list
batchMailing.finishRecipients
