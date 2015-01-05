package com.resurf.graph

import com.resurf.common.RequestSummary
import com.twitter.util.Duration

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


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
  def getDurationToNextRequest(incomingRequest: RequestSummary,
                         orderedOutgoingRequests: Seq[RequestSummary]): Option[Duration] = {
    val targetTs = incomingRequest.ts
    orderedOutgoingRequests.find(_.ts > targetTs) match {
      case None => None
      case Some(next) => Some(next.ts - targetTs)
    }
  }
}