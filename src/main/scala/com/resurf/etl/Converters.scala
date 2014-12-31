package com.resurf.etl

import java.net.URL

import com.resurf.common.WebRequest
import com.twitter.util.Time

object Converters {
  private[etl] val customSquidPattern = """(\d+\.\d+) (\d+\.\d+\.\d+\.\d+) (\d+) (\w+) "(.+)" (HTTP/\d\.\d) (\d{3}) (\d+) "(.+)" "(.+)" ([\w/\.\-]+)[\n]*""".r

  def customSquid2WebEvent(logLine: String): WebRequest =
    logLine match {
      case customSquidPattern(ts, ip, srcPort, method, url, version, code, bytes, referrer, agent, content) =>
        val optionalReferrer = referrer match {
          case "-" | "" => None
          case x => Some(new URL(x))
        }
        WebRequest(Time.fromMilliseconds((ts.toDouble*1000).toLong),method,new URL(url),
          optionalReferrer,content,bytes.toInt, Some(logLine) )
    }
}
