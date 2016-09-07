package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.{Actor, Props}
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.Chat

/**
  * Processes message for the specified chat.
  */
class Processor(chat: Chat) extends Actor {
  override def receive: Receive = {
    case m => println(s"${chat.id}, $m")
  }
}

object Processor {
  def props(chat: Chat) = Props(new Processor(chat))
}
