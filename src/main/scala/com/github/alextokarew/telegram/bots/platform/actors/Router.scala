package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.{Actor, ActorRef, Props}
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{Chat, Update}

/**
  * The router actor, which routes messages to the corresponding Processor depending on the chat id.
  */
class Router extends Actor {

  def process(interactions: Map[Long, ActorRef]): Receive = {
    case Update(id, Some(message), None, None, None, None) =>
      val chat = message.chat
      val processor = interactions.getOrElse(chat.id, newProcessor(chat, interactions))
      processor ! message
  }

  def receive: Receive = process(Map.empty)

  private def newProcessor(chat: Chat, interactions: Map[Long, ActorRef]) = {
    val processor = context.actorOf(Processor.props(chat))
    context.become(process(interactions + (chat.id -> processor)))
    processor
  }

}

object Router {
  def props() = Props(new Router())

}