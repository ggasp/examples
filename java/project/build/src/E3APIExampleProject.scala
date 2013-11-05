import sbt._

/**
 * The E3APIExampleProject. 
 * A DefaultProject with Maven layout and the dependencies specified below.
 */
class E3APIExampleProject( info : ProjectInfo )
extends DefaultProject( info ) {
    
    val restletRepository	= "restlet repository" at "http://maven.restlet.org"

    val apache_commons_lang = "commons-lang" % "commons-lang" % "2.6"

    val joda_time           = "joda-time" % "joda-time" % "1.6"

    val restlet_api         = "org.restlet" % "org.restlet" % "1.1.10"
    val restlet_impl        = "com.noelios.restlet" % "com.noelios.restlet" % "1.1.10"
    val restlet_httpclient  = "com.noelios.restlet" % "com.noelios.restlet.ext.httpclient" % "1.1.10"

    override def mainClass 	= Some( "com.emarsys.e3.api.example.NewsletterExample" )
}