package com.github.alextokarew.telegram.bots.platform.flow

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Source}
import akka.stream.{Materializer, SourceShape}
import com.github.alextokarew.telegram.bots.domain.Protocol
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.{OkWrapper, Update}

import scala.util.{Success, Try}

object Poller extends Protocol {
  type Response = OkWrapper[Seq[Update]]

  case class Poll(nextId: Long) extends ApiConnector.RequestType

  /**
    * Creates a source that long-polls for updates via telegram's API getUpdates method and emits updates.
    * @param url base telegram url
    * @param timeout poll timeout in seconds
    * @return Source[Update]
    */
  def updatesSource(
    url: String,
    timeout: Int,
    apiConnector: ApiConnector)(implicit s: ActorSystem, fm: Materializer) = {
    import GraphDSL.Implicits._
    import s._

    Source.fromGraph {
      GraphDSL.create() { implicit b =>
        val init = Source.single(Poll(0))
        val merge = b.add(Merge[Poll](2))
        val request = b.add {
          Flow[Poll].map { poll =>
            HttpRequest(uri = s"$url/getUpdates?timeout=$timeout&offset=${poll.nextId}") -> poll
          }
        }
        val http = b.add(apiConnector.flow[Poll])
        val response = b.add {
          //TODO: deal with failures and with bad http status codes, maybe write to log and go to the feedback loop
          Flow[(Try[HttpResponse], Poll)].collect {
            case (Success(HttpResponse(StatusCodes.OK, _, entity, _)), poll) => entity -> poll
          }.mapAsync(1) {
            case (entity, poll) => Unmarshal(entity).to[Response].map(_ -> poll)
          }
        }

        val broadcast = b.add(Broadcast[(Response, Poll)](2))

        val sequencer = b.add {
          Flow[(Response, Poll)].mapConcat {
            case (resp, _) => resp.result.toList
          }
        }

        val feedback = b.add {
          Flow[(Response, Poll)]
            .map {
              case (resp, poll) => if (resp.result.nonEmpty) Poll(resp.result.last.update_id + 1) else poll
            }
        }

        // format: OFF
        init ~> merge ~> request ~> http ~> response ~> broadcast ~> sequencer
                merge <~ feedback                    <~ broadcast
        // format: ON

        SourceShape(sequencer.out)
      }
    }
  }
}