organization := "com.github.alextokarew.telegram.bots"

name := "telegram-bot-framework"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.3"
val akkaHttpVersion = "10.0.9"

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "org.scalatest" %% "scalatest" % "3.0.0" % Test,
  "com.github.tomakehurst" % "wiremock" % "2.1.10" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)