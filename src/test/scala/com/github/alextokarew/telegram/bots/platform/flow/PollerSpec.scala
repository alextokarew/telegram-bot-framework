package com.github.alextokarew.telegram.bots.platform.flow

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import com.github.alextokarew.telegram.bots.domain.Protocol.Responses.Update
import com.github.alextokarew.telegram.bots.platform.test.WireMock
import com.github.tomakehurst.wiremock.client.{WireMock => WM}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

class PollerSpec extends TestKit(ActorSystem("PollerSpec")) with WordSpecLike with WireMock with Matchers {

  val timeout = 30

  "Poller" should {
    "poll getUpdates method" in {
      implicit val materializer = ActorMaterializer()
      val apiConnector = new ApiConnector {
        def flow[T] = Http().cachedHostConnectionPool[T]("localhost", wireMockServer.port())
      }

      val pollerSource = Poller.updatesSource("/botToken", timeout, apiConnector)

      pollerSource.runWith(Sink.actorRef(testActor, NotUsed))

      val messages = receiveWhile(messages = 4) {
        case u: Update => u
      }

      wireMockServer.verify(WM.getRequestedFor(
        WM.urlPathEqualTo("/botToken/getUpdates"))
          .withQueryParam("offset", WM.equalTo("0"))
          .withQueryParam("timeout", WM.equalTo(timeout.toString))
      )

      messages should have size 4

      expectNoMsg(1.second)

      wireMockServer.verify(WM.getRequestedFor(
        WM.urlPathEqualTo("/botToken/getUpdates"))
        .withQueryParam("offset", WM.equalTo("219169025"))
        .withQueryParam("timeout", WM.equalTo(timeout.toString))
      )
    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }
}
