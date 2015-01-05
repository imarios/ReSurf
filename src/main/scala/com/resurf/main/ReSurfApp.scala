package com.resurf.main

import java.net.URL

import com.resurf.common.WebRequest
import com.resurf.graph.ReferrerGraph
import com.twitter.util.Time

object ReSurfApp extends App{

  def sanityTest() = {
    def now = Time.fromMilliseconds(System.currentTimeMillis())
    val url1 = new URL("http://www.a.com")
    val url2 = new URL("http://www.b.com")
    val url3 = new URL("http://www.c.com")

    val w1 = WebRequest(now,"GET",url1,None,"txt",10)
    val w2 = WebRequest(now,"GET",url2,Some(url1),"txt",34)
    val w3 = WebRequest(now,"GET",url3,Some(url2),"txt",67)
    val w4 = WebRequest(now,"GET",url3,Some(url1),"txt",67)

    val graph = new ReferrerGraph("graph-name")
    graph.processRequest(w1)
    graph.processRequest(w2)
    graph.processRequest(w3)
    graph.processRequest(w3.copy(ts = now))
    graph.processRequest(w4)

    graph.viz
  }

  println("Hello world")
  sanityTest()
}


