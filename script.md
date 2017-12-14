この記事は [ウェブクルー Advent Calendar 2017](https://qiita.com/advent-calendar/2017/webcrew) の15日目の記事です。
昨日は@wc-kobayashiTさんの「(AMP（Accelerated Mobile Pages）について)(https://qiita.com/wc-kobayashiT/items/8c63f782dcfc30636c31) 」でした。

Scalazの様々な業務で使えそうな機能について集めてみました。Scalazは、Scalaで関数型プログラミングをするためのライブラリです。Scalaでよりスムーズにプログラミングするためは、標準の機能のみでは厳しい部分もあるそうです。そのような部分を補うための、関数型プログラミングを行うためのライブラリです。Scalazが提供する多彩な型クラスや関数のうち、弊社の業務でも明日から使用できそうな、実用的な側面についてまとめて紹介します。

[scalaz/scalaz - Github](https://github.com/scalaz/scalaz)

## 環境
* Scala: `scalaVersion := "2.12.4"`
* Scalaz: `libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.17"`

### import

```
import scalaz._
import scalaz.Scalaz._
```

## 今回紹介するライブラリの型と関数

* 便利な関数
* Validation
* EitherT(FutureとEitherの混合)
* その他

## 便利な関数

Scala標準の型であってもImplicit Conversion(暗黙の型変換)によりScalazの恩恵を受けられます。Scalazをimportするだけです。

### Boolean to Option

```
(true).option("hogehoge")  // => Some("hogehoge")
(false).option("hogehoge") // => None
```

BooleanからOptionへの関数。Booleanから条件分岐によってOptionの値に変換する必要がなくなります。Validationなどで、Booleanを返してくるメソッドと、Optionを返してくるメソッドが混合している時に、for式でまとめて、Validationのメソッド呼び出しを列挙したい時に使えそうです。

### intersperse

```
Seq("1", "2", "3").intersperse(",") // => Seq("1", ",", "2", ",", "3")
```

HaskellやClojureには、リストの間に要素を挿入するintersperseやinterposeがありますが、標準のScalaのライブラリにはありません。類似のメソッドにmkStringがありますが、これは返す結果がStringのみになってしまいます。Scalazのintersperseなら、リストのままリストの内部の要素に要素を詰め込めます。

### tailOption

```
List().tailOption // => None
List(1).tailOption // => Some(List())
List(1, 2).tailOption // => Some(List(2))
```

headOptionのtail版です。tail部に要素がない場合、Noneそれ以外の場合は、tail要素が詰め込まれたSomeを返します。前述のようにfor式で統一的に実行結果を扱いたい時に、使用できそうです。

### scalaz.Validation.parseInt

```
"123".parseInt.toOption // => Some(123)
"ab3".parseInt.toOption // => None
```

デフォルトのString.toIntやparseIntでは、parse対象が数値を表す文字列でない場合に、例外`java.lang.NumberFormatException`を吐きます。その為、toIntをする度に、例外をcatchするか、文字列をチェックして数値を表さない場合には、toIntを行わないように気をつける必要があります。parseIntは、NumberFormatExceptionが発生した場合、Failureとして扱います。toOptionでTry型をOption型にしてしまうことで、parse処理を簡潔に記述します。ちなみに、parseInt以外に、parseBooleanもあります。

## Validation

WebアプリケーションなどでValidationを行う際、一度のバリデーションで複数のエラーを出してほしい場合が多いと思います。例えば、データ登録の画面において、名前、住所、電話番号、メールアドレスなどのデータを一括して受け取り、各項目ごとにバリデーションを行い、不適切な項目のエラーメッセージを返すようなシチュエーションがあります。この場合、複数のバリデーション中のエラーでは、リストにエラーの項目を入れていくような処理を書く必要がでてきます。例えば、以下のような。failsはSeq型で、何かしらErrorを表すオブジェクトのリストとなります。

```
val failMessages = verifyName(name) ++ verifyAddress(address) ++ verifyTel(tel) ++ verifyMail(mail)
```

Scalazが提供するValidationクラスは、自然な形でバリデーションを行う複数の関数をつなぎ合わせて、バリデーションの処理を表現できます。この時、バリデーションの処理を行う関数どうしは、独立した成功/失敗(エラーメッセージ)を返す関数として記述できます。Validationは、ScalaやScalazのOptionやEither型と違い、Validationの処理をすべて実行し、その結果を返します。

まずはじめに、バリデーションを行う単体の関数を書きます。NELは、Not Empty Listの略です。successNelで、Validationの成功の値、failureNelでValidation失敗時の値を返します。下記のコードではthen節で成功時の値、else節でFailureの場合の値を返します。

```
object V {

  def validateName(name: String) = {
    if (name.length < 84) {
      name.successNel[String]
    } else {
      "ピカソよりも名前が長い".failureNel[String]
    }
  }

  def validateTel(tel: String) = {
    if (tel != "110") {
      tel.successNel[String]
    } else {
      "110番を登録しないで下さい".failureNel[String]
    }
  }

  def validateAge(age: String) = {
    if (age.toInt < 117) {
      age.successNel[String]
    } else {
      "存命中の世界の長寿者十傑を超える".failureNel[String]
    }
  }

```

次に、バリデーションを行う単体の関数をつなげていきます。成功時のValidation結果は、case classにマッピングできます。

```
  case class Member(name: String, tel: String, age: String)

  def validate(re: Member) = {
    val result = (
      validateName(re.name) |@|
        validateTel(re.tel) |@|
        validateAge(re.age))(Member)
    result
  }
}
```

そして、以下のように実行し、値が返ってきます。

```
scala> V.validate(V.Member("パブロ・ピカソ", "119", "10"))
res1: scalaz.Validation[scalaz.NonEmptyList[String],V.Member] = Success(Member(パブロ・ピカソ,119,10))

scala> V.validate(V.Member("パブロ・ピカソ", "110", "1000"))
res2: scalaz.Validation[scalaz.NonEmptyList[String],V.Member] = Failure(NonEmpty[110番を登録しないで下さい,存命中の世界の長寿者十傑を超える])
```

以上のように記述することで、Validation処理をValidation関数の合成として記述することができるようになります。単にリストにエラー内容をまとめしまう場合と比較して、Validationで表されること扱う処理の内容が明確になり、SuccessとFailureでバリデーションの結果であることが明確に表現できます。

## EitherT[Future, A, B] (Eitherのモナドトランスフォーマー)

PlayframeworkとSlickを使いノンブロッキングな処理を記述する時、様々な処理がFutureに包まれることになると思います。特にController～データアクセス間では、様々な処理結果をFutureでつなぎ合わせて記述することになります。それと同時に、処理が進むに連れて、成功/失敗を表現する必要性が出てくると思います。つまり、Future[Either[A, B]]型をコンテキストに持つようなfor式です。例えば、以下のような。

```
val result = for {
    userId <- dataAccess.findByValue(value1) // Future[Either[String, String]]型の値を返す
    userRow <- dataAccessA.findById(userId) // Future[Either[String, RowA]]型の値を返す
    otherRow <- dataAccessB.findBy(userRow.BId) // Future[Either[String, RowB]]型の値を返す
} yield (userRow, otherRow)
```

しかし、このようなfor式は十分に機能してくれません。まず、for式自体はFuture型の為のFor式となるため、Eitherの為のfor式を別途用意する必要があります。外側の型がFutureであるためfor式自体は、EitherのためのLeft/Rightによる分岐の機能を持ちません。さらに悪い事に、上記のうち変数userIdに代入されるのは、EitherのRight/Leftの中身であるStringではなく、EitherのRight/Leftによってラップされた値がuserIdに入ります。

また、一番目や二番目の関数呼び出しで失敗した場合でも処理が中断されることがありません。例えば、`dataAccess.findByValue(value1)`でLeftが帰ってきた場合であっても、それ以降の`dataAccessA.findById(userId)`が実行されてしまいます。しかし、二番目の関数呼び出しが一番目の関数呼び出しを前提としている以上、for式中の二番目以降の処理は中断されるべきでしょう。つまり、別途、Eitherの値の結果に応じた各パターンの処理を記述する必要があります。

この問題を克服するための方法として、ScalazのFutureとEitherの合成、EitherT[Future, A, B]が考えられます。EitherTはモナドトランスフォーマーと呼ばれ、２つのモナドを合成します。今回は、EitherとFutureを合成します。Scalazにおけるモナドについては[ここ](https://tech.recruit-mp.co.jp/server-side/post-2540/) などを参照して下さい。


まず、Either[Future, A, B]を使う前に、Futureをモナド化しておく必要があります。定義は、[ここ](http://koff.io/posts/290071-make-async-with-scalaz-either-and-futures/) のものを引用しています。

```
implicit val FutureMonad = new Monad[Future] {
    def point[A](a: => A): Future[A] = Future { a }
    def bind[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f
}
```

モナドトランスフォーマーは、モナド同士の合成であるため、Scala標準のモナドでないFutureは、そのまま合成できません。Either[Future, A, B]を使用するスコープ内で暗黙の型変換が可能なように上記のコードが参照できるようにしておく必要があります。

次に、`Future[\/[A, B]]`となる関数を用意します。`\/`はScalazにおけるEitherです。Scalazでは、Eitherは`\/`で表します。RightとLeftもそれぞれ、`\/-`と`-\/`といった書き方です。Scala標準のEitherはモナド則を満たさない為に、ScalazではEitherが再実装されています。ScalazのEitherをそのまま使うことで、FutureとEither、両方モナドとなり、モナドトランスフォーマーによる合成が可能になります。

```
def f(n: Int)(implicit ec : ExecutionContext) : Future[\/[A, B]]
```

このメソッドは適当なものを用意すれば問題ありません。例えば、SlickでDBアクセスし結果を取得した時に得られるFuture[Seq[A]]型をEither型に変換した結果を返すような関数とか。データ取得結果に応じて、`\/-("data1")`や`-\/("Not Found")`などEitherの値を返す値です。同じfor式内でまとめたい場合には、必ず、`Future[Either[A\/B]]`の型は一致させておく必要があります。そして、`Future[\/[A, B]]`から`EitherT[Future, A, B]`への変換します。その後、変換後のモナドトランスフォーマーの結果から値を取得し、再び型コンテキストをFutureに戻します。これらは以下のように記述することができます。

```
import scalaz.EitherT.eitherT

def f = { for {
    result <- eitherT(f(a))
    ...
    } yield result }.run
```

上記の記述をまとめると、`Future[\/[A, B]]`型を返す関数f, g, hの処理を続けて記述する時、以下のように書けます。

```
{ for {
    r1 <- eitherT(f(a))
    r2 <- eitherT(g(r1))
    r3 <- eitherT(h(r2))
  } yield r3 }.run
```

このfor式は、FutureとEitherの機能、両方を併せ持ちます。この結果、上記のfor式は、以下の重要な特徴を持ちます。

* Futureとして、非同期の処理が記述できる。(for式により、flatMapの複雑なネストに陥っていない)
* Futureなので、例外が投げられた場合は、FutureのFailureの値が返される。
* Eitherとしてのfor式がFutureを使いながら書ける。
* Eitherとして、処理の失敗時に中断ができ、なおかつ、Leftによって、失敗時のパラメータを持てる。
* for式の前後は、普通のFutureとして扱える。(Futureの実行結果を変換するだけで、上記のfor式中で使用することができる/上記の式の処理結果は、単なるFutureとして他のライブラリ(Playframeworkなど)側に渡すことができる)

すなわち、EitherT[Future, A, B]型とfor式の組み合わせにより、見た目のシンプルさを維持しながら、複雑な多数の分岐やネストをコントロールすることが可能になります。

EitherT[Future, A, B]は、例えば以下のように使えます。

```
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
```

dummyDaoは、引数の値によってFailure(Exception)、SuccessのRight(`\/-`)、SuccessのLeft(`-\/`)の値が返されます。

上記を実行すると以下のような結果を得られます。

```
scala> Await.result(FE.f(3), Duration.Inf)
res35: String \/ Int = \/-(1)

scala> Await.result(FE.f(2), Duration.Inf)
res36: String \/ Int = -\/(n is zero)

scala> Await.result(FE.f(1), Duration.Inf)
res37: String \/ Int = -\/(n is zero)

scala> Await.result(FE.f(-1), Duration.Inf)
java.lang.ArithmeticException: n must not be negative
  ...
```

EitherのRightとLeft、Futureが失敗した場合のException(Failure)が出力されます。それぞれ`dummyDao`の結果に応じて、for式の制御が切り替わっています。

## その他

### Resource/withResource

Scalazのloan patternの実装です。Scalaには標準で、Pythonのwith構文や、C#にあるようなusing構文がついてきません。
例えば、closableなりソースをopenした場合にこのような構文が必要になってきますが、Scalaではloan patternで
リソースのcloseを保証するようですが、loan patternは(難しくはないものの)自分で実装する必要があります。
これを代替する機能としてScalazにはAutoClosableというメソッドが用意されています。

http://takezoe.hatenablog.com/entry/20110930/p13

### getOrElseの演算子

```
Some(1) | 2 // => 1
None | 2 // => 2
```

名前は分かりませんでしたが、Scalazでの定義を見ると、getOrElseそのものでした。getOrElse同様、成功時には中の値を取り出し、失敗時には、Elseの時の値(この演算子の右側の値)を返します。また、バリエーションとして、`|||`(こちらはorElseの演算子)というのもあります。

### C言語などの三項演算子風の条件分岐

```
false ? "a" | "b"
```

標準のif-else説よりもタイプ数が減ります。 if () elseが8文字なので、6文字分コードが短くなります。基本的に、if-elseを使えばいいので、あまり、ありがたみはないですが、横に長くなりがちなScalaのコードを短くします。。。`|`が入っているので、getOrElseの演算子と似てはいますが、ここでの`|`はBooleanOpsクラスで定義されている別のものです。

## おわりに

Scalazのライブラリ内には、(コモナドトランスフォーマなど)使うタイミングが不明な型クラスもありますが、日常的に書くコードの痒い所に手が届く様々な関数や型クラスも多数用意されています。特に、[ここ](http://blog.share-wis.com/scala-play-libraries) で言及されているように、Futureを使った処理を使う場合には、EitherTやOptionTなどがあると便利なのではないでしょうか。多少やり過ぎなきらいはありますが、業務で使用するScalaのコードも、Scalazを使用することで、より完結で分かりやすいコードが書ける場合もあるのかもしれません。

## 参考文献
* [Noel Markham - ScALAZ](http://noelmarkham.github.io/scalaz-intro/#/2)
  Scalazの包括的な紹介。便利な関数など

* [intersperse scalaz/scalaz - Github](https://github.com/scalaz/scalaz/blob/v7.0.0-M9/core/src/main/scala/scalaz/std/List.scala#L99)
  上記で紹介したintersperseの実装

* [BooleanOps scalaz/scalaz - Github](https://github.com/scalaz/scalaz/blob/431f4bafee4d734c89b7f70bca9f88e92ecd4ac6/core/src/main/scala/scalaz/syntax/std/BooleanOps.scala)
  上記で紹介したBoolean系の関数の実装

* [scalazのValidationの使い方　値の検証・エラー処理 - つかびーの技術日記](http://tech-blog.tsukaby.com/archives/729)
  ScalazのValidationについて

* [EitherとValidation - pab_tech - 株式会社ドワンゴ Scalaz勉強会](http://slides.pab-tech.net/either-and-validation)
  ScalazのEitherとValidationの違いについてなど

* [Practical Scalaz: Make async operations with scalaz.Either and Futures - KOFF.io](http://koff.io/posts/290071-make-async-with-scalaz-either-and-futures/)
  上記のFutureMonadの定義。モナドトランスフォーマーによるFutureとEitherの合成

* [Future[Either] and monad transformers - Functional Justin](http://justinhj.github.io/2017/06/02/future-either-and-monad-transformers.html)
  上記と同様に、FutureMonadによるモナドトランスフォーマーを使ったFutureとEitherの合成

* [NonEmptyList, Validation - ひなたねこ](http://basking-cat.blogspot.jp/2011/12/nonemptylist.html)
  Validationや上記のgetOrElseの演算子など

* [Salazを使おう #1 - PSYENCE:MEDIA](https://tech.recruit-mp.co.jp/server-side/post-2540/)
  Scalazにおけるモナドの説明

---

ウェブクルーでは一緒にScalaのコードを書いていただけるメンバーを募集しています！
[開発エンジニアの募集](https://hrmos.co/pages/1004681658307198976/jobs/0000005)
ご興味のある方はぜひ！

ここの参考文献も整理すること(TODO)

* type classの
http://noelmarkham.github.io/scalaz-intro/#/12

* useful resources
http://noelmarkham.github.io/scalaz-intro/#/25

* Scalazを使おう
https://tech.recruit-mp.co.jp/server-side/post-2540/

* ドワンゴのScalaz
http://slides.pab-tech.net/either-and-validation/#14
(EitherのValidationの違いなど)

* EitherT
http://xuwei-k.hatenablog.com/entry/20140919/1411136788

* 使えるか? 継続モナドとFutureをPlayframeworkで
https://qiita.com/pab_tech/items/fc3d160a96cecdead622
(Futureと継続モナドを使ってPlayのControllerを組み替える話)
