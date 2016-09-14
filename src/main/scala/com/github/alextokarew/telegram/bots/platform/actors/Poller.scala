package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.alextokarew.telegram.bots.domain.Protocol
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{OkWrapper, Update}
import com.github.alextokarew.telegram.bots.platform.TelegramApiConnector

import scala.util.{Failure, Success}

/**
  * An actor that long-polls for updates via telegram's API getUpdates method.
  */
class Poller(url: String, timeout: Int, router: ActorRef, apiConnector: TelegramApiConnector) extends Actor with Protocol {
  import Poller._
  import akka.pattern.pipe
  import context.dispatcher
  implicit val materializer = ActorMaterializer()

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    self ! Poll
  }

  def process(nextId: Long): Receive = {
    case Poll =>
      Source.single(HttpRequest(uri = s"$url/getUpdates?timeout=$timeout&offset=$nextId") -> 42)
        .via(apiConnector.flow)
        .map(_._1.get)
        .runWith(Sink.head)
        .pipeTo(self)

    case HttpResponse(StatusCodes.OK, _, entity, _) =>
      Unmarshal(entity).to[OkWrapper[Seq[Update]]].andThen {
        case Success(wrapper) =>
          wrapper.result.foreach(update => router ! update)
          if (wrapper.result.nonEmpty) {
            self ! LastUpdateId(wrapper.result.last.update_id)
          }
          self ! Poll

        case Failure(e) => e.printStackTrace() //TODO - write to log
      }

    case LastUpdateId(id) =>
      context.become(process(id + 1))
  }

  override def receive: Receive = process(0)
}

object Poller {
  def props(url: String, timeout: Int, router: ActorRef, apiConnector: TelegramApiConnector) =
    Props(new Poller(url, timeout, router, apiConnector))

  case object Poll
  case class LastUpdateId(id: Long)
}