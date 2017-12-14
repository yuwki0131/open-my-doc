package ex2

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalaz.{-\/, \/, \/-, Monad}
import scalaz.EitherT.eitherT
import scala.concurrent.ExecutionContext.Implicits.global

object FE {
  implicit val FutureMonad = new Monad[Future] {
    def point[A](a: => A): Future[A] = Future { a }
    def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f
  }

  def dummyDao(n: Int)(implicit ec : ExecutionContext) : Future[\/[String, Int]] = {
    if (n < 0) {
      Future.failed(new ArithmeticException("n must not be negative"))
    } else {
      Future.successful(
        if(0 < n) {
          \/-(n - 1)
        } else {
          -\/("n is zero")
        }
      )
    }
  }

  def f(init: Int):Future[String \/ Int] = {for {
    value1 <- eitherT(dummyDao(init))
    value2 <- eitherT(dummyDao(value1))
    value3 <- eitherT(dummyDao(value2))
  } yield (value2)}.run.map {
    res => res
  }
}

// println(s"Simple success example ${res}")
// Await.result(ex2.Ex1.f, Duration.Inf)
