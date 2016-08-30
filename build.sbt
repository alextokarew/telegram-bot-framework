organization := "com.github.alextokarew.telegram.bots"

name := "asuperusefulbot"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.9"

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion
)