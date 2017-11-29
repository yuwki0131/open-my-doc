// See http://justinhj.github.io/2017/06/02/future-either-and-monad-transformers.html

package ex2

// import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, ExecutionContext, Future}
import scalaz.{-\/, \/, \/-, Monad}
import scalaz.EitherT.eitherT
import scala.concurrent.ExecutionContext.Implicits.global

// Futureをモナドにします。
// http://koff.io/posts/290071-make-async-with-scalaz-either-and-futures/
// implicit val FutureMonad = new Monad[Future] {
//     def point[A](a: => A): Future[A] = Future { a }
//     def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f
// }

object Ex1{
  implicit val FutureMonad = new Monad[Future] {
    def point[A](a: => A): Future[A] = Future { a }
    def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f
  }

  def dummyFunction1(n: Int)(implicit ec : ExecutionContext) : Future[\/[String, Int]] = {
    n match {
      case 0 => Future.failed(new ArithmeticException("n must not be zero"))
      case _ => {
        Future.successful(
          if(n % 2 == 0)
            \/-(n / 2)
          else
            -\/("An odd number")
        )
      }
    }
  }

  def f = {for {
    rb1 <- eitherT(dummyFunction1(8))
    rb2 <- eitherT(dummyFunction1(rb1))
    rb3 <- eitherT(dummyFunction1(4))
    rb4 <- eitherT(dummyFunction1(0))
  } yield (rb2)}.run.map {
    res => res // println(s"Simple success example ${res}")
  }
  // Await.result(ex2.Ex1.f, Duration.Inf)
}
// Await.result(ex2.Ex1.f, Duration.Inf)

/*
object Ex1 {

  // To use eitherT we need a Monad for the Future type
  implicit def MWEC(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future]{
    def point[A](a: => A): Future[A] = Future(a)
    def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f
  }
}
// Couple of example functions we can use with EitherT

// halves the input if it is even else fails
// to investigate exception handling we will throw an ArithmeticException if n is zero
def dummyFunction1(n: Int)(implicit ec : ExecutionContext) : Future[\/[String, Int]] = {

  if(n == 0) {
    Future.failed(new ArithmeticException("n must not be zero"))
  }
  else {
    Future.successful(
      if(n % 2 == 0)
        \/-(n / 2)
      else
        -\/("An odd number")
    )
  }
}

// appends a suffix to the input after converting to a string
// it doesn't like numbers divisible by 3 and 7 though
def dummyFunction2(n: Int)(implicit ec : ExecutionContext) : Future[\/[String, String]] = {
  Future.successful(
    if(n % 3 != 0 && n % 7 != 0)
      \/-(n.toString + " horay!")
    else
      -\/(s"I don't like the number $n")
  )
}

// Example usages

{for (
        rb1 <- eitherT(dummyFunction1(8));
        rb2 <- eitherT(dummyFunction1(12))

      ) yield (rb1 + rb2)}.run.foreach {
        res =>
          println(s"Simple success example ${res}")
      }

{for (
      rb1 <- eitherT(dummyFunction1(14));
      rb2 <- eitherT(dummyFunction1(12));
      rb3 <- eitherT(dummyFunction2(rb2 + rb1))

    ) yield rb3}.run.foreach {
    res =>
          println(s"Show extracting the values and passing to other function ${res}")
      }

{for (
     rb1 <- eitherT(dummyFunction1(14));
     rb2 <- eitherT(dummyFunction1(14));
     rb3 <- eitherT(dummyFunction2(rb1 + rb2))
    ) yield rb3}.run.foreach {
    res =>
          println(s"Show error response ${res}")
      }

// Throws exception

val f = {for (
  rb1 <- eitherT(dummyFunction1(0))) yield rb1}.run

f onFailure {

  case e: Exception =>
    println(s"Show exception: ${e.getMessage}")

}

// Without the transformer

dummyFunction1(14).flatMap{
  case \/-(rb1) =>
    dummyFunction1(12).flatMap {
      case \/-(rb2) =>
        dummyFunction2(rb2 + rb1).map {
          case \/-(rb3) => println(s"Show flatmap result $rb3")
          case -\/(oops) => println(s"oops $oops")
        }
    }
}

}
 */
