package akka.http.model

import java.net.{URI => jURI}
import akka.http.model.{Uri => AkkaUri}
import node.scalajs.Url.{Url => NodeURL}


//if one wanted to go the banana-rdf way..
//trait URI
//trait JavaURI extends URI
//trait NodeURI extends URI
//trait AkkaURI extends URI
//
//trait URIOps[Uri<: URI] {
//  def scheme(u: Uri): String
//  def userInfo(u: Uri): String
//  def host(u: Uri): String
//  def port(u: Uri): Int
//  def path(u: Uri): String
//  def query(u: Uri): String
//  def fragment(u: Uri): Option[String]
//}

// this should act like the higher level akka uri class
// one should never cast to bURI or this will create an allocation
// see http://docs.scala-lang.org/overviews/core/value-classes.html
trait bURI extends Any {
  def scheme: String
  def userInfo: String
  def host: String
  def port: Int
  def path: String
  def query: String
  def fragment: Option[String]

  //using this will create an object ( see value classes ) but saves me time
  def equalsUri(other: bURI) = {
    other.scheme == scheme &&
      other.userInfo == userInfo &&
      other.host == host &&
      other.port == port &&
      other.path == path &&
      other.query == query &&
      other.fragment == fragment
  }

  override def toString =
    s"""
       |scheme=$scheme userInfo=$userInfo host=$host port=$port path=$path query=$query fragment=$fragment
     """.stripMargin
}

class bjavaURI(val juri: jURI) extends AnyVal with bURI {
  private def ns(s: String) = if (s==null) "" else s

  override def scheme = ns(juri.getScheme)
  override def fragment = Option(juri.getFragment)
  override def host = ns(juri.getHost)
  override def userInfo = ns(juri.getUserInfo)
  override def port = {
    val port = juri.getPort
    if (port == -1) 0 else port
  }
  override def path = ns(juri.getPath)
  override def query = ns(juri.getQuery)
}

class bAkkaURI(val auri: AkkaUri) extends AnyVal with bURI {
  override def scheme = auri.scheme
  override def fragment = auri.fragment
  override def host = auri.authority.host.address
  override def userInfo = auri.authority.userinfo
  override def port = auri.authority.port
  override def path = auri.path.toString
  override def query = auri.query.toString
}

class bNodeURI(val nuri: NodeURL) extends AnyVal with bURI {
  private def ns(s: String) = if (s==null) "" else s
  override def scheme = {
    val p = nuri.protocol
    if ( p != null && p.endsWith(":")) p.substring(0,p.length-1) else ns(p)
  }
  override def fragment = Option(nuri.hash)
  override def host = nuri.host
  override def userInfo = ns(nuri.auth)
  override def path = nuri.pathname
  override def port = {
    val port = nuri.port
    if ( port == null ) 0 else Integer.parseInt(port)
  }
  override def query = {
    val q = ns(nuri.search)
    if (q.startsWith("?")) q.substring(1) else q
  }
}
