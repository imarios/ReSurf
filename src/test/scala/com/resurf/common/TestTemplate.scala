package com.resurf.common


import org.apache.log4j._
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.Matchers

class TestTemplate extends FunSuite with BeforeAndAfter with Matchers {

  protected def log4jToConsoleAndNewLevel(newLevel: org.apache.log4j.Level) = {
    val rootLogger = org.apache.log4j.Logger.getRootLogger
    rootLogger.removeAllAppenders()
    rootLogger.setLevel(newLevel)
    rootLogger.addAppender(new ConsoleAppender(new PatternLayout("[%d{dd/MM/yy hh:mm:ss:sss z}] %5p %c{2}: %m%n")))
  }

  before {
    //org.apache.log4j.BasicConfigurator.configure()
    log4jToConsoleAndNewLevel(org.apache.log4j.Level.OFF)
  }
}
