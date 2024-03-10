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

  it should "return BadRequest within array size limitation" in {
    Post("/find", Input(Array(3), Some(18))) ~> testRoute ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String] shouldBe "The length of the array must be greater than 1 and less than 10000"
    }
  }

  it should "return ToManyRequest within throttling feature" in {
    val targetOps = Some(17)
    val clientMock = mock[HttpBinServiceClient]
    when(clientMock.getTargetValue(targetOps)).thenReturn(Future.successful(targetOps.value))

    val throttleLimit = 5
    val testRoute = new RouteService(clientMock, new NumberFinderService(), throttleLimit).route

    (1 to throttleLimit).foreach { _ =>
      Post("/find", Input(Array(3, 5), Some(17))) ~> testRoute
    }

    Post("/find", Input(Array(3, 5), Some(17))) ~> testRoute ~> check {
      status shouldEqual StatusCodes.TooManyRequests
      responseAs[String] shouldBe "The user has sent too many requests in a given amount of time."
    }
  }
}
