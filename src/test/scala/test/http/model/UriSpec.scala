/**
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package test.http.model

//import java.net.InetAddress

import akka.http.model.Uri._
import akka.scalajs.UTF8
import akka.http.model.{IllegalUriException, Uri}
import utest._

import scala.annotation.tailrec

object UriSpec extends TestSuite {

  override def tests = TestSuite {
    'UriHost {
      'Ipv4Literals {
        assert(Host("192.0.2.16") == IPv4Host("192.0.2.16"))
        assert(Host("255.0.0.0") == IPv4Host("255.0.0.0"))
        assert(Host("0.0.0.0") == IPv4Host("0.0.0.0"))
        assert(Host("1.0.0.0") == IPv4Host("1.0.0.0"))
        assert(Host("2.0.0.0") == IPv4Host("2.0.0.0"))
        assert(Host("3.0.0.0") == IPv4Host("3.0.0.0"))
        assert(Host("30.0.0.0") == IPv4Host("30.0.0.0"))
      }
      'InetAddressRountTrip {
        println("Can't test round trip because scala-js does not have access to java.net.InetAddress")

        //        def roundTrip(ip: String): Unit = {
        //          val inetAddr = InetAddress.getByName(ip)
        //          val addr = Host(inetAddr)
        //          assert( addr == IPv4Host(ip))
        //          assert(addr.inetAddresses == Seq(inetAddr))
        //        }
        //        roundTrip("192.0.2.16")
        //        roundTrip("192.0.2.16")
        //        roundTrip("255.0.0.0")
        //        roundTrip("0.0.0.0")
        //        roundTrip("1.0.0.0")
        //        roundTrip("2.0.0.0")
        //        roundTrip("3.0.0.0")
        //        roundTrip("30.0.0.0")
        //      }
      }
      'IPV6Literals {
        //    "parse correctly from IPv6 literals (RFC2732)"
        // various
        assert(Host("[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]") == IPv6Host("FEDCBA9876543210FEDCBA9876543210", "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210"))
        assert(Host("[1080:0:0:0:8:800:200C:417A]") == IPv6Host("108000000000000000080800200C417A", "1080:0:0:0:8:800:200C:417A"))
        assert(Host("[3ffe:2a00:100:7031::1]") == IPv6Host("3ffe2a00010070310000000000000001", "3ffe:2a00:100:7031::1"))
        assert(Host("[1080::8:800:200C:417A]") == IPv6Host("108000000000000000080800200C417A", "1080::8:800:200C:417A"))
        assert(Host("[::192.9.5.5]") == IPv6Host("000000000000000000000000C0090505", "::192.9.5.5"))
        assert(Host("[::FFFF:129.144.52.38]") == IPv6Host("00000000000000000000FFFF81903426", "::FFFF:129.144.52.38"))
        assert(Host("[2010:836B:4179::836B:4179]") == IPv6Host("2010836B4179000000000000836B4179", "2010:836B:4179::836B:4179"))

        // Quad length
        assert(Host("[abcd::]") == IPv6Host("ABCD0000000000000000000000000000", "abcd::"))
        assert(Host("[abcd::1]") == IPv6Host("ABCD0000000000000000000000000001", "abcd::1"))
        assert(Host("[abcd::12]") == IPv6Host("ABCD0000000000000000000000000012", "abcd::12"))
        assert(Host("[abcd::123]") == IPv6Host("ABCD0000000000000000000000000123", "abcd::123"))
        assert(Host("[abcd::1234]") == IPv6Host("ABCD0000000000000000000000001234", "abcd::1234"))

        // Full length
        assert(Host("[2001:0db8:0100:f101:0210:a4ff:fee3:9566]") == IPv6Host("20010db80100f1010210a4fffee39566", "2001:0db8:0100:f101:0210:a4ff:fee3:9566")) // lower hex
        assert(Host("[2001:0DB8:0100:F101:0210:A4FF:FEE3:9566]") == IPv6Host("20010db80100f1010210a4fffee39566", "2001:0DB8:0100:F101:0210:A4FF:FEE3:9566")) // Upper hex
        assert(Host("[2001:db8:100:f101:210:a4ff:fee3:9566]") == IPv6Host("20010db80100f1010210a4fffee39566", "2001:db8:100:f101:210:a4ff:fee3:9566"))
        assert(Host("[2001:0db8:100:f101:0:0:0:1]") == IPv6Host("20010db80100f1010000000000000001", "2001:0db8:100:f101:0:0:0:1"))
        assert(Host("[1:2:3:4:5:6:255.255.255.255]") == IPv6Host("000100020003000400050006FFFFFFFF", "1:2:3:4:5:6:255.255.255.255"))

        // Legal IPv4
        assert(Host("[::1.2.3.4]") == IPv6Host("00000000000000000000000001020304", "::1.2.3.4"))
        assert(Host("[3:4::5:1.2.3.4]") == IPv6Host("00030004000000000000000501020304", "3:4::5:1.2.3.4"))
        assert(Host("[::ffff:1.2.3.4]") == IPv6Host("00000000000000000000ffff01020304", "::ffff:1.2.3.4"))
        assert(Host("[::0.0.0.0]") == IPv6Host("00000000000000000000000000000000", "::0.0.0.0")) // Min IPv4
        assert(Host("[::255.255.255.255]") == IPv6Host("000000000000000000000000FFFFFFFF", "::255.255.255.255")) // Max IPv4

        // Zipper position
        assert(Host("[::1:2:3:4:5:6:7]") == IPv6Host("00000001000200030004000500060007", "::1:2:3:4:5:6:7"))
        assert(Host("[1::1:2:3:4:5:6]") == IPv6Host("00010000000100020003000400050006", "1::1:2:3:4:5:6"))
        assert(Host("[1:2::1:2:3:4:5]") == IPv6Host("00010002000000010002000300040005", "1:2::1:2:3:4:5"))
        assert(Host("[1:2:3::1:2:3:4]") == IPv6Host("00010002000300000001000200030004", "1:2:3::1:2:3:4"))
        assert(Host("[1:2:3:4::1:2:3]") == IPv6Host("00010002000300040000000100020003", "1:2:3:4::1:2:3"))
        assert(Host("[1:2:3:4:5::1:2]") == IPv6Host("00010002000300040005000000010002", "1:2:3:4:5::1:2"))
        assert(Host("[1:2:3:4:5:6::1]") == IPv6Host("00010002000300040005000600000001", "1:2:3:4:5:6::1"))
        assert(Host("[1:2:3:4:5:6:7::]") == IPv6Host("00010002000300040005000600070000", "1:2:3:4:5:6:7::"))

        // Zipper length
        assert(Host("[1:1:1::1:1:1:1]") == IPv6Host("00010001000100000001000100010001", "1:1:1::1:1:1:1"))
        assert(Host("[1:1:1::1:1:1]") == IPv6Host("00010001000100000000000100010001", "1:1:1::1:1:1"))
        assert(Host("[1:1:1::1:1]") == IPv6Host("00010001000100000000000000010001", "1:1:1::1:1"))
        assert(Host("[1:1::1:1]") == IPv6Host("00010001000000000000000000010001", "1:1::1:1"))
        assert(Host("[1:1::1]") == IPv6Host("00010001000000000000000000000001", "1:1::1"))
        assert(Host("[1::1]") == IPv6Host("00010000000000000000000000000001", "1::1"))
        assert(Host("[::1]") == IPv6Host("00000000000000000000000000000001", "::1")) // == localassert(host
        assert(Host("[::]") == IPv6Host("00000000000000000000000000000000", "::")) // == all addresses

        // A few more variations
        assert(Host("[21ff:abcd::1]") == IPv6Host("21ffabcd000000000000000000000001", "21ff:abcd::1"))
        assert(Host("[2001:db8:100:f101::1]") == IPv6Host("20010db80100f1010000000000000001", "2001:db8:100:f101::1"))
        assert(Host("[a:b:c::12:1]") == IPv6Host("000a000b000c00000000000000120001", "a:b:c::12:1"))
        assert(Host("[a:b::0:1:2:3]") == IPv6Host("000a000b000000000000000100020003", "a:b::0:1:2:3"))

      }

      'Inet6AddressRoundTrip {
        println("Can't test round trip because scala-js does not have access to java.net.InetAddress")

        // scalaJs does not have access to Inet6Addressses
        //    "support inetAddresses round-trip for Inet6Addresses" in {
        //      def fromAddress(address: String): IPv6Host = Host(s"[$address]").asInstanceOf[IPv6Host]
        //      def roundTrip(ip: String): Unit = {
        //        val inetAddr = InetAddress.getByName(ip)
        //        val addr = Host(inetAddr)
        //        addr equalsIgnoreCase fromAddress(ip) should be(true)
        //        addr.inetAddresses shouldEqual Seq(inetAddr)
        //      }
        //
        //      roundTrip("1:1:1::1:1:1:1")
        //      roundTrip("::1:2:3:4:5:6:7")
        //      roundTrip("2001:0DB8:0100:F101:0210:A4FF:FEE3:9566")
        //      roundTrip("2001:0db8:100:f101:0:0:0:1")
        //      roundTrip("abcd::12")
        //      roundTrip("::192.9.5.5")
        //    }
      }

      'NamedHostLiterals {
        assert(Host("www.spray.io") == NamedHost("www.spray.io"))
        assert(Host("localhost") == NamedHost("localhost"))
        assert(Host("%2FH%C3%A4ll%C3%B6%5C") == NamedHost( """/hällö\"""))
      }
      'IllegalIPv4Literals {
        //          "not accept illegal IPv4 literals"
        assert(Host("01.0.0.0").isInstanceOf[NamedHost])
        assert(Host("001.0.0.0").isInstanceOf[NamedHost])
        assert(Host("00.0.0.0").isInstanceOf[NamedHost])
        assert(Host("000.0.0.0").isInstanceOf[NamedHost])
        assert(Host("256.0.0.0").isInstanceOf[NamedHost])
        assert(Host("300.0.0.0").isInstanceOf[NamedHost])
        assert(Host("1111.0.0.0").isInstanceOf[NamedHost])
        assert(Host("-1.0.0.0").isInstanceOf[NamedHost])
        assert(Host("0.0.0").isInstanceOf[NamedHost])
        assert(Host("0.0.0.").isInstanceOf[NamedHost])
        assert(Host("0.0.0.0.").isInstanceOf[NamedHost])
        assert(Host("0.0.0.0.0").isInstanceOf[NamedHost])
        assert(Host("0.0..0").isInstanceOf[NamedHost])
        assert(Host(".0.0.0").isInstanceOf[NamedHost])
      }

      'IllegalIPv6Literals {
        // "not accept illegal IPv6 literals"
        //      // 5 char quad
        val err = intercept[IllegalUriException] {
          Host("[::12345]")
        }
        assert(err == new IllegalUriException("Illegal URI host: Invalid input '5', expected !HEXDIG, ':' or ']' (line 1, column 8)",
          "[::12345]\n" +
            "       ^"))


        // Two zippers
        intercept[IllegalUriException] {
          Host("[abcd::abcd::abcd]")
        }

        // Triple-colon zipper
        intercept[IllegalUriException] {
          Host("[:::1234]")
        }
        intercept[IllegalUriException] {
          Host("[1234:::1234:1234]")
        }
        intercept[IllegalUriException] {
          Host("[1234:1234:::1234]")
        }
        intercept[IllegalUriException] {
          Host("[1234:::]")
        }

        // No quads, just IPv4
        intercept[IllegalUriException] {
          Host("[1.2.3.4]")
        }
        intercept[IllegalUriException] {
          Host("[0001.0002.0003.0004]")
        }

        // Five quads
        intercept[IllegalUriException] {
          Host("[0000:0000:0000:0000:0000:1.2.3.4]")
        }

        // Seven quads
        intercept[IllegalUriException] {
          Host("[0:0:0:0:0:0:0]")
        }
        intercept[IllegalUriException] {
          Host("[0:0:0:0:0:0:0:]")
        }
        intercept[IllegalUriException] {
          Host("[0:0:0:0:0:0:0:1.2.3.4]")
        }

        // Nine quads
        intercept[IllegalUriException] {
          Host("[0:0:0:0:0:0:0:0:0]")
        }

        // Invalid IPv4 part
        intercept[IllegalUriException] {
          Host("[::ffff:001.02.03.004]")
        } // Leading zeros
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3.1111]")
        } // Four char octet
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3.256]")
        } // > 255
        intercept[IllegalUriException] {
          Host("[::ffff:311.2.3.4]")
        } // > 155
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3:4]")
        } // Not a dot
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3]")
        } // Missing octet
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3.]")
        } // Missing octet
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3a.4]")
        } // Hex in octet
        intercept[IllegalUriException] {
          Host("[::ffff:1.2.3.4:123]")
        } // Crap input

        // Nonhex
        intercept[IllegalUriException] {
          Host("[g:0:0:0:0:0:0]")
        }

      }
    }

    'UriPath {
      import akka.http.model.Uri.Path.Empty

      'ParsedCorrectly {
        assert(Path("") == Empty)
        assert(Path("/") == Path./)
        assert(Path("a") == "a" :: Empty)
        assert(Path("//") == Path./ / "")
        assert(Path("a/") == "a" :: Path./)
        assert(Path("/a") == Path / "a")
        assert(Path("/abc/de/f") == Path / "abc" / "de" / "f")
        assert(Path("abc/de/f/") == "abc" :: '/' :: "de" :: '/' :: "f" :: Path./)
        assert(Path("abc///de") == "abc" :: '/' :: '/' :: '/' :: "de" :: Empty)
        assert(Path("/abc%2F") == Path / "abc/")
        assert(Path("H%C3%A4ll%C3%B6") == """Hällö""" :: Empty)
        assert(Path("/%2F%5C") == Path / """/\""")
        assert(Path("/:foo:/") == Path / ":foo:" / "")
        assert(Path("%2520").head == "%20")
      }

      'startsWith {
        //  "support the `startsWith` predicate"
        assert({
          Empty startsWith Empty
        } == true)
        assert({
          Path./ startsWith Empty
        } == true)
        assert({
          Path("abc") startsWith Empty
        } == true)
        assert({
          Empty startsWith Path./
        } == false)
        assert({
          Empty startsWith Path("abc")
        } == false)
        assert({
          Path./ startsWith Path./
        } == true)
        assert({
          Path./ startsWith Path("abc")
        } == false)
        assert({
          Path("/abc") startsWith Path./
        } == true)
        assert({
          Path("abc") startsWith Path./
        } == false)
        assert({
          Path("abc") startsWith Path("ab")
        } == true)
        assert({
          Path("abc") startsWith Path("abc")
        } == true)
        assert({
          Path("/abc") startsWith Path("/a")
        } == true)
        assert({
          Path("/abc") startsWith Path("/abc")
        } == true)
        assert({
          Path("/ab") startsWith Path("/abc")
        } == false)
        assert({
          Path("/abc") startsWith Path("/abd")
        } == false)
        assert({
          Path("/abc/def") startsWith Path("/ab")
        } == true)
        assert({
          Path("/abc/def") startsWith Path("/abc/")
        } == true)
        assert({
          Path("/abc/def") startsWith Path("/abc/d")
        } == true)
        assert({
          Path("/abc/def") startsWith Path("/abc/def")
        } == true)
        assert({
          Path("/abc/def") startsWith Path("/abc/def/")
        } == false)
      }



      'dropChars {
        // "support the `dropChars` modifier"
        assert(Path./.dropChars(0) == Path./)
        assert(Path./.dropChars(1) == Empty)
        assert(Path("/abc/def/").dropChars(0) == Path("/abc/def/"))
        assert(Path("/abc/def/").dropChars(1) == Path("abc/def/"))
        assert(Path("/abc/def/").dropChars(2) == Path("bc/def/"))
        assert(Path("/abc/def/").dropChars(3) == Path("c/def/"))
        assert(Path("/abc/def/").dropChars(4) == Path("/def/"))
        assert(Path("/abc/def/").dropChars(5) == Path("def/"))
        assert(Path("/abc/def/").dropChars(6) == Path("ef/"))
        assert(Path("/abc/def/").dropChars(7) == Path("f/"))
        assert(Path("/abc/def/").dropChars(8) == Path("/"))
        assert(Path("/abc/def/").dropChars(9) == Empty)

      }
    }
    'UriQueryInstances {
        def parser(mode: Uri.ParsingMode): String ⇒ Query = Query(_, mode = mode)
       'strictMode {
         val test = parser(Uri.ParsingMode.Strict)
         assert(test("") == {("", "") +: Query.Empty})
         assert(test("a") == {("a", "") +: Query.Empty})
         assert(test("a=") == {("a", "") +: Query.Empty})
         assert(test("=a") == {("", "a") +: Query.Empty})
         assert(test("a&") == {("a", "") +: ("", "") +: Query.Empty})
         intercept[IllegalUriException]{ test("a^=b")}
       }
      'relaxedMode {
        val test = parser(Uri.ParsingMode.Relaxed)
        assert(test("") == {("", "") +: Query.Empty})
        assert(test("a") == {("a", "") +: Query.Empty})
        assert(test("a=") == {("a", "") +: Query.Empty})
        assert(test("=a") == {("", "a") +: Query.Empty})
        assert(test("a&") == {("a", "") +: ("", "") +: Query.Empty})
        assert(test("a^=b") == {("a^", "b") +: Query.Empty})

      }
      'relaxedWithRawQuery {
         val test = parser(Uri.ParsingMode.RelaxedWithRawQuery)
         assert(test("a^=b&c").toString == "a^=b&c")
         assert(test("a%2Fb") == Uri.Query.Raw("a%2Fb"))
      }
      'retrievalInterface {  //"properly support the retrieval interface"
          val query = Query("a=1&b=2&c=3&b=4&b")
          assert(query.get("a") == Some("1"))
          assert(query.get("d") == None)
          assert(query.getOrElse("a", "x") == "1")
          assert(query.getOrElse("d", "x") == "x")
          assert(query.getAll("b") == List("", "4", "2"))
          assert(query.getAll("d") == Nil)
          assert(query.toMap == Map("a" -> "1", "b" -> "", "c" -> "3"))
          assert(query.toMultiMap == Map("a" -> List("1"), "b" -> List("", "4", "2"), "c" -> List("3")))
          assert(query.toList == List("a" -> "1", "b" -> "2", "c" -> "3", "b" -> "4", "b" -> ""))
          assert(query.toSeq == Seq("a" -> "1", "b" -> "2", "c" -> "3", "b" -> "4", "b" -> ""))
      }
      'List2NamValPairConversion { // "support conversion from list of name/value pairs" 
          import akka.http.model.Uri.Query._
          val pairs = List("key1" -> "value1", "key2" -> "value2", "key3" -> "value3")
          assert(Query(pairs: _*).toList.diff(pairs) == Nil)
          assert(Query() == Empty)
          assert(Query("k" -> "v") == ("k" -> "v") +: Empty)
     }
 

  }
  'URI {

    // http://tools.ietf.org/html/rfc3986#section-1.1.2
    'parseAndRender {  //"be correctly parsed from and rendered to simple test examples"
      assert(Uri("ftp://ftp.is.co.za/rfc/rfc1808.txt") == Uri.from(scheme = "ftp", host = "ftp.is.co.za", path = "/rfc/rfc1808.txt"))

      assert(Uri("http://www.ietf.org/rfc/rfc2396.txt") == Uri.from(scheme = "http", host = "www.ietf.org", path = "/rfc/rfc2396.txt"))

      assert(Uri("ldap://[2001:db8::7]/c=GB?objectClass?one") == Uri.from(scheme = "ldap", host = "[2001:db8::7]", path = "/c=GB", query = Query("objectClass?one")))

      assert(Uri("mailto:John.Doe@example.com") == Uri.from(scheme = "mailto", path = "John.Doe@example.com"))

      assert(Uri("news:comp.infosystems.www.servers.unix") == Uri.from(scheme = "news", path = "comp.infosystems.www.servers.unix"))

      assert(Uri("tel:+1-816-555-1212") == Uri.from(scheme = "tel", path = "+1-816-555-1212"))

      assert(Uri("telnet://192.0.2.16:80/") == Uri.from(scheme = "telnet", host = "192.0.2.16", port = 80, path = "/"))

      assert(Uri("urn:oasis:names:specification:docbook:dtd:xml:4.1.2") == Uri.from(scheme = "urn", path = "oasis:names:specification:docbook:dtd:xml:4.1.2"))

      // more examples
      assert(Uri("http://") == Uri(scheme = "http", authority = Authority(host = NamedHost(""))))
      assert(Uri("http:?") == Uri.from(scheme = "http", query = Query("")))
      assert(Uri("?a+b=c%2Bd") == Uri.from(query = ("a b", "c+d") +: Query.Empty))

      // illegal paths
      assert(Uri("foo/another@url/[]and{}") == Uri.from(path = "foo/another@url/%5B%5Dand%7B%7D"))
      intercept[IllegalUriException] {
        Uri("foo/another@url/[]and{}", mode = Uri.ParsingMode.Strict)
      }

      // handle query parameters with more than percent-encoded character
      assert(Uri("?%7Ba%7D=$%7B%7D", UTF8, Uri.ParsingMode.Strict) == Uri(query = Query.Cons("{a}", "${}", Query.Empty)))

      // don't double decode
      assert(Uri("%2520").path.head == "%20")
      assert(Uri("/%2F%5C").path == Path / """/\""")

      // render
      assert(Uri("https://server.com/path/to/here?st=12345").toString == "https://server.com/path/to/here?st=12345")
      assert(Uri("/foo/?a#b").toString == "/foo/?a#b")

    }
    
    'normalization {

      // http://tools.ietf.org/html/rfc3986#section-6.2.2
      assert(normalize("eXAMPLE://a/./b/../b/%63/%7bfoo%7d") == "example://a/b/c/%7Bfoo%7D")

      // more examples
      assert(normalize("") == "")
      assert(normalize("/") == "/")
      assert(normalize("../../") == "../../")
      assert(normalize("aBc") == "aBc")

      assert(normalize("Http://Localhost") == "http://localhost")
      assert(normalize("hTtP://localHost") == "http://localhost")
      assert(normalize("https://:443") == "https://")
      assert(normalize("ftp://example.com:21") == "ftp://example.com")
      assert(normalize("example.com:21") == "example.com:21")
      assert(normalize("ftp://example.com:22") == "ftp://example.com:22")

      assert(normalize("//user:pass@[::1]:80/segment/index.html?query#frag") == "//user:pass@[::1]:80/segment/index.html?query#frag")
      assert(normalize("http://[::1]:80/segment/index.html?query#frag") == "http://[::1]/segment/index.html?query#frag")
      assert(normalize("http://user:pass@[::1]/segment/index.html?query#frag") == "http://user:pass@[::1]/segment/index.html?query#frag")
      assert(normalize("http://user:pass@[::1]:80?query#frag") == "http://user:pass@[::1]?query#frag")
      assert(normalize("http://user:pass@[::1]/segment/index.html#frag") == "http://user:pass@[::1]/segment/index.html#frag")
      assert(normalize("http://user:pass@[::1]:81/segment/index.html?query") == "http://user:pass@[::1]:81/segment/index.html?query")
      assert(normalize("ftp://host:21/gnu/") == "ftp://host/gnu/")
      assert(normalize("one/two/three") == "one/two/three")
      assert(normalize("/one/two/three") == "/one/two/three")

      assert(normalize("//user:pass@localhost/one/two/three") == "//user:pass@localhost/one/two/three")
      assert(normalize("http://www.example.com/") == "http://www.example.com/")
      assert(normalize("http://sourceforge.net/projects/uriparser/") == "http://sourceforge.net/projects/uriparser/")
      assert(normalize("http://sourceforge.net/project/platformdownload.php?group_id=182840") == "http://sourceforge.net/project/platformdownload.php?group_id=182840")
      assert(normalize("mailto:test@example.com") == "mailto:test@example.com")
      assert(normalize("file:///bin/bash") == "file:///bin/bash")
      assert(normalize("http://www.example.com/name%20with%20spaces/") == "http://www.example.com/name%20with%20spaces/")
      assert(normalize("http://examp%4Ce.com/") == "http://example.com/")
      assert(normalize("http://example.com/a/b/%2E%2E/") == "http://example.com/a/")
      assert(normalize("http://user:pass@SOMEHOST.COM:123") == "http://user:pass@somehost.com:123")
      assert(normalize("HTTP://a:b@HOST:123/./1/2/../%41?abc#def") == "http://a:b@host:123/1/A?abc#def")

      // acceptance and normalization of unescaped ascii characters such as {} and []:
      assert(normalize("eXAMPLE://a/./b/../b/%63/{foo}/[bar]") == "example://a/b/c/%7Bfoo%7D/%5Bbar%5D")
      intercept[IllegalUriException]{ normalize("eXAMPLE://a/./b/../b/%63/{foo}/[bar]", mode = Uri.ParsingMode.Strict)}

      // queries
      assert(normalize("?") == "?")
      assert(normalize("?key") == "?key")
      //todo: these two do not work - could it be a bug with plantain2 js
      //assert(normalize("?key=") == "?key=")
      //assert(normalize("?key=&a=b") == "?key=&a=b")
      assert(normalize("?key={}&a=[]") == "?key=%7B%7D&a=%5B%5D")
      intercept[IllegalUriException] { normalize("?key={}&a=[]", mode = Uri.ParsingMode.Strict) }
      assert(normalize("?=value") == "?=value")
      assert(normalize("?key=value") == "?key=value")
      assert(normalize("?a+b") == "?a+b")
      assert(normalize("?=a+b") == "?=a+b")
      assert(normalize("?a+b=c+d") == "?a+b=c+d")
      assert(normalize("??") == "??")
      assert(normalize("?a=1&b=2") == "?a=1&b=2")
      assert(normalize("?a+b=c%2Bd") == "?a+b=c%2Bd")
      assert(normalize("?a&a") == "?a&a")
      assert(normalize("?&#") == "?&#")
      assert(normalize("?#") == "?#")
      assert(normalize("#") == "#")
      assert(normalize("#{}[]") == "#%7B%7D%5B%5D")
      intercept[IllegalUriException] { normalize("#{}[]", mode = Uri.ParsingMode.Strict)}
    }
    'tunneling {
      // "support tunneling a URI through a query param"
      val uri = Uri("http://aHost/aPath?aParam=aValue#aFragment")
      val q = Query("uri" -> uri.toString)
      val uri2 = Uri(path = Path./, query = q, fragment = Some("aFragment")).toString
      assert(uri2 == "/?uri=http://ahost/aPath?aParam%3DaValue%23aFragment#aFragment")
      assert(Uri(uri2).query == q)
      assert(Uri(q.getOrElse("uri", "<nope>")) == uri)
    }
    'illegalUriErrorMsg {
      //"produce proper error messages for illegal URIs"
      // illegal scheme
      assert(intercept[IllegalUriException] {
        Uri("foö:/a")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input 'ö', expected scheme-char, ':', path-segment-char, '%', '/', '?', '#' or 'EOI' (line 1, column 3)",
          "foö:/a\n" +
            "  ^")
      )

      // illegal userinfo
      assert(intercept[IllegalUriException] {
        Uri("http://user:ö@host")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input 'ö', expected userinfo-char, '%', '@' or DIGIT (line 1, column 13)",
          "http://user:ö@host\n" +
            "            ^")
      )

      // illegal percent-encoding
      assert(intercept[IllegalUriException] {
        Uri("http://use%2G@host")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input 'G', expected HEXDIG (line 1, column 13)",
          "http://use%2G@host\n" +
            "            ^")
      )

      // illegal path
      assert(intercept[IllegalUriException] {
        Uri("http://www.example.com/name with spaces/")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input ' ', expected path-segment-char, '%', '/', '?', '#' or 'EOI' (line 1, column 28)",
          "http://www.example.com/name with spaces/\n" +
            "                           ^")
      )

      // illegal path with control character
      assert(intercept[IllegalUriException] {
        Uri("http:///with\newline")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input '\\n', expected path-segment-char, '%', '/', '?', '#' or 'EOI' (line 1, column 13)",
          "http:///with\n" +
            "            ^")
      )

      // illegal query
      assert(intercept[IllegalUriException] {
        Uri("?a=b=c")
      } ==
        new IllegalUriException("Illegal URI reference: Invalid input '=', expected '+', query-char, '%', '&', '#' or 'EOI' (line 1, column 5)",
          "?a=b=c\n" +
            "    ^")
      )
    }
    
        // http://tools.ietf.org/html/rfc3986#section-5.4
    'rfc3986referenceResolution {
      //"pass the RFC 3986 reference resolution examples"
      val base = parseAbsolute("http://a/b/c/d;p?q")
      def resolve(uri: String): String = {
        parseAndResolve(uri, base).toString
      }

      'normalExamples {
        //todo: can't seem to compile the code below
        //check issue https://github.com/lihaoyi/utest/issues/24
//        assert( resolve("g:h") == "g:h")
//        assert( resolve("g") == "http://a/b/c/g")
//        assert( resolve("./g") == "http://a/b/c/g")
//        assert( resolve("g/") == "http://a/b/c/g/")
//        assert( resolve("/g") == "http://a/g")
//        assert( resolve("//g") == "http://g")
//        assert( resolve("?y") == "http://a/b/c/d;p?y")
//        assert( resolve("g?y") == "http://a/b/c/g?y")
//        assert( resolve("#s") == "http://a/b/c/d;p?q#s")
//        assert( resolve("g#s") == "http://a/b/c/g#s")
//        assert( resolve("g?y#s") == "http://a/b/c/g?y#s")
//        assert( resolve(";x") == "http://a/b/c/;x")
//        assert( resolve("g;x") == "http://a/b/c/g;x")
//        assert( resolve("g;x?y#s") == "http://a/b/c/g;x?y#s")
//        assert( resolve("") == "http://a/b/c/d;p?q")
//        assert( resolve(".") == "http://a/b/c/")
//        assert( resolve("./") == "http://a/b/c/")
//        assert( resolve("..") == "http://a/b/")
//        assert( resolve("../") == "http://a/b/")
//        assert( resolve("../g") == "http://a/b/g")
//        assert( resolve("../..") == "http://a/")
//        assert( resolve("../../") == "http://a/")
//        assert( resolve("../../g") == "http://a/g")
      }

      'abnormalExamples {
        //todo: Can't compile the code below
        //check issue https://github.com/lihaoyi/utest/issues/24
//        assert(resolve("../../../g") == "http://a/g")
//        assert(resolve("../../../../g") == "http://a/g")
//
//        assert(resolve("/./g") == "http://a/g")
//        assert(resolve("/../g") == "http://a/g")
//        assert(resolve("g.") == "http://a/b/c/g.")
//        assert(resolve(".g") == "http://a/b/c/.g")
//        assert(resolve("g..") == "http://a/b/c/g..")
//        assert(resolve("..g") == "http://a/b/c/..g")
//
//        assert(resolve("./../g") == "http://a/b/g")
//        assert(resolve("./g/.") == "http://a/b/c/g/")
//        assert(resolve("g/./h") == "http://a/b/c/g/h")
//        assert(resolve("g/../h") == "http://a/b/c/h")
//        assert(resolve("g;x=1/./y") == "http://a/b/c/g;x=1/y")
//        assert(resolve("g;x=1/../y") == "http://a/b/c/y")
//
//        assert(resolve("g?y/./x") == "http://a/b/c/g?y/./x")
//        assert(resolve("g?y/../x") == "http://a/b/c/g?y/../x")
//        assert(resolve("g#s/./x") == "http://a/b/c/g#s/./x")
//        assert(resolve("g#s/../x") == "http://a/b/c/g#s/../x")
//
//        assert(resolve("http:g") == "http:g")
      }
    }

    'Copyable {
      val uri = Uri("http://host:80/path?query#fragment")
      assert (uri.copy() == uri )
    }
    
    'FluentTransformationSugar {
    //    "provide sugar for fluent transformations"
      val uri = Uri("http://host:80/path?query#fragment")
      val nonDefaultUri = Uri("http://host:6060/path?query#fragment")

      assert(uri.withScheme("https") == Uri("https://host/path?query#fragment"))
      assert(nonDefaultUri.withScheme("https") == Uri("https://host:6060/path?query#fragment"))

      assert(uri.withAuthority(Authority(Host("other"), 3030)) == Uri("http://other:3030/path?query#fragment"))
      assert(uri.withAuthority(Host("other"), 3030) == Uri("http://other:3030/path?query#fragment"))
      assert(uri.withAuthority("other", 3030) == Uri("http://other:3030/path?query#fragment"))

      assert(uri.withHost(Host("other")) == Uri("http://other:80/path?query#fragment"))
      assert(uri.withHost("other") == Uri("http://other:80/path?query#fragment"))
      assert(uri.withPort(90) == Uri("http://host:90/path?query#fragment"))

      assert(uri.withPath(Path("/newpath")) == Uri("http://host/newpath?query#fragment"))
      assert(uri.withUserInfo("someInfo") == Uri("http://someInfo@host:80/path?query#fragment"))

      assert(uri.withQuery(Query("param1" -> "value1")) == Uri("http://host:80/path?param1=value1#fragment"))
      assert(uri.withQuery("param1=value1") == Uri("http://host:80/path?param1=value1#fragment"))
      assert(uri.withQuery(("param1", "value1")) == Uri("http://host:80/path?param1=value1#fragment"))
      assert(uri.withQuery(Map("param1" -> "value1")) == Uri("http://host:80/path?param1=value1#fragment"))

      assert(uri.withFragment("otherFragment") == Uri("http://host:80/path?query#otherFragment"))
    }
    
    'effectivePort {
      //"return the correct effective port" 
      assert(80 == Uri("http://host/").effectivePort)
      assert(21 == Uri("ftp://host/").effectivePort)
      assert(9090 == Uri("http://host:9090/").effectivePort)
      assert(443 == Uri("https://host/").effectivePort)

      assert(4450 == Uri("https://host/").withPort(4450).effectivePort)
      assert(4450 == Uri("https://host:3030/").withPort(4450).effectivePort)
      }

    }
  }
}

