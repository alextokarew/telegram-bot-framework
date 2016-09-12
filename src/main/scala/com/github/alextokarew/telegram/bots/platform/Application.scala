package com.github.alextokarew.telegram.bots.platform

import akka.actor.{ActorSystem, Props}
import com.github.alextokarew.telegram.bots.domain.Protocol
import com.github.alextokarew.telegram.bots.platform.actors.{Poller, Printer, Router}
import com.typesafe.config.ConfigFactory

/**
  * Created by alextokarew on 30.08.16.
  */
object Application extends App with Protocol {
  println("Starting bot's backend") //TODO log

  implicit val system = ActorSystem("asuperusefulbot")
  implicit val executor = system.dispatcher

  val config = ConfigFactory.load()
  val token = config.getString("telegram.bot.token")
  val url = config.getString("telegram.bot.url").replace("<token>", token)
  val timeout = config.getInt("telegram.bot.timeout")

  val printer = system.actorOf(Props[Printer])
  val router = system.actorOf(Router.props())
  val poller = system.actorOf(Poller.props(url, timeout, router))

  sys.addShutdownHook {
    system.terminate()
  }
}
