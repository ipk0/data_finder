package ua.net.ipk0.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Mockito.when
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar.mock
import spray.json._

import scala.concurrent.Future

class RouteServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with JsonSupport {
  val clientMock = mock[HttpBinServiceClient]

  val testRoute = new RouteService(clientMock, new NumberFinderService(), 50).route

  it should "return OK with the appropriate JSON response" in {
    val targetOps = Some(18)

    when(clientMock.getTargetValue(targetOps)).thenReturn(Future.successful(targetOps.value))

    Post("/find", Input(Array(3, 8, 10, 14), targetOps)) ~> testRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String].parseJson shouldBe "[{\"indexes\":[1,2],\"values\":[8,10]}]".parseJson
    }
  }
}
