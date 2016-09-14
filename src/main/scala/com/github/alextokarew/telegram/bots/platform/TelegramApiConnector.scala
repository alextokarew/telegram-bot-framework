package com.github.alextokarew.telegram.bots.platform

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.Config

/**
  * Wraps an HTTP connection pool to the telegram API.
  */
class TelegramApiConnector(host: String, port: Int)(implicit system: ActorSystem, materializer: ActorMaterializer) {
  val flow = Http().cachedHostConnectionPool[Int](host, port)
}

object TelegramApiConnector {
  final val HOST = "host"
  final val PORT = "port"

  def apply(config: Config)(implicit system: ActorSystem, materializer: ActorMaterializer): TelegramApiConnector =
    new TelegramApiConnector(config.getString(HOST), config.getInt(PORT))
}
