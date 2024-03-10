package ua.net.ipk0.directive

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.model.StatusCodes
import io.github.bucket4j.{Bandwidth, Bucket, Refill}

import java.time.Duration

abstract class Throttle(tpm: Int) {

  private val rateLimiter: Bucket = Bucket.builder.addLimit(
    Bandwidth.classic(tpm, Refill.intervally(tpm, Duration.ofMinutes(1)))
  ).build

  def throttle(): Directive0 = {
      if (rateLimiter.tryConsume(1)) {
        pass
      } else {
        complete(StatusCodes.TooManyRequests)
      }
}}
