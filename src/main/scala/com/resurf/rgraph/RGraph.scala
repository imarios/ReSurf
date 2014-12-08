package com.resurf.rgraph

import org.graphstream.graph._
import org.graphstream.graph.implementations.SingleGraph
import scala.collection.JavaConverters._

abstract class RGraphLike {
  def addNode(nodeId: String)

  def addEdge(edgeId: String, srcId: String, dstId: String)
}

// TODO: the graph will be nodes as the URLs and connections are between URLs using referrer strings

class RGraph extends RGraphLike {
  // TODO: We need this since we can have URLs without referrers (which will be singleton nodes)
  override def addNode(nodeId: String): Unit = graph.addNode(nodeId)

  override def addEdge(edgeId: String, srcId: String, dstId: String): Unit =
    graph.edgeAdded("Tutorial 1",12323,edgeId,srcId,dstId,false)
    //graph.addEdge(edgeId, srcId, dstId)

  val graph: Graph = new SingleGraph("Tutorial 1")
  graph.setAutoCreate(true)

  def getEdges: Iterable[Edge] = graph.getEachEdge.asScala
}
