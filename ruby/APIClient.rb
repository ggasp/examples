require 'rubygems'
require 'yaml'
require 'builder'
require 'net/https'
require 'net/sftp'
require 'XMLRequests.rb'
require 'RESTClient.rb'

# Little workaround to prevent ssl to send a warning every time
# is not needed for the api!
class Net::HTTP
  alias_method :old_initialize, :initialize
  def initialize(*args)
    old_initialize(*args)
    @ssl_context = OpenSSL::SSL::SSLContext.new
    @ssl_context.verify_mode = OpenSSL::SSL::VERIFY_NONE
  end
end

# Add the to_iso_format method to the Time class to
# ease formatting of dates
class Time
  def to_iso_format
    self.strftime("%Y-%m-%dT%H:%M:%S%z")
  end
end

class APIClient

  attr_reader :mailing_id

  def initialize(config, mailing_name)
    @config = config
    @mailing_id  = "#{mailing_name}#{Time.new.to_i}"
    @XmlRequests = XmlRequests.new(@mailing_id, @config, (Time.now + 5*60).to_iso_format)
    @restClient  = RESTClient.new(@config["apiUsername"], @config["apiPasswordHash"], @config["apiHost"], @config["apiBase"])
  end

  def createBatchMailing
    createMissingRequirements

    # Create the Batch via API request
    @restClient.doPost("batches/#{mailing_id}", @XmlRequests.batch_xml)
  end

  def createTransactionalMailing
    createMissingRequirements

    # Create the Batch via API request
    @restClient.doPost("transactional_mailings/#{mailing_id}", @XmlRequests.transactional_xml)
  end

  # Transfers a file to the SFTP, which is need for a batch to import a recipient list
  def transferRecipientData
    Net::SFTP.start(@config["scpHost"], @config["scpUsername"], :password => @config["scpPassword"], :port => @config["scpPort"]) do |sftp|
      sftp.upload!(@config["localRecipientFile"], remote_filename)
      puts "uploaded recipient data (#{@config["localRecipientFile"]})"
    end
  end

  # Triggers the import for a batch, after a recipient file was uploaded to the SFTP
  def triggerImport
    @restClient.doPost("batches/#{mailing_id}/import", @XmlRequests.import_xml)
  end

  # Returns revision number for a transactional mail
  def publish
    revisonPath = @restClient.doPost("transactional_mailings/#{mailing_id}/revisions", "<nothing/>")
    @latestRevision = revisonPath.split("/")[-1]
  end

  # Triggering the sending of the latest revision by tranfering a recipient list
  def send(filePath)
    if(!defined? @latestRevision)
      puts "no revision published yet"
      return
    end

    @restClient.doPost("transactional_mailings/#{mailing_id}/revisions/#{@latestRevision}/recipients", File.read(filePath))
  end

  private # All methods that follow are private

  def remote_filename
    @config["scpDirectory"] + "/" + @mailing_id
  end

  def createMissingRequirements
    # Check if the sender is already in the account, if not, adds it
    createMissingSenders

    # Check if all fields are already in the account, if not, adds them
    createMissingRecipientFields
  end

  def createMissingSenders
    availableSenders = loadAvailableSenders

    if !availableSenders.include?@config['senderId']
      addSender
    end
  end

  def loadAvailableSenders
    require "rexml/document"

    senders = @restClient.doGet('senders')
    xml = REXML::Document.new(senders)
    ids = []
    xml.elements['senders'].elements.each('sender') do |sender|
      ids << sender.attributes['id']
    end

    return ids
  end

  def addSender
    @restClient.doPut("senders/#{@config["senderId"]}", @XmlRequests.sender_xml)
  end

  def createMissingRecipientFields
    availableFields = loadAvailableFields

    @config['fields'].each do |field|
      if !availableFields.include?field[0]
        addField(field)
      end
    end
  end

  def loadAvailableFields
    require "rexml/document"

    fields = @restClient.doGet('fields')
    xml = REXML::Document.new(fields)
    names = []
    xml.elements['fields'].elements.each('field') do |field|
      names << field.attributes['name']
    end

    return names
  end

  def addField (field)
    if field.length == 1
      field[1] = "text"
    end

    @restClient.doPost('fields', @XmlRequests.field_xml(field[0], field[1]))
  end
end
