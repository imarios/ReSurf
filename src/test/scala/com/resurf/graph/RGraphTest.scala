package com.resurf.graph


import com.resurf.common.{RequestSummary, TestTemplate}
import com.twitter.util.Time

class RGraphTest extends TestTemplate{
  def now = Time.fromMilliseconds(System.currentTimeMillis())
  test("Simple graph testing (sanity check)") {
    val graph = new ReferrerGraph("graph-name")
    graph.addEdge("a","b", RequestSummary(now,"GET","" ))
    graph.addEdge("c","d", RequestSummary(now,"GET","" ))

    Thread.sleep(1200)
    graph.addEdge("c","d", RequestSummary(now,"GET","" ))

    graph.getEdges.foreach(println)
  }
}

//object Tt {
//  def main(s: Array[String]): Unit = {
//    val graph = new MultiGraph("Tutorial 1")
//    graph.addNode("a")
//    graph.addNode("b")
//    graph.addEdge("1", "a", "b")
//
//    graph.addEdge("2", "a", "b")
//
//    println(graph.getEdge("1"))
//
//    graph.display()
//  }
//}
