package com.github.alextokarew.telegram.bots.platform.actors

import com.github.alextokarew.telegram.bots.platform.test.WireMock
import org.scalatest.WordSpec

class PollerSpec extends WordSpec with WireMock {

  "Poller" should {
    "poll getUpdates method every 5 minutes" in {

      println("OK" + wireMockServer.port())
    }
  }

}
