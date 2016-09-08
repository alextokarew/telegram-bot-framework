package com.github.alextokarew.telegram.bots.platform.test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
  * Created by alextokarev on 08.09.16.
  */
trait WireMock extends BeforeAndAfterAll { this: Suite =>

  val wireMockServer = new WireMockServer(options().port(8089))

  override def beforeAll(): Unit = {
    super.beforeAll()
    wireMockServer.start()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    wireMockServer.stop()
  }
}
