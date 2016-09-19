package com.github.alextokarew.telegram.bots.platform.flow

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

class Responder(chatId: Long) extends Actor {
  import Responder._

  implicit val materializer = ActorMaterializer()

  val http = Http(context.system)

  override def receive: Receive = {
    case Text(text) =>
      val uri = Uri(s"$url/sendMessage").withQuery(Uri.Query("chat_id" -> chatId.toString, "text" -> text))
      http.singleRequest(HttpRequest(uri = uri))
  }

}

object Responder {
  val config = ConfigFactory.load()

  val token = config.getString("telegram.bot.token")
  val url = config.getString("telegram.bot.url").replace("<token>", token)

  def props(chatId: Long) = Props(new Responder(chatId))


  case class Text(text: String)
}
