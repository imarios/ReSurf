package com.resurf.graph


import java.net.URL

import com.resurf.common.{WebRequest, RequestSummary, TestTemplate}
import com.twitter.util.Time

class RGraphTest extends TestTemplate{
  def now = Time.fromMilliseconds(System.currentTimeMillis())
  test("Simple graph testing (sanity check)") {
    val graph = new ReferrerGraph("graph-name")
    graph.addLink("a","b", RequestSummary(now,"GET","" ))
    graph.addLink("c","d", RequestSummary(now,"GET","" ))

    graph.addLink("c","d", RequestSummary(now,"GET","" ))

    graph.getLinks.toList should have length 2
    graph.getLinks.toList.flatMap(_.repo.getRepo) should have length 3
  }

  // Add log4s
  test("populating web requests") {
    val url1 = new URL("http://www.a.com")
    val url2 = new URL("http://www.b.com")
    val url3 = new URL("http://www.c.com")

    val w1 = WebRequest(now,"GET",url1,None,"txt",10)
    val w2 = WebRequest(now,"GET",url2,Some(url1),"txt",34)
    val w3 = WebRequest(now,"GET",url3,Some(url2),"txt",67)

    val graph = new ReferrerGraph("graph-name")
    graph.processRequest(w1)
    graph.processRequest(w2)
    graph.processRequest(w3)
    graph.processRequest(w3.copy(ts = now))

    graph.getLinks.foreach(println)

    graph.getNodeDetailedInfo
  }
}

