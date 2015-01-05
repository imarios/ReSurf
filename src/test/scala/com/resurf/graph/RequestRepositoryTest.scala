package com.resurf.graph

import com.resurf.common._
import com.twitter.util.{Duration, Time}

class RequestRepositoryTest extends TestTemplate {
  test("Adding requests") {
    val myRepo = new RequestRepository()
    myRepo add
      RequestSummary(getCurrentTime,"GET","",Some("txt"),None)
    myRepo add
      RequestSummary(getCurrentTime,"GET","",Some("txt"),None)
    myRepo should have size 2
  }

  def mkRequest(ts: Time) = RequestSummary(ts,"GET","",Some("txt"),None)

  test("Delay to next request") {
    val now = getCurrentTime
    val target = RequestSummary(now,"GET","",Some("txt"),None)

    val myRepo = new RequestRepository()
    myRepo.add(mkRequest(now + Duration.fromSeconds(3)))
    myRepo.add(mkRequest(now + Duration.fromSeconds(5)))
    myRepo.add(mkRequest(now + Duration.fromSeconds(7)))

    val duration =
      RequestRepository.getDurationToNextRequest(target,myRepo.getRepo)
    duration.get.inSeconds shouldBe 3
  }
}
