package com.resurf.graph

import java.net.URL
import com.resurf.common.{WebRequest, RequestSummary, TestTemplate}
import com.twitter.util.Time

class ReferrerGraphTest extends TestTemplate {
  def now = Time.fromMilliseconds(System.currentTimeMillis())

  test("Simple graph testing (sanity check)") {
    val graph = new ReferrerGraph("graph-name")
    graph.addLink("a", "b", RequestSummary(now, "GET", ""))
    graph.addLink("c", "d", RequestSummary(now, "GET", ""))

    graph.addLink("c", "d", RequestSummary(now, "GET", ""))

    graph.getLinks.toList should have length 2
    graph.getLinks.toList.flatMap(_.repo.getRepo) should have length 3
  }

  test("Populating web requests and testing graph creation") {
    val url1 = new URL("http://www.1.com")
    val url2 = new URL("http://www.2.com")
    val url3 = new URL("http://www.3.com")

    val w1 = WebRequest(now, "GET", url1, None, "txt", 10)
    val w2 = WebRequest(now, "GET", url2, Some(url1), "txt", 34)
    val w3 = WebRequest(now, "GET", url3, Some(url2), "txt", 67)

    val graph = new ReferrerGraph("graph-name")
    graph.processRequest(w1)
    graph.processRequest(w2)
    graph.processRequest(w3)

    graph.getLinks.toSeq should have length 2
    graph.getNodeDetailedInfo.toSeq should have length 3

    println("the gap is now very hacky. needs more work")
    graph.getNodeDetailedInfo.toSeq.foreach(println)

    graph.getGraphSummary should have(
      'nodeCount(3),
      'linkCount(2),
      'connectedComponentCount(1)
    )
  }
}

