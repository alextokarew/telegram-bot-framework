package com.github.alextokarew.telegram.bots.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.alextokarew.telegram.bots.messages.Protocol.Responses.{GetMe, GetMeResult}
import spray.json.DefaultJsonProtocol

/**
  * Created by alextokarew on 30.08.16.
  */
trait Protocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val getMeResultFormat = jsonFormat3(GetMeResult)
  implicit val getMeFormat = jsonFormat2(GetMe)

}

object Protocol {
  object Responses {

    /**
      * {"ok":true,"result":{"id":267503591,"first_name":"A super useful Bot","username":"asuperusefulbot"}}
      */
    case class GetMe(ok: Boolean, result: GetMeResult)
    case class GetMeResult(id: Long, first_name: String, username: String)
  }
}