package ua.net.ipk0.service

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Mockito.{doReturn, spy}
import org.scalatest.{FlatSpec, Matchers}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class HttpBinServiceClientSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  it should "parse mocked response and return the appropriate value" in {
    val targetOps = Some(18)
    val url = "http://localhost"

    val testClass = spy(new HttpBinServiceClient(url, 5))
    doReturn(Future.successful("{ \"args\": { \"target\": \"4\" }}")).when(testClass).doCall(Uri(url + "?target=18"))

    val actual = testClass.getTargetValue(targetOps)
    Await.result(actual, Duration(1, TimeUnit.SECONDS)) shouldBe 4
  }

  it should "use fallback value" in {
    val targetOps = None
    val url = "http://localhost"

    val testClass = spy(new HttpBinServiceClient(url, 5))
    doReturn(Future.failed(new RuntimeException())).when(testClass).doCall(Uri(url))

    val actual = testClass.getTargetValue(targetOps)
    Await.result(actual, Duration(1, TimeUnit.SECONDS)) shouldBe 5
  }
}
