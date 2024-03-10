package ua.net.ipk0.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import play.api.libs.json._

import scala.concurrent.Future

class HttpBinServiceClient(httpBinUrl: String, fallback: Int)(implicit system: ActorSystem,
                                                              val mat: Materializer) extends JsonSupport {
  private implicit val ec = system.dispatcher

  def getTargetValue(originTarget: Option[Int]): Future[Int] = {
    val queryParams = originTarget.map(value => ("target", value.toString)).toMap
    val url = Uri(httpBinUrl).withQuery(Uri.Query(queryParams))

    doCall(url).map(findTarget)
      .fallbackTo(Future.successful(fallback))
  }

  def doCall(url: Uri): Future[String] = {
    Http().singleRequest(Get(url)).flatMap(Unmarshal(_).to[String])
  }

  private def findTarget(value: String): Int =
    (Json.parse(value) \ "args" \ "target").as[String].toInt
}
