package com.resurf.graph

import com.resurf.common.RequestSummary

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class TimeRepo() {
  private val repo: mutable.Buffer[RequestSummary] = new ArrayBuffer()

  def add(t: RequestSummary) = repo.append(t)

  override def toString: String = {
    "{ " + repo.mkString(" , ") + " }"
  }

  def getRepo: List[RequestSummary] = repo.toList

  def size = repo.size
}

