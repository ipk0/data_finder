package ua.net.ipk0.service

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route, ValidationRejection}
import akka.stream.Materializer
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, POST, Path, Produces}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import ua.net.ipk0.directive.{Throttle, Validate}

import scala.util.{Failure, Success}

@Path("find")
class RouteService(client: HttpBinServiceClient, service: NumberFinderService, throttleLimit: Int
  )(implicit val system: ActorSystem, val mat: Materializer) extends Throttle(throttleLimit) with JsonSupport with Validate {

  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ValidationRejection(msg, _) =>
        complete(BadRequest, msg)
    }
    .handleNotFound {
      complete(NotFound)
    }
    .result()

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: Exception =>
        extractUri { _ =>
          complete(HttpResponse(InternalServerError, entity = "Something bad happened, sorry..."))
        }
    }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Two Sum service", description = "The service involves finding two numbers in an array that add up to a given target number. It returns the corresponding values and their indices",
    requestBody = new RequestBody(required = true,
      content = Array(new Content(schema = new Schema(implementation = classOf[Input])))),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Response contains calculates data",
        content = Array(new Content(schema = new Schema(implementation = classOf[Output])))),
      new ApiResponse(responseCode = "400", description = "The request contains bad syntax or inappropriate data"),
      new ApiResponse(responseCode = "404", description = "Not Found"),
      new ApiResponse(responseCode = "429", description = "Too Many Requests"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def route: Route = Route.seal(
    post {
      pathPrefix("find"){
        throttle() {
          parseInput { req: Input =>
            onComplete(client.getTargetValue(req.target)){
              case Success(target) => complete(service.solve(req.data, target))
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        }
      }
    }
  )
}

case class Input(data: Array[Int], target: Option[Int])

case class Output(indexes: List[Int], values: List[Int])


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val requestFormat: RootJsonFormat[Input] = jsonFormat2(Input.apply)
  implicit val outputFormat: RootJsonFormat[Output] = jsonFormat2(Output.apply)

}