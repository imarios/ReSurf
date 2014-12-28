package com.resurf.rgraph

import java.net.URL

import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph
import com.twitter.util.Time
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class RGraphLike {
  def addNode(nodeId: String)

  def addEdge(srcId: String, dstId: String, details: RequestSummary)

  def getEdgeIdAsString(src: String, dst: String) = s"$src->$dst"
}

case class REdge(edgeId: String, srcId: String, dstId: String, repo: TimeRepo)


/**
 * This is what we store for each edge
 * @param ts
 * @param method
 * @param parameters
 */
case class RequestSummary(ts: Time, method: String, parameters: String)


/** A representation for each Web Request */
case class WebRequest(ts: Time, method: String, url: URL, referrer: URL,
                      contentType: String, size: Int, rawContent: Option[String] = None)


case class TimeRepo() {
  val repo: mutable.Buffer[RequestSummary] = new ArrayBuffer()
  def add(t: RequestSummary) = repo.append(t)
}

/** We have one such graph for each src IP*/
class ReferrerGraph(user: String) extends RGraphLike {

  private val graph: Graph = new MultiGraph(s"RG:$user")

  override def addNode(nodeId: String) = graph.addNode(nodeId)

  // Check if the edge exists
  override def addEdge(srcId: String, dstId: String, details: RequestSummary) = {
    val edgeId = getEdgeIdAsString(srcId,dstId)
    graph.addNode(srcId)
    graph.addNode(dstId)

    val edge: Edge = graph.getEdge(edgeId)
    if(edge == null) {
      //graph.addEdge(edgeId, srcId, dstId, false)
      graph.edgeAdded(user, details.ts.inNanoseconds, edgeId.toString, srcId, dstId, false)
      val newRepo = TimeRepo()
      newRepo.add(details)
      graph.getEdge(edgeId).asInstanceOf[Edge].setAttribute("a", newRepo)
    } else {
      edge.asInstanceOf[Edge].getAttribute[TimeRepo]("a").add(details)
    }
  }

  def getEdges: Iterable[REdge] = graph.getEachEdge.asScala.map{
    (x: Edge) =>
      val repo: TimeRepo = x.getAttribute("a")
      REdge(x.getId,x.getSourceNode.toString, x.getTargetNode.toString, repo)
  }
}
