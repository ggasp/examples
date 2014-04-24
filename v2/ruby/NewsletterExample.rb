require 'APIClient.rb'

#load config
config = YAML.load_file("config.yml")

batchMailing = APIClient.new(config, "BatchMailExample")

# Create the batch
batchMailing.createBatchMailing

# Transfer the file to be imported
batchMailing.transferRecipientData

# Trigger the import
batchMailing.triggerImport
