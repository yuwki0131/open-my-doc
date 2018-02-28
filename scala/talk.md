## このドキュメントについて
* Javaエンジニア用Scalaユーザ向け。
* Scala 2.13.x系を基準。
* 同じ関数型言語でも結構言語によって慣習が違うため、関数型プログラミング一般の話をするのは難しい。
  * (同じオブジェクト指向でもJavaとObjective-C、Smalltalk、Ruby、JavaScriptではかなり雰囲気が違うのと同じ)
  * [Twitterより](https://twitter.com/esumii/status/638588331459153920)
  * Scala以外の関数型言語。
    * Haskell, OCaml, StandardML, F#, Clean(ConcurrentClean), Erlang, Elixir, Scheme, Clojure他多数。
  * フロントエンド専用だとElm, PureScriptなども。
  * というわけで**関数型プログラミングっぽい**話をします。
* 以下に書いてある事に関しては基本的に話しません。内容が重複するため。
  * [Scala研修テキスト - dwango on GitHub](https://dwango.github.io/scala_text/)

## なぜ、関数型プログラミングをするのか?
(※個人の主観です。)
* 抽象化が容易になる(デザインパターンを使わない(意識しない)プログラミング)
* 副作用の分離と排除

### デザインパターン
の多くが不要になる。(語弊のある言い方をすれば)

#### Gofのデザインパターン
* Strategy
  * アプリケーションで使用するアルゴリズムやコードを動的に切り替える。
  * [Strategy パターン - Wikipedia](https://ja.wikipedia.org/wiki/Strategy_%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3)
  * ScalaだとStrategyを切り替える為のインターフェースは不要。単に関数を受け渡すだけになる。
  * 型クラス(後述)を定義するなど、このパターンを実装するための方法がScalaには何通りもある。
* Template Method
  * 上に同じ
* Factory Method
  * 部分適用(Partial Application)が使える。
  * https://www.ibm.com/developerworks/jp/java/library/j-ft10/
* Visitor
  * 代数的データ型 + パターンマッチ(+ 再帰)を使う。
* Composite
  * 上に同じ
* Interpreter
  * 上に同じ
* Iterator
  * Scalaなら、Travasable
* State
  * 関数型プログラミングに反する。
* Decorator
  * map
* Singleton
  * オブジェクトは大体シングルトン

#### Gofのデザインパターン以外
[デザインパターン紹介(Gof以外のデザインパターン)](http://www.hyuki.com/dp/dpinfo.html#Balking)

* Null Object
  * オブジェクトはOption型で定義して、部分関数(Partial Function)を使う。
* Balking
  * ガード節とも。これのScala版のいい方法がわからない。あえて言うなら、for-yield式がそれに相当。
* Immutable object
  * 積極的にmutableにしなければ、オブジェクトは全てimmutable
* Future
  * ScalaのFuture

### immutable
* オブジェクトの「状態」が変わらない。
  * インスタンスが一度生成されたらそれ以降は不変。
  * オブジェクトが破壊されない(不変である)ことが保証される。
    * オブジェクトの書き換えに由来するバグを回避できる。
    * 書き換えられるタイミングの調査コスト、思考のコストが減る。
    * 難しいことを考えなくて済む。
* 関数は、入力と出力(引数と戻り値)のみを注目すればいい。
  * **参照透過性**: 引数に同じ値を与えれば同じ戻り値を得ることができる関数を参照透過な関数という。
  * 入力に対して、想定した出力が得られれば、正しい関数であると言える。
* (注)関数型言語のみがimmutableなコードを書ける訳ではない。他の言語でもある程度同じことが可能。
  * 但し、Javaは破壊的な変更をしていないことの保証のサポートが手薄。(finalを使えばその限りではないものの)
  * Scalaではimmutableなデータ型(Tuple、case classなど)を使う限りでは不変であることが保証される。
   * valによる(Javaで言うところのfinalを付ける)変数。
   * immutableな標準型、immutableなHashmap、List、etc...
   * 最後の一つはTupleによるプログラミング、case classによるデータ定義。

### 副作用が他の関数型言語以外の言語よりも少ない傾向にある
* 副作用とは、数学には存在し得ない計算機のみがもつ特有な作用のこと。。。らしいです。
  * 出典: [Notions of computation and monads](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.79.733&rep=rep1&type=pdf)
* 副作用が少なくなるように意識した言語設計
  * 変数の破壊的代入を前提としないような書き方を可能にする文法、言語機能。

## 関数型言語のプログラム
* プログラム全体が巨大な
* 関数定義は巨大な関数適用
*

## これから説明する内容
プログラム全体の設計の話というよりは細かいテクニカルな話が中心になります。

仕事で頻出するテクニックというよりは、他の(社内外問わず)プログラマが書いたScala(他)のソースコードを読む時の手がかりや
調べる為(ググるため)の用語、関数型プログラミングにおける基礎的な概念について紹介します。
または、関数型プログラミングや言語に由来する変なバグを仕込まない為の知識の共有も目的としています。

* 関数型プログラミングなので、当然ながら関数の使い方(概念、用語、テクニック)をメインに説明します。
  * 再帰、無名関数、静的スコープ、クロージャ、高階関数、合成関数
* 関数型プログラミングで頻繁に用いられるデータ型である代数的データ型の概念と使い方。
  * 代数的データ型、パターンマッチ
👇多すぎるので、以下はスキップ。
* 業務でよく使われるOption/Either/Exception、及びfor式について。
  * 例外の使い方、for式
* Scalaは静的型付言語なので、型の話をします。
  * Scalaの3つのimplicit、Scalaの型、型クラス
* Scalaについて調べていたら発見した余談。末尾にもリンクをいくつかつけています。
* 一番最後に、今回は詳細は話しませんが、関数型プログラミングまわりの技術について紹介します。

## (まずはじめに)replによるローカル実行テスト
* repl: Read Eval Print Loop
* ミニマムなコードで、シンタックスの動作確認や型の確認ができる。
* Scalaで小さい単位で文法を学ぶのにはうってつけ。
* Eclipseがフリーズした時など。

(ご存知だとは思いますが)以下のコマンドで実行出来る。
```
sbt console
```
後は以下のようにインタラクティブにScalaのコードを打ち込んで行けば即座に実行結果が得られる。
(電卓にも使える)
```
scala> 1 + 1
res0: Int = 2
```
型を調べたい時は、`:t`のコマンドを使う。
```
scala> :t 1.0
Double
```
メソッドを調べる。`クラス名.getClass.getMethods.map(_.getName)`
```
scala> Ordering.getClass.getMethods.map(_.getName)
res22: Array[String] = Array(Tuple3, Tuple2, Tuple4, Tuple5, comparatorToOrdering, ordered, Tuple9, Tuple8, Tuple7, Tuple6, Iterable, Option, by, fromLessThan, apply, wait, wait, wait, equals, toString, hashCode, getClass, notify, notifyAll)
```

## ここでクイズ！

```scala
def func1(a: (String, String)): Int = {
〜
}
```

```scala
def func1(a: Seq[Int]): Int = {
〜
}
```
この２つの関数を統合したい。。func1が呼ばれる時は常にSeq[Int]または(String, String)が与えられる。
〜以降の処理は一緒。aの型に応じて処理を切り替える処理は用意されている。match/caseで引数aのパターンごとに処理するため。
* 代数的データ型を使うのはなし
* リフレクション、関数のオーバーロードもNG、Scalaの基本的な機能のみを使って処理を切り替える。
* 型パラメータ(`def func1[A](a: A)`)みたいな書き方もなしで。Genericな処理は眼中にないので。
* もちろんAnyもダメです。。。
* ちなみにPythonやRuby(JavaScript他)だと、何も考えずに以下のように書いたら終わり。
```scala
def func1(a): Int = {
〜
}
```

## Answer
```scala
def func1(a: Option[(String, String)], b: Option[Seq[Int]]): Int = {
〜
}
```
だと思った? 残念さやかちゃんでした！
* 上記の書き方の問題点として後続の切り替えするmatch-caseが上手く書けない。
```scala
(a, b) match {
case (Some(a), _) => 〜
case (_, Some(b)) => 〜
case _ => 〜
}
```
* パターンにマッチしない場合(`case _ => `)に、例外を投げるというのも考えられるが、その例外送出は本来必要ないはず。
* もっと自然な書き方があるはず！
* (String, String)型かSeq[Int]型か、どちらかの値のみを引数にとることが、型レベル(引数の型)で十分に表現されていない。
  (関数のコードを読むまでわからない)

## Answer
```scala
def func1(a: Eihter[(String, String), Seq[Int]]): Int = {
〜
}
```
Scala標準のEitherはRightとLeftに対して平等。
ちなみに、Eitherは次のように書ける。
```scala
def func1(a: (String, String) Eihter Seq[Int]): Int = {
〜
}
```
* 3つ来たらどうなるんだという話はあるが、その時はEitherのネスト or 代数的データ型。
* コンセプトとしてはScalaの型定義の柔軟性を意識してほしい。
* Option以外の型もあるのでその辺りも考慮してほしい。
  * Seq/Eihter/Success-Failure
  * 代数的データ型
* 基本的に動的型付き言語が手に入れている柔軟性(に近いこと)を静的型付き言語でも同様に実現できる機能が用意されている。
