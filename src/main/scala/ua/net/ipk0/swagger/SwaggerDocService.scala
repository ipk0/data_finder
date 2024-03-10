package ua.net.ipk0.swagger

import com.github.swagger.akka.ui.SwaggerHttpWithUiService
import ua.net.ipk0.service.RouteService

class SwaggerDocService(hostAndPort: String) extends SwaggerHttpWithUiService {

  override def apiClasses: Set[Class[_]] = Set(classOf[RouteService])

  override val host = hostAndPort
}
