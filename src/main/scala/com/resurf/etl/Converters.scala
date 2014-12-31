package com.resurf.etl

import java.net.URL

import com.resurf.rgraph.WebRequest
import com.twitter.util.Time

object Converters {
  private[etl] val customSquidPattern = """(\d+\.\d+) (\d+\.\d+\.\d+\.\d+) (\d+) (\w+) "(.+)" (HTTP/\d\.\d) (\d{3}) (\d+) "(.+)" "(.+)" ([\w/\.\-]+)[\n]*""".r

  def customSquid2WebEvent(logLine: String): WebRequest =
    logLine match {
      case customSquidPattern(ts, ip, srcPort, method, url, version, code, bytes, referrer, agent, content) =>
        WebRequest(Time.fromMilliseconds((ts.toDouble*1000).toLong),method,new URL(url),
          new URL(referrer),content,bytes.toInt, Some(logLine) )
    }
}
