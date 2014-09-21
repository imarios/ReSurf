import sbt._
import Keys._

name := "ReSurf"

organization := "www.resurf.com"

version := "1.0"

scalaVersion in ThisBuild := "2.11.2"

scalacOptions ++= Seq("-unchecked", "-deprecation")

// Import setting to help us automatically create project using 'np'
seq(npSettings: _*)
