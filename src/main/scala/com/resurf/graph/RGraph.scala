package com.resurf.graph

import com.resurf.common.{WebRequest, RequestSummary}
import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class RGraphLike {
  def addNode(nodeId: String)

  def addEdge(srcId: String, dstId: String, details: RequestSummary)

  def getLinkIdAsString(src: String, dst: String) = s"$src->$dst"

  def processRequest(newEvent: WebRequest): Unit
}

case class Link(linkId: String, srcId: String, dstId: String, repo: TimeRepo)

class TimeRepo() {
  val repo: mutable.Buffer[RequestSummary] = new ArrayBuffer()
  def add(t: RequestSummary) = repo.append(t)

  override def toString: String = {
    "{ " + repo.mkString(" , ") + " }"
  }
}

/** We have one such graph for each src IP*/
class ReferrerGraph(user: String) extends RGraphLike {

  private val graph: Graph = new MultiGraph(s"RG:$user",false,true)

  // The node id is either the URL or the Host
  override def addNode(nodeId: String) = graph.addNode(nodeId)

  override def addEdge(srcId: String, dstId: String, details: RequestSummary) = {
    val edgeId = getLinkIdAsString(srcId,dstId)
    // If node already exists, nothing will be added here
    graph.addNode(srcId)
    graph.addNode(dstId)

    // Gets the edge if it exists, else it returns null
    val edge: Edge = graph.getEdge(edgeId)
    if(edge == null) {
      //graph.addEdge(edgeId, srcId, dstId, false)
      graph.edgeAdded(user, details.ts.inNanoseconds, edgeId.toString, srcId, dstId, false)
      val newRepo = new TimeRepo()
      newRepo.add(details)
      graph.getEdge(edgeId).asInstanceOf[Edge].setAttribute("a", newRepo)
    } else {
      edge.asInstanceOf[Edge].getAttribute[TimeRepo]("a").add(details)
    }
  }

  // TODO: Here we return all the details of the node.
  // the timings, the degrees, the content types, etc.
  def getNodeDetailedInfo = ???

  def getEdges: Iterable[Link] = graph.getEachEdge.asScala.map{
    (x: Edge) =>
      val repo: TimeRepo = x.getAttribute("a")
      Link(x.getId,x.getSourceNode.toString, x.getTargetNode.toString, repo)
  }

  def viz = graph.display()

  override def processRequest(newEvent: WebRequest) = {
    // When we get a web-request it's transformed to a nodes, edges, and a RequestSummary

    // The src is the referrer. If the referrer does not exist it means we don't have a --> b but just b
    // This means that we need to keep the RequestSummary both at the Links and at the Node
    // The Node will keep all it's requests in a separate collection.
    val src = ???
    val dst = ???

    // Optionally ---> Create a Link to connect src and dst
    val edge = ???

    ???
  }
}
