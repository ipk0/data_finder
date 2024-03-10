package ua.net.ipk0.directive

import akka.http.scaladsl.server.Directives.{as, entity, pass, reject, tprovide, provide}
import akka.http.scaladsl.server.{Directive0, Directive1, ValidationRejection}
import ua.net.ipk0.service.{Input, JsonSupport}

trait Validate extends JsonSupport {

  def parseInput: Directive1[Input] = {
    entity(as[Input]).flatMap { req: Input =>
      if (req.data.length < 2 || req.data.length > 10000){
        reject(ValidationRejection("The length of the array must be greater than 1 and less than 10000"))
      } else {
        provide(req)
      }
    }
  }
}
