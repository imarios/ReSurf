import sbt._
import Keys._

name := "ReSurf"

organization := "www.resurf.com"

version := "1.0"

scalaVersion in ThisBuild := "2.11.2"

scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(clojure.settings :_*)

libraryDependencies ++= Seq(
     "org.clojure" % "clojure" % "1.5.1",
     //"org.scalautils" % "scalautils_2.11" % "2.1.2",
     "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
     "org.graphstream" % "gs-core" %  "1.2",
     "org.graphstream" % "gs-algo" %  "1.1" )

// Import setting to help us automatically create project using 'np'
//seq(npSettings: _*)
