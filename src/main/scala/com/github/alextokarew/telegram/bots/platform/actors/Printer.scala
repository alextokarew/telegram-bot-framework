package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.Actor

/**
  * Created by alextokarev on 05.09.16.
  * TODO: deleteme
  */
class Printer extends Actor {
  override def receive: Receive = {
    case m => println(m)
  }
}
