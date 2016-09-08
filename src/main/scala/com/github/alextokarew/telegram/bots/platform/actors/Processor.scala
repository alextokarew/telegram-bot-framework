package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.{Actor, Props}
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{Chat, Message}

/**
  * Processes message for the specified chat.
  */
class Processor(chat: Chat) extends Actor {

  val responder = context.actorOf(Responder.props(chat.id))

  override def receive: Receive = {
    case m: Message =>
      responder ! Responder.Text(m.text.get)
  }
}

object Processor {
  def props(chat: Chat) = Props(new Processor(chat))
}
