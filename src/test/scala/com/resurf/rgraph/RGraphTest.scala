package com.resurf.rgraph

import com.resurf.common.TestTemplate
import com.twitter.util.Time
import org.graphstream.graph.implementations.MultiGraph

class RGraphTest extends TestTemplate{
  test("simple graph") {
    val graph = new ReferrerGraph("a")
    graph.addEdge("a","b",
      RequestSummary( Time.fromMilliseconds(System.currentTimeMillis()),"GET","" )
    )
    graph.addEdge("c","d",
      RequestSummary( Time.fromMilliseconds(System.currentTimeMillis()),"GET","" )
    )
    graph.getEdges.foreach(println)
  }

  test("sdasd") {
    val graph = new MultiGraph("Tutorial 1")
    graph.addNode("a")
    graph.addNode("b")

    graph.addEdge("1", "a", "b")

    graph.addEdge("2", "a", "b")

    println(graph.getEdge("1"))
  }
}

object Tt {
  def main(s: Array[String]): Unit = {
    val graph = new MultiGraph("Tutorial 1")
    graph.addNode("a")
    graph.addNode("b")
    graph.addEdge("1", "a", "b")

    graph.addEdge("2", "a", "b")

    println(graph.getEdge("1"))

    graph.display()
  }
}
