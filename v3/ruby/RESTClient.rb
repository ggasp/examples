class RESTClient

  def initialize(apiUsername, apiPasswordHash, apiHost, apiBase)
    @apiUsername = apiUsername
    @apiPasswordHash = apiPasswordHash
    @apiHost = apiHost
    @apiBase = apiBase
  end

  def doGet(resource_path)
    return doRequest(resource_path, "", 'Get')
  end

  def doPost(resource_path, xml)
    doRequest(resource_path, xml, 'Post')
  end

  def doPut(resource_path, xml)
    doRequest(resource_path, xml, 'Put')
  end

  private

  def doRequest(resource_path, xml, method)

    uri = URI(@apiHost + @apiBase + resource_path)
    #puts "URI = '#{uri}'"

    Net::HTTP.start(
      uri.host, uri.port,
      :use_ssl => uri.scheme == 'https',
      :verify_mode => OpenSSL::SSL::VERIFY_NONE
    ) { |http|
      case method
        when 'Put'
          req = Net::HTTP::Put.new(uri.request_uri)
        when 'Post'
          req = Net::HTTP::Post.new(uri.request_uri)
        else
          req = Net::HTTP::Get.new(uri.request_uri)
      end

      req.basic_auth(@apiUsername, @apiPasswordHash)
      req.body = xml
      req.content_type = "application/xml"

      res = http.request(req)

      case res
        when Net::HTTPSuccess, Net::HTTPRedirection
          puts "#{method} OK (#{resource_path})"
          case method
            when'Get'
              return res.body
            when 'Post'
              return res["location"]
          end
        else
          puts res.body
          res.error!
      end
    }
  end
end
