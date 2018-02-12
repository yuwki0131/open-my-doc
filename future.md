```scala
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{ Success, Failure }
```
https://stackoverflow.com/questions/34572730/how-to-best-handle-future-filter-predicate-is-not-satisfied-type-errors

https://qiita.com/mtoyoshi/items/f68beb17710c3819697f

https://docs.scala-lang.org/ja/overviews/core/futures.html

```scala
Await.result(for { a <- Future { 1 }; if a == 1 } yield { a }, Duration("1 seconds"))
(for { a <- Future { 1 } } yield a).onComplete { a => println(a) }
(for { a <- Future { 1 }; if a == 2} yield a).onComplete { a => println(a) }
(for { a <- Future { 1 }; if a == 2} yield a).onComplete { case Success(a) => println(a); case Failure(e) => println(e); }
(for { a <- Future { 1 }; _ <- if (a == 2) Future.successful(0) else Future.failed(new RuntimeException("fail"))} yield a).onComplete { case Success(a) => println(a); case Failure(e) => println(e); }
```

```scala
scala> :t Future.successful _
Nothing => scala.concurrent.Future[Nothing]
scala> :t Future.failed _
Throwable => scala.concurrent.Future[Nothing]
```
