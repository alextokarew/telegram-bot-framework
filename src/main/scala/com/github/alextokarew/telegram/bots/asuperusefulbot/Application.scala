package com.github.alextokarew.telegram.bots.asuperusefulbot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol

/**
  * Created by alextokarev on 30.08.16.
  */
object Application extends App with SprayJsonSupport with DefaultJsonProtocol {
  println("Starting bot's backend")

  implicit val system = ActorSystem("asuperusefulbot")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val token = config.getString("telegram.bot.token")
  val getMeUrl = config.getString("telegram.bot.url").replace("<token>", token)

  implicit val responseFormat = jsonFormat1(Response)

  Http().singleRequest(HttpRequest(uri = getMeUrl)).onSuccess {
    case HttpResponse(_, _, entity, _) =>
      Unmarshal(entity).to[Response].onSuccess {
        case r => println(r)
      }
  }

  sys.addShutdownHook {
    system.terminate()
  }

//  {"ok":true,"result":{"id":267503591,"first_name":"A super useful Bot","username":"asuperusefulbot"}}
  case class Response(ok: Boolean)
}
