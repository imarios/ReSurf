package com.resurf.rgraph

import com.resurf.common.TestTemplate

class RGraphTest extends TestTemplate{
  case class EdgeDetails(timeStamps: List[Long])
  test("simple graph") {
    val g =  new RGraph
    g.addNode("a")
    g.addNode("b")
    g.addEdge("1","a","b") // TODO here we can actaully add the edge details object where we can access all the timestamps we want



    g.getEdges.toList should have length 1



    g.getEdges.toList.head.getAttribute[EdgeDetails]("a")

  }
}
