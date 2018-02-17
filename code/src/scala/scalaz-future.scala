import scala.concurrent._
import scala.util.{Success, Failure}
import scala.concurrent._
import ExecutionContext.Implicits.global

// future option cutting
object FutureOptionCutting {

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
  def foCutting5: Unit = {
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
