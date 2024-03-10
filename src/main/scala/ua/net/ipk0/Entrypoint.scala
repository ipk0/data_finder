package ua.net.ipk0

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import sun.misc.Signal
import ua.net.ipk0.service.{NumberFinderService, HttpBinServiceClient, RouteService}
import ua.net.ipk0.swagger.SwaggerDocService

import scala.util.Try

object Entrypoint extends App with LazyLogging with RouteConcatenation {
  implicit val system = ActorSystem("data-finder-system")
  implicit val materializer = Materializer.createMaterializer(system)

  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load

  val appConfig = config.getConfig("data-finder")
  val interface = appConfig.getString("server.interface")
  val port = appConfig.getInt("server.port")
  val throttleLimit = appConfig.getInt("throttle-per-minute")
  val targetFallback = appConfig.getInt("target-fallback")
  val httpBinUrl = appConfig.getString("httpbin-url")

  val bindingFuture = Http().newServerAt(interface, port)
    .bind(new SwaggerDocService(interface + ":" + port).routes
      ~ new RouteService(new HttpBinServiceClient(httpBinUrl, targetFallback), new NumberFinderService(), throttleLimit).route)

  logger.info(s"Server online at $interface:$port")

  List("USR2").foreach(sig =>
    Try(new Signal(sig)) foreach { s ⇒
      try Signal.handle(
        s,
        (signal: Signal) => {
          logger.info(s"[GracefullyShutdown] >>>>>>> Signal $signal received")
          bindingFuture
            .flatMap(_.unbind())
            .onComplete(_ => system.terminate())
        })
      catch {
        case ex: Throwable ⇒
          logger.warn("[GracefullyShutdown] Error registering handler for signal: {}", s, ex)
      }
    })
}