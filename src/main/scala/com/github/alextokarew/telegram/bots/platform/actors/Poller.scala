package com.github.alextokarew.telegram.bots.platform.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.github.alextokarew.telegram.bots.domain.Protocol
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{OkWrapper, Update}

import scala.util.{Failure, Success}

/**
  * An actor that long-polls for updates via telegram's API getUpdates method.
  */
class Poller(url: String, timeout: Int, router: ActorRef) extends Actor with Protocol {
  import Poller._
  import akka.pattern.pipe
  import context.dispatcher
  implicit val materializer = ActorMaterializer()

  val http = Http(context.system)

  //TODO:
  //  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
//    Http().outgoingConnection("akka.io")
//  val responseFuture: Future[HttpResponse] =
//    Source.single(HttpRequest(uri = "/"))
//      .via(connectionFlow)
//      .runWith(Sink.head)
//
//  responseFuture.andThen {
//    case Success(_) => println("request succeded")
//    case Failure(_) => println("request failed")
//  }.andThen {
//    case _ => system.terminate()
//  }


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    self ! Poll
  }

  def process(nextId: Long): Receive = {
    case Poll =>
      http.singleRequest(HttpRequest(uri = s"$url/getUpdates?timeout=$timeout&offset=$nextId")).pipeTo(self)

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
  def props(url: String, timeout: Int, router: ActorRef) = Props(new Poller(url, timeout, router))

  case object Poll
  case class LastUpdateId(id: Long)
}