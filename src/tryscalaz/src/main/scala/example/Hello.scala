// package example

import scalaz._
import Scalaz._
import scala.concurrent._
import scala.util.{ Success, Failure }
import scala.concurrent._
import ExecutionContext.Implicits.global

// println(classOf[String].getMethods.toSeq.map(_.getName).mkString("\n"))



// future option cutting
object FutureOptionCutting {

  def methodA(): EitherT[Future, Int, Int] = {
    EitherT.right( Future { 2 } )
  }

  def methodB(): EitherT[Future, Int, Int] = {
    EitherT.right( Future { 3 } )
  }

  def methodC(): EitherT[Future, Int, Int] = {
    EitherT.right( Future { 3 } )
  }

  def foCuttingsz = {
    val result = for {
      alpha <- methodA
      beta <- methodB
//      gamma <- EitherT.left( Future { -1 } )
    } yield alpha
    result
  }

// (Future { 2 }).some.sequence
// http://www.smartjava.org/content/scalaz-features-everyday-usage-part-2-monad-transformers-and-reader-monad

  // Await.result(FutureOptionCutting.railsways, duration.Duration.Inf)
  def railsways = {
    val result = for {
      alpha <- Future { Some(20) }
      x = alpha.getOrElse(throw new RuntimeException("here"))
    } yield x
    result
  }

  // optionでよくやるやつ
  def optiononly: Unit = {
    for {
      alpha <- None
      beta <- Some(20)
    } yield { println((alpha, beta)) }
    ()
  }

  // Future Optionで上手くできないやつ
  def futureoption: Unit = {
    for {
      alpha <- Future { None }
      beta <- Future { Some(20) }
    } yield { println((alpha, beta)) }
    ()
  }

  // Future Optionでなんとかやるやつ
  def foCutting: Unit = {
    for {
      alpha <- Future { None }
      if alpha != None
      beta <- Future { Some(20) }
    } yield { println((alpha, beta)) }
    ()
  }
/*
  // Future Optionで分岐させたい時
  def foCutting2: Future[(Option[Int], Option[Int], Option[Int])] = {
    val result = for {
      alpha <- Future { None }
      //if alpha != None
      beta <- Future { Some(20) }
      gamma <- Future { Some(30) }
    } yield (alpha, beta, gamma)
    result match {
      case Success(value) => value
      case Failture()
    }
  } */

  // Future Optionで分岐させたい時
  def foCutting2: Unit = {
    val result = for {
      alpha <- Future { None }
      if alpha != None
      beta <- Future { Some(20) }
      gamma <- Future { Some(30) }
    } yield (alpha, beta, gamma)
    result onComplete {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }
    ()
  }

  def predicate(condition: Boolean)(fail: Exception): Future[Unit] = {
    if (condition) Future( () ) else Future.failed(fail)
  }

  // Future Optionで分岐させたい時(解決案0)
  // https://stackoverflow.com/questions/17869624/scala-future-with-filter-in-for-comprehension
  def foCutting3: Unit = {
    val result = for {
      alpha <- (Future { None })
      _ <- predicate(alpha != None)(new RuntimeException("erra"))
      beta <- Future { Some(20) }
      gamma <- Future { Some(30) }
    } yield (alpha, beta, gamma)
    result onComplete {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }
    ()
  }

  // Future Optionで分岐させたい時 (解決案1)
  def foCutting4: Unit = {
    val result = for {
      alpha <- Future { None }.map( x => if (x.isEmpty) { throw new RuntimeException("erra") })
      beta <- Future { Some(20) }
      gamma <- Future { Some(30) }
    } yield (alpha, beta, gamma)
    result onComplete {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }
    ()
  }

  // Future Optionで分岐させたい時 (解決案1)
  def foCutting5: Unit = {
    val result = for {
      alpha <- Future { None }
      _ <- if (! alpha.isEmpty) Future {()} else { throw new RuntimeException("era") }
      beta <- Future { Some(20) }
      gamma <- Future { Some(30) }
    } yield (alpha, beta, gamma)
    result onComplete {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }
    ()
  }

  // Twitter用
  def foCutting5twitter: Unit = {
    (for {
      alpha <- Future { None }
      _ <- if (! alpha.isEmpty) Future {()} else { throw new RuntimeException("era") }
      beta <- Future { Some(20) }
    } yield (alpha, beta)) onComplete {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }
    ()
  }

}

// object Hello extends Greeting with App {
//   println(greeting)
// }

// trait Greeting {
//   lazy val greeting: String = "hello"
// }
