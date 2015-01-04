package com.resurf.common

import java.net.URL

import com.twitter.util.Time

/**
 * This is what we store for each edge
 * @param ts
 * @param method
 * @param parameters
 */
case class RequestSummary(ts: Time, method: String, parameters: String,
                          contentType: Option[String] = None, size: Option[Int] = None)


/** A representation for each Web Request */
case class WebRequest(ts: Time, method: String, url: URL, referrer: Option[URL],
                      contentType: String, size: Int, rawContent: Option[String] = None) {
  def getSummary: RequestSummary = RequestSummary(ts,method,url.getQuery,Some(contentType))
}


case class NodeProfile(fanIn: Int, fanOut: Int)