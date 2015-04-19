import sbt._
import Keys._

name := "ReSurf"

organization := "www.cs.ucr.edu/~marios"

version := "1.0"

scalaVersion in ThisBuild := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(clojure.settings :_*)

resolvers ++= Seq(
    "clojars.org" at "http://clojars.org/repo/",
    "maven.org" at "http://repo.maven.apache.org/",
    "twitter" at "http://maven.twttr.com/"
)

lazy val slf4jDependencies = Seq(
    "log4j" % "log4j" % "1.2.16",
    "org.slf4j" % "slf4j-log4j12" % "1.7.5",
    "org.slf4j" % "slf4j-api" % "1.7.5"
)

libraryDependencies ++= Seq(
     // Support for clojure
     "org.clojure" % "clojure" % "1.5.1",
     // Using the ScalaTest library (only for testing)
     "org.scalatest" %% "scalatest" % "2.2.1" % "test",
     // Using the very useful utils by twitter
     "com.twitter" %% "util-collection" % "6.12.1",
     // Processing JSON
     "org.json4s" %% "json4s-jackson" % "3.2.11",
     // Reactive
     "com.netflix.rxjava" % "rxjava-scala" % "0.19.1", 
     // Manipulating in memory Graphs
     "org.graphstream" % "gs-core" %  "1.2",
     "org.graphstream" % "gs-algo" %  "1.1" ) ++ slf4jDependencies

// Import setting to help us automatically create project using 'np'
//seq(npSettings: _*)
