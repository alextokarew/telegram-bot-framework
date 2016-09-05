package com.github.alextokarew.telegram.bots.platform

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.github.alextokarew.telegram.bots.domain.Protocol
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{OkWrapper, Update}
import com.typesafe.config.ConfigFactory

/**
  * Created by alextokarew on 30.08.16.
  */
object Application extends App with Protocol {
  println("Starting bot's backend")

  implicit val system = ActorSystem("asuperusefulbot")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val token = config.getString("telegram.bot.token")
  val url = config.getString("telegram.bot.url").replace("<token>", token)

  Http().singleRequest(HttpRequest(uri = s"$url/getUpdates")).onSuccess {
    case HttpResponse(_, _, entity, _) =>
      println(entity)
      Unmarshal(entity).to[OkWrapper[Seq[Update]]].onSuccess {
        case r => println(r.result)
      }
  }

  sys.addShutdownHook {
    system.terminate()
  }
}
