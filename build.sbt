import sbt._
import Keys._

name := "ReSurf"

organization := "www.resurf.com"

version := "1.0"

scalaVersion in ThisBuild := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(clojure.settings :_*)

resolvers ++= Seq(
    "clojars.org" at "http://clojars.org/repo/",
    "maven.org" at "http://repo.maven.apache.org/",
    "twitter" at "http://maven.twttr.com/"
)

libraryDependencies ++= Seq(
     "org.clojure" % "clojure" % "1.5.1",
     "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test",
     "com.twitter" %% "util-collection" % "6.12.1",
     "org.json4s" %% "json4s-jackson" % "3.2.11",
     "org.graphstream" % "gs-core" %  "1.2",
     "org.graphstream" % "gs-algo" %  "1.1" )

// Import setting to help us automatically create project using 'np'
//seq(npSettings: _*)
