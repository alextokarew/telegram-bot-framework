package com.github.alextokarew.telegram.bots.platform

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.typesafe.config.Config

import scala.util.Try

/**
  * Wraps an HTTP connection pool to the telegram API.
  */
class TelegramApiConnector(host: String, port: Int)(implicit system: ActorSystem, materializer: ActorMaterializer) {
  import TelegramApiConnector._
  val connectionPoolSettings = ConnectionPoolSettings(system)

  val httpFlow = Http().cachedHostConnectionPool[RequestType](host, port, connectionPoolSettings)

  def flow[T <: RequestType]: Flow[(HttpRequest, T), (Try[HttpResponse], T), Http.HostConnectionPool] = ???
}

object TelegramApiConnector {
  final val HOST = "host"
  final val PORT = "port"

  trait RequestType

  def apply(config: Config)(implicit system: ActorSystem, materializer: ActorMaterializer): TelegramApiConnector =
    new TelegramApiConnector(config.getString(HOST), config.getInt(PORT))
}
