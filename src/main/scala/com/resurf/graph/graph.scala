package com.resurf.graph

import java.lang

import com.resurf.common._
import com.twitter.util.Duration
import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.algorithm.ConnectedComponents

import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory


abstract class RGraphLike {
  def addNode(nodeId: String, details: Option[RequestSummary])

  def addLink(srcId: String, dstId: String, details: RequestSummary)

  def getLinkIdAsString(src: String, dst: String) = s"$src->$dst"

  def processRequest(newEvent: WebRequest): Unit
}

case class Link(linkId: String, srcId: String, dstId: String, repo: RequestRepository)



/** We have one such graph for each src IP */
class ReferrerGraph(user: String) extends RGraphLike {

  val SummaryAttribute = "Requests"

  private[this] lazy val logger = LoggerFactory.getLogger(this.getClass)
  private val internalGraph: Graph = new MultiGraph(s"RG:$user", false, true)

  override def addNode(nodeId: String, details: Option[RequestSummary] = None) = {
    details match {
      case None =>
        internalGraph.addNode(nodeId).asInstanceOf[Node]
      case Some(request) =>
        logger.debug(s"Adding node $nodeId with details $details")
        val node: Node = internalGraph.getNode(nodeId)
        if (node == null) {
          logger.debug("First time seeing node: {}", nodeId)
          val newRepo = new RequestRepository()
          newRepo.add(request)
          this.addNode(nodeId)
          internalGraph.getNode(nodeId).asInstanceOf[Node].setAttribute(SummaryAttribute, newRepo)
        } else {
          logger.debug("Updating existing node {}", nodeId)
          val attr: RequestRepository = node.asInstanceOf[Node].getAttribute[RequestRepository](SummaryAttribute)
          if (attr == null) internalGraph.getNode(nodeId).asInstanceOf[Node].setAttribute(SummaryAttribute, attr)
          else attr.add(request)
        }
    }
  }

  override def addLink(srcId: String, dstId: String, details: RequestSummary) = {
    logger.debug(s"Adding edge from $srcId to $dstId")
    val edgeId = getLinkIdAsString(srcId, dstId)

    this.addNode(srcId)
    this.addNode(dstId, Some(details))

    // Gets the edge if it exists, else it returns null
    val edge: Edge = internalGraph.getEdge(edgeId)
    if (edge == null) {
      logger.debug(s"New edge from $srcId to $dstId")
      internalGraph.addEdge(edgeId.toString, srcId, dstId, true)
      val newRepo = new RequestRepository()
      newRepo.add(details)
      val e = internalGraph.getEdge(edgeId.toString).asInstanceOf[Edge]
      assert(e != null, "Edge was just added, it should not be null")
      e.setAttribute(SummaryAttribute, newRepo)
    } else {
      edge.asInstanceOf[Edge].getAttribute[RequestRepository](SummaryAttribute).add(details)
    }
  }

  private[this] def getInternalNodes = internalGraph.getEachNode.asScala.map((n: Node) => n)

  private[this] def getTimeRepo(element: Element): Option[RequestRepository] = {
    val at: RequestRepository = element.getAttribute(SummaryAttribute)
    if (at == null) None
    else Some(at.asInstanceOf[RequestRepository])
  }


  def getGraphSummary: GraphSummary = {
    val cc = new ConnectedComponents()
    cc.init(internalGraph)

    GraphSummary(
      nodeCount = internalGraph.getNodeCount,
      linkCount = internalGraph.getEdgeCount,
      connectedComponentCount = cc.getConnectedComponentsCount
    )
  }

  private def getNodeOutgoingRequests(n: Node): Seq[RequestSummary] = {
    val edges = n.getEachLeavingEdge.asInstanceOf[lang.Iterable[Edge]]
    edges.asScala.map{
      (x: Edge) => getTimeRepo(x).get
    }.flatMap(_.getRepo).toSeq.sorted
  }

  // TODO: Here we return all the details of the node.
  // the timings, the degrees, the content types, etc.
  def getNodeDetailedInfo: Iterable[NodeProfile] = {
    for (n <- getInternalNodes) yield {
      // Get incoming requests to this node
      val incomingRequests = getTimeRepo(n)

      // - Get outgoing requests (that have the node as referrer)
      val outgoingRequests = getNodeOutgoingRequests(n)

      // - For each incoming request find time gap to next outgoing request
      val avgDelay: Option[Duration] = incomingRequests match {
        case None => None
        case Some(data) =>
          data.getRepo.flatMap {
            incoming => RequestRepository.getDurationToNextRequest(incoming, outgoingRequests)
          } match {
            case Nil => None
            case k : Seq[Duration] => Some(averageDuration(k)) // Todo: find the average or median here
          }
      }

      val profile = NodeProfile(
        fanIn = n.getInDegree,
        fanOut = n.getOutDegree,
        totalRequest = incomingRequests match {
          case None => 0;
          case Some(t) => t.size
        },
        passThroughDelay = avgDelay
      )
      logger.debug(s"On ${n.getId} $profile")
      profile
    }
  }



  def getLinks: Iterable[Link] = internalGraph.getEachEdge.asScala.map {
    (x: Edge) =>
      val repo: RequestRepository = x.getAttribute(SummaryAttribute)
      Link(x.getId, x.getSourceNode.toString, x.getTargetNode.toString, repo)
  }

  def viz = internalGraph.display()

  override def processRequest(newEvent: WebRequest) = {
    val referrer = newEvent.referrer
    referrer match {
      case None =>
        // There is no referrer, so we just update the node
        addNode(newEvent.url.toString, Some(newEvent.getSummary))
      case Some(ref) =>
        // There is a referrer, so we can update the link (from referrer to target)
        addLink(ref.toString, newEvent.url.toString, newEvent.getSummary)
    }
  }
}

object ReferrerGraph{

}
