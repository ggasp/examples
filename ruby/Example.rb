require 'rubygems'
require 'yaml'
require 'builder'
require 'net/https'
require 'net/sftp'



# Add the  to_iso_format method to the Time class to
# ease formatting of dates
class Time
  def to_iso_format
    self.strftime("%Y-%m-%dT%H:%M:%S%z")
  end
end

class ApiExample
  
  attr_reader :batch_name
  
  def initialize(config)
    @config = config
    @batch_name = "TemplateConditionExampleEx#{Time.new.to_i}"
  end
    
  def remote_filename
    @config["scp_directory"] + "/" + @batch_name
  end

  def transfer_recipient_data
    Net::SFTP.start(@config["scp_host"], @config["scp_username"], :password => @config["scp_password"], :port => @config["scp_port"]) do |sftp|
      sftp.upload!("recipients.cvs", remote_filename)
      puts "uploaded recipient data (recipients.cvs)"
    end
  end

  def post(resource_path, xml)

    https = Net::HTTP.new(@config["api_host"], Net::HTTP.https_default_port)
    https.use_ssl = true

    https.start() { |http|
      req = Net::HTTP::Post.new(@config["api_base"] + resource_path)
      req.basic_auth(@config["username"], @config["password"])
      req.body = xml
      req.content_type = "application/xml"
      
      res = http.request(req)
      
      case res when Net::HTTPSuccess, Net::HTTPRedirection
          puts "POST OK (" + resource_path + ")"
      else
          puts res.body
          res.error!
      end
    }
  end
  
  def batch_xml
    xml = Builder::XmlMarkup.new

    xml.batch {
      xml.name @batch_name
      # Schedule to run in 5 minutes
      xml.runDate((Time.now + 5*60).to_iso_format)
      xml.properties {
        xml.property("key" => "Sender") { xml.text! "default" }
        xml.property("key" => "Language") { xml.text! "en" }
        xml.property("key" => "Encoding") { xml.text! "iso-8859-1" }
        xml.property("key" => "Domain") { xml.text! @config["domain"] }
      }
      xml.subject subjectContent
      xml.html {
        xml.data htmlContent
      }
      xml.conditions {
        xml.condition("name" => "HEADER", "type" => "text/html") {
          xml.cases {
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "emarsys.com") }
              xml.content { xml.data emarsysHeader }
            }
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "example.com") }
              xml.content { xml.data exampleHeader }
            }
          }
          xml.otherwise { xml.data otherwiseHeader }
        }
        xml.condition("name"=>"FOOTER", "type"=>"text/html") {
          xml.cases {
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "emarsys.com") }
              xml.content { xml.data emarsysFooter }
            }
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "example.com") }
              xml.content { xml.data exampleFooter }
            }
          }
          xml.otherwise { xml.data otherwiseFooter }
        }
        xml.condition("name"=>"SUBJECT", "type"=>"text/plain") {
          xml.cases {
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "emarsys.com") }
              xml.content { xml.data emarsysSubject }
            }
            xml.case {
              xml.whens { xml.when("propertyName" => "RCPT_DOMAIN", "operator" => "contains", "value" => "example.com") }
              xml.content { xml.data exampleSubject }
            }
          }
          xml.otherwise { xml.data otherwiseSubject }
        }

      }
    }
  end

  def import_xml 
    xml = Builder::XmlMarkup.new

    xml.importRequest {
        xml.filePath remote_filename
    }
  end    
  
  def emarsysHeader
    %{ <h1>Hi Emarsys!</h1> }
  end

  def exampleHeader 
      %{ <h1>Hi Example!</h1> }
  end

  def otherwiseHeader 
      %{ <h1>Hi Otherwise!</h1> }
  end

  def emarsysFooter
    %{ <p><a href="http://emarsysunsubscribe.com">Click here to unsubscribe</a></p> }
  end

  def exampleFooter 
    %{ <p><a href="http://exampleunsubscribe.com">Click here to unsubscribe</a></p> }
  end

  def otherwiseFooter 
      %{ <p><a href="http://otherwiseunsubscribe.com">Click here to unsubscribe</a></p> }
  end

  def emarsysSubject
    %{ emarsys }
  end

  def exampleSubject 
    %{ example }
  end

  def otherwiseSubject 
    %{ otherwise }
  end
  
  def subjectContent
    %{ Emarsys API Test (##subject##) }
  end

  def htmlContent
    %{
      <html>

        <head>
          <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        </head>

        <body>

          <div align="center">

            <table width="650" cellspacing="0" cellpadding="0" border="0">

              <tr>

                <td align="left">

                  ##HEADER##

                  <p>This is a mail sent to $$email$$</p>
              
                  ##FOOTER##

                </td>

              </tr>

            </table>
  
          </div>

        </body>

      </html>
    }
  end    

end

example = ApiExample.new(YAML.load_file("config.yml"))

# First create the batch
example.post("/batches/#{example.batch_name}", example.batch_xml)
# Transfer the file to be imported
example.transfer_recipient_data
# Trigger the import
example.post("/batches/#{example.batch_name}/import", example.import_xml)
