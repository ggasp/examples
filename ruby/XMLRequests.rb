class XmlRequests

  def initialize(name, config, time)
    @name = name
    @config = config
    @time = time
  end

  def batch_xml
    xml = Builder::XmlMarkup.new

    xml.batch {
      xml.name @name
      # Schedule to run in 5 minutes
      xml.runDate @time
      xml.properties {
        xml.property("key" => "Sender") { xml.text! "default" }
        xml.property("key" => "Language") { xml.text! "en" }
        xml.property("key" => "Encoding") { xml.text! "iso-8859-1" }
        xml.property("key" => "Domain") { xml.text! @config["linkDomain"] }
        xml.property("key" => "IncludeHeader") { xml.text! "false" }
      }
      xml.subject subjectContent
      xml.html htmlContent
      xml.conditions {
        xml.condition("id" => "HEADER") {
          xml.cases {
            xml.case {
              xml.when "(LANGUAGE_v2 = \"en\") and (not(RCPT_TYPE_v2 in (1,2,3,4)))"
              xml.html emarsysHeader
            }
            xml.case {
              xml.when "(LANGUAGE_v2 = \"de\") and (RCPT_TYPE_v2 in (1,2,3,4))"
              xml.html exampleHeader
            }
          }
          xml.otherwise {
            xml.html otherwiseHeader
          }
        }
        xml.condition("id"=>"FOOTER") {
          xml.cases {
            xml.case {
              xml.when "(LANGUAGE_v2 contains \"n\") and (RCPT_TYPE_v2 < 1)"
              xml.html emarsysFooter
            }
            xml.case {
              xml.when "(LANGUAGE_v2 contains \"d\") and (RCPT_TYPE_v2 > 1)"
              xml.html exampleFooter
            }
          }
          xml.otherwise {
            xml.html otherwiseFooter
          }
        }
        xml.condition("id"=>"SUBJECT") {
          xml.cases {
            xml.case {
              xml.when "(RCPT_TYPE_v2 equals 0)"
              xml.text emarsysSubject
            }
            xml.case {
              xml.when "(RCPT_TYPE_v2 equals 4)"
              xml.text exampleSubject
            }
          }
          xml.otherwise {
            xml.text otherwiseSubject
          }
        }

      }
    }
  end

  def transactional_xml
    xml = Builder::XmlMarkup.new

    xml.mailing {
      xml.name @name
      # These properties can be defaulted on the account, and do not need to be specified each time.
      xml.properties {
        xml.property("key" => "Sender") { xml.text! "default" }
        xml.property("key" => "Language") { xml.text! "en" }
        xml.property("key" => "Encoding") { xml.text! "iso-8859-1" }
        xml.property("key" => "Domain") { xml.text! @config["linkDomain"] }
        xml.property("key" => "IncludeHeader") { xml.text! "false" }
      }
      # The specification of the recipient fields is mandatory.
      xml.recipientFields {
        xml.field("name" => "EMAIL")
        xml.field("name" => "RCPT_TYPE_v2")
        xml.field("name" => "LANGUAGE_v2")
      }
      xml.subject subjectContent
      xml.html htmlContent
      xml.conditions {
        xml.condition("id" => "HEADER") {
          xml.cases {
            xml.case {
              xml.when "(LANGUAGE_v2 = \"en\") and (not(RCPT_TYPE_v2 in (1,2,3,4)))"
              xml.html emarsysHeader
            }
            xml.case {
              xml.when "(LANGUAGE_v2 = \"de\") and (RCPT_TYPE_v2 in (1,2,3,4))"
              xml.html exampleHeader
            }
          }
          xml.otherwise {
            xml.html otherwiseHeader
          }
        }
        xml.condition("id"=>"FOOTER") {
          xml.cases {
            xml.case {
              xml.when "(LANGUAGE_v2 contains \"n\") and (RCPT_TYPE_v2 < 1)"
              xml.html emarsysFooter
            }
            xml.case {
              xml.when "(LANGUAGE_v2 contains \"d\") and (RCPT_TYPE_v2 > 1)"
              xml.html exampleFooter
            }
          }
          xml.otherwise {
            xml.html otherwiseFooter
          }
        }
        xml.condition("id"=>"SUBJECT") {
          xml.cases {
            xml.case {
              xml.when "(RCPT_TYPE_v2 equals 0)"
              xml.text emarsysSubject
            }
            xml.case {
              xml.when "(RCPT_TYPE_v2 equals 4)"
              xml.text exampleSubject
            }
          }
          xml.otherwise {
            xml.text otherwiseSubject
          }
        }

      }
    }
  end

  def import_xml
    xml = Builder::XmlMarkup.new

    xml.importRequest {
        xml.filePath @name
    }
  end

  def sender_xml
    xml = Builder::XmlMarkup.new

    xml.sender {
        xml.name @config["senderName"]
        xml.address @config["senderAddress"]
    }
  end

  def field_xml(name, type)
    xml = Builder::XmlMarkup.new

    xml.fields {
        xml.field("name" => name, "type" => type)
    }
  end

  def emarsysHeader
    %{ <h1>Header 1</h1> }
  end

  def exampleHeader
    %{ <h1>Header 2</h1> }
  end

  def otherwiseHeader
    %{ <h1>other Header</h1> }
  end

  def emarsysFooter
    %{ <div>footer1</div> }
  end

  def exampleFooter
    %{ <div>footer2</div> }
  end

  def otherwiseFooter
    %{ <div>other footer</div> }
  end

  def emarsysSubject
    %{ subject 1 }
  end

  def exampleSubject
    %{ subject 2 }
  end

  def otherwiseSubject
    %{ other subject }
  end

  def subjectContent
    %{ Emarsys API Test (##subject##) }
  end

  def htmlContent
    %{
      <html>
        <head>
          <title>$$RCPT_DOMAIN$$</title>
          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </head>

        <body>

          ##HEADER##

          <p>This is a test with a <a href="http://www.emarsys.com">link</a></p>

          ##FOOTER##

        </body>
      </html>
    }
  end
end
