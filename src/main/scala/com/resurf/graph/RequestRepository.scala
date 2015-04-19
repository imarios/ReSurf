package com.resurf.graph

import com.resurf.common.RequestSummary
import com.twitter.util.Duration
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
 * A summary of all the requests of an edge or a node.
 * This is short enough to keep several of this on an edge.
 */
class RequestRepository() {
  private val repo: mutable.Buffer[RequestSummary] = new ArrayBuffer()

  def add(t: RequestSummary) = repo.append(t)

  override def toString: String = {
    "{ " + repo.mkString(" , ") + " }"
  }

  def getRepo: List[RequestSummary] = repo.toList

  def size = repo.size
}

object RequestRepository {
  /**
   * Given a request and a large repository of requests find the time it took from the
   * target request until the closets outgoing request.
   * @param incomingRequest RequestSummary
   * @param orderedOutgoingRequests Seq[RequestSummary]
   * @return Option[Duration]
   */
  def getDurationToNextRequest(incomingRequest: RequestSummary,
                         orderedOutgoingRequests: Seq[RequestSummary]): Option[Duration] = {
    val targetTs = incomingRequest.ts
    orderedOutgoingRequests.find(_.ts > targetTs) match {
      case None => None
      case Some(next) => Some(next.ts - targetTs)
    }
  }
}