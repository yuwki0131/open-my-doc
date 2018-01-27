# Scalaと関数型プログラミングとは まとめ

**編集途中のドキュメント**

2018/01/27版

* Javaエンジニア用Scalaユーザ向け
* そういう傾向があるという話。
* 同じ関数型言語でも結構言語によって慣習が違う。
  * (例えば、同じオブジェクト指向でもJavaとObjective-CとSmalltalk、Python、JavaScriptではかなり雰囲気が違うのと同じ)
* 関数型プログラミング一般の話をするのは難しい。
  * [Twitterより](https://twitter.com/esumii/status/638588331459153920)
* Scala以外だと、Haskell, OCaml, StandardML, F#, Clean(ConcurrentClean), Erlang, Elixir, Scheme, Clojure他多数。
* フロントエンド専用だとElm, PureScriptなどもある。
* というわけで関数型プログラミングっぽい話をします。
* 以下に書いてある事に関しても基本的に話しません。内容が重複するため。
  * [Scala研修テキスト - dwango on GitHub](https://dwango.github.io/scala_text/)

## なぜ、関数型プログラミングをするのか?

### デザインパターン
の多くが不要になる。(語弊のある言い方をすれば)

#### Gofのデザインパターン

* Strategy
  * アプリケーションで使用するアルゴリズムやコードを動的に切り替える。
  * [Strategy パターン - Wikipedia](https://ja.wikipedia.org/wiki/Strategy_%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3)
  * => Scalaだと切り替える為のインターフェースなどは不要、単に関数を受け渡すだけになる。
  * または型クラスを定義する。
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

#### Gofのデザインパターン以外
  [デザインパターン紹介(Gof以外のデザインパターン)](http://www.hyuki.com/dp/dpinfo.html#Balking)

* Null Object
  * オブジェクトはOption型で定義して、部分関数(Partial Function)を使う。(参照)
* Balking
  * ガード節とも。これのScala版のいい方法がわからない。あえて言うなら、for-yield式がそれに相当。
* Immutable object
  * 積極的にmutableにしなければ、オブジェクトは全てimmutable
* Future
  * ScalaのFuture

### immutable
* 「状態」が変わらない
* 再代入を行わない
* データ型では代入は、最初の1回のみ。
* データ構造を破壊しないことで、思考コスト、オブジェクトがどこで書き換えられるかの調査コストが減る。
  * どこでデータが書き換えられるかを考える必要が無くなる。
  * 関数の入力と出力のみに注目すればいい。
    * (参照透過性: 引数に同じ値を与えれば同じ戻り値を得ることができる関数を参照透過な関数という)
  * 入力に対して、想定した出力が得られれば、正しい関数であると言える。(プロパティベースのテスト(関数に対するユニットテスト))
  * (注)別に関数型プログラミングだからというわけではない。Javaなど他の言語でもある程度同じことが可能。
    * 但し、Javaは破壊的な変更をしていないことの保証のサポートが弱い(finalを使えばその限りではない)
    * Scalaではimmutableなデータ型やTuple、case classなどを使っている限りでは変更されていないことを保証する仕組みが多数
* オブジェクトが破壊されないことの保証
  * 一つはval(Javaで言うところのfinalを付ける)によるデータ型
  * またははimmutableな標準型、immutableなhashmap、list、etc...
  * 最後の一つはTupleによるプログラミング、否、case classによるデータ定義

### 副作用が他の関数型言語以外の言語よりも少ない傾向にある
* 割と少ないか少なくなるように意識した言語設計(変数の破壊的代入を前提としないような書き方)

## これから説明する内容
プログラム全体の設計の話というよりは細かいテクニカルな話が中心になります。

仕事で使いまくるテクニックというよりは、ネット上のソースコードを読む時の手がかりや参考になる内容、
調べる為(ググるため)の用語、関数型プログラミングにおける基礎的な用語、
または、言語に由来する変なバグを仕込まない為の知識の紹介を目的にしています。

* replの紹介
* 関数型プログラミングなので、当然関数の使い方をメインに説明します。
  * (再帰、無名関数、静的スコープ、高階関数、合成関数、名前渡し)
* 関数型プログラミングで頻繁に用いられるデータ型である代数的データ型
* 業務でよく使われるOption/Either/Future及びfor式について
* Scalaは静的型付言語なので、型の話をします。
* Scalaの余談
* 一番最後に、今回は詳細は話しませんが、関数型プログラミングまわりの技術について紹介します。

## (まずはじめに)replによるローカル実行テスト
* repl: Read Eval Print Loop
* ミニマムなコードで、シンタックスの動作確認や型の確認ができる。
* Scalaのスモールサイズの文法を学ぶのにはうってつけ

以下のコマンドで実行出来る。
```
sbt console
```
後は以下のようにインタラクティブにScalaのコードを打ち込んで行けば即座に実行結果が得られる。
(電卓にも使える)
```
scala> 1 + 1
res0: Int = 2

scala>
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

## 関数型プログラミング言語の用語

* 変数へ代入することを**束縛(bind/binding)**という習慣がある。
  * 「変数aに値を束縛する」という言い方をする。
  * 数理論理学の用語に由来している。
  * [自由変数と束縛変数 - Wikipedia](https://ja.wikipedia.org/wiki/%E8%87%AA%E7%94%B1%E5%A4%89%E6%95%B0%E3%81%A8%E6%9D%9F%E7%B8%9B%E5%A4%89%E6%95%B0)
  * 特に、ローカルで定義され代入された変数は束縛変数、グローバル変数などローカルで定義されていない変数の事を自由変数と言ったりする。
    * (後述するレキシカルスコープ参照)
* プログラムの実行、特にプログラム中の部分的な式を実行することを**評価(evaluate)**という。

## 再帰(Recursion)
* 再帰とは自分自身を自分自身の中に持つような構造。
  * プログラミングでは、再帰的なデータ構造や、再帰呼出しなどがある。
  * [再帰 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%86%8D%E5%B8%B0)
* Scalaでのループはコレクション関数を使用することが(多分)殆どなので、再帰呼出しは、あまり使わないが、たまに使う。
  * (コレクション関数は後述)
* リスト(構造)についても使えるが、コレクション関数を使用すればいい場合が多い(要出典)ので殆ど使わないはず。
* 木構造のような再帰的なデータ型がある場合や分割統治法を使ったアルゴリズムの場合は、使うとわかりやすいコードが書ける。
  * 分割統治法
    * 大きな問題を小さな部分問題に分割し、個々の小さな部分問題を解決しながら、
      その部分問題の解答結果のマージを繰り返し、最終的に元の問題を解くようなアルゴリズム。
    * [分割統治法 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%88%86%E5%89%B2%E7%B5%B1%E6%B2%BB%E6%B3%95)
    * 次のQuicksortが典型例。
  * Quicksort(あるいは、関数型プログラミングにおける偽のQuicksort)
    * Javaでクイックソート:
      [【Java】クイックソートのアルゴリズムのテスト - Qiita](https://qiita.com/gigegige/items/4817c27314a2393eb02d)
    * 関数型プログラミングでは偽のクィックソートというのがある。
```
def quicksort[A](ls: Seq[A])(implicit ord: Ordering[A]): Seq[A] = ls match {
    case Nil => Nil
    case a::as => quicksort(as.filter(ord.lt(_, a))) ++ Seq(a) ++ quicksort(as.filter(ord.gteq(_, a)))
}
```
```
scala> quicksort(List(5, 6, 7, 4, 3, 10, 2, 8, 0, 3))
res38: Seq[Int] = List(0, 2, 3, 3, 4, 5, 6, 7, 8, 10)
```
  * 木構造のデータ型 + matchによるパターンマッチで再帰を使う例
```
sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]

def sum[A](t: Tree[A], f: (A, A) => A): A = t match {
  case Leaf(a) => a
  case Node(l, r) => f(sum(l, f), sum(r, f))
}
```
```
scala> val d = Node(Node(Leaf(10), Leaf(20)), Leaf(1))
d: Node[Int] = Node(Node(Leaf(10),Leaf(20)),Leaf(1))

scala> sum(d, (a:Int, b:Int) => a + b)
res36: Int = 31

scala> val e = Node(Node(Leaf("a"), Leaf("b")), Leaf("c"))
e: Node[String] = Node(Node(Leaf(a),Leaf(b)),Leaf(c))

scala> sum(e, (a: String, b: String) => "(" ++ a ++ " " ++ b ++ ")")
res44: String = ((a b) c)
```
* 末尾最適化
  * Scalaの再帰はをする場合としない場合がある。
    * 末尾再帰形式になっていない場合は、末尾最適化が行われない。

```
def fact1(n: Int): Int = if (n < 1){
  1
} else {
  n * fact(n - 1)
}
```
    * 自分自身を呼び出しのみ、かつ末尾再帰形式になっている場合
      計算結果を保持する変数を一つ追加する。
```
def fact2(n: Int, a: Int): Int = if (n < 1){
  a
} else {
  fact2(n - 1, n * a)
}
```
```
scala> fact2(10, 1)
res39: Int = 3628800
```
        * 末尾再帰では基本的に綺麗なプログラムを書こうと考えない事がポイント(末尾再帰の時点で大して綺麗に書けてない)。
        * whileループを無理矢理、再帰に書き換えるような勢いが大切。
        * 末尾再帰形式の引数はいわゆる「変化するmutableな変数」を表している。(引数で副作用を引き回すスタイル)
    * Scalaでは相互再帰では末尾最適化をしない。
      * 例えば以下のようなプログラム、

```
def odd(n: Int): Boolean = if (n = 1) {
  true
} else {
  even(n - 1)
}

def even(n: Int): Boolean = if (n = 0) {
  true
} else {
  odd(n - 1)
}
```
は、oddとevenを相互に呼び出し、自分自身の末尾で関数を呼び出す。

[スタックレスScala](http://halcat.org/scala/stackless/index.html)

* Trampolineで末尾最適化をする。
  (TODO: Trampolineで末尾再帰の例を入れる)
* なので結局、原理主義的に再帰のみでゴリゴリimmutableなコードも書ける(はずだ)が、
  可読性や後でメンテナンスすることを考えるなら、Scalaの場合はwhile文なりfor文を使った方が現実的な場合もあるかも知れない。
* 関数全体で見ると、参照透過な関数になっていればOKという考え方。

## 無名関数(ラムダ抽象)
```
scala> val f = ((a: Int) => a + 1)
f: Int => Int = $$Lambda$4017/598382925@72f0dff7

scala> f(1)
res19: Int = 2

scala> f(2)
res20: Int = 3
```
* おなじみの無名関数。大体、無名関数かラムダ式でググったら出てくる。ラムダ式とはあんまり言わない(方がいい)(※個人の意見です)。
* 第一級オブジェクトとしての関数(データとして扱う事が出来る関数): 関数を渡す、値として保持することができる。
* 無名関数の式が評価されると、**関数オブジェクト**が生成される。
* オブジェクトなのでデータを持たせる事ができる。後述のレキシカルスコープ参照。
* C言語の関数のポインタと何が違うのか? / JavaのStrategyパターンと何が違うのか。
  * 関数のポインタと違い、データ(値)を保持することができる。
  * JavaのStrategyパターンと違い、インターフェースを必要としない。

## レキシカルスコープ(Lexical scope, 静的スコープ)
* "Scope"とは範囲のこと。"Lexical"とはLiterallyくらいの意味で深い意味はない。
* レキシカルスコープとは、静的にどの変数がどのタイミングで代入された
* Scalaの変数(valや引数)の有効範囲は、レキシカルスコーピングによって決定される。
* 変数の値を取り出す時、(Scalaの)レキシカルスコープでは、ネストした変数定義において最も内側で定義された変数を参照する。

例えば、以下の２つの例。
```
scala> {val a = 1; ((a:Int) => ((a:Int) => a)(3))(2) }
res5: Int = 3
```
または、
```
scala> {val a = 1; {val a = 2; {val a = 3; a} } }
res11: Int = 3
```
この時、aは、最も内側にあるaは、ネストしている中で最も手前で定義された変数(引数)aの値を参照する。
次の例では、最も内側のスコープを抜けているので上記とは別の値を見に行く。
```
scala> {val a = 1; ((a:Int) => {((a:Int) => a)(3); a})(2) }
res10: Int = 2
```
または、
```
scala> {val a = 1; {val a = 2; {val a = 3;} a} }
res14: Int = 2
```
* レキシカルスコープの仕組み自体は、Scalaだけでなく、JavaScriptやTypeScript(たぶん)、Ruby(たぶん)、Pythonでも共通。
* 但し、JavaScriptにはFunctionスコープというのがあるので注意。
* 動的スコープを採用している言語はほとんどない。但し、implicit parameterは動的スコープ的な役割に近い。
  現代で実用的な動的スコープがメインの言語はEmacs Lispくらい。。。
* implicit parameterは、社内だとFutureのExecutionContextがよく使われる。

## クロージャ(Closure)
* ブロックから抜けだした関数オブジェクトは、レキシカルスコーピングによって保持した変数の参照を保持し続ける。
  このような関数オブジェクトの事をクロージャ(Closure)という。
* プログラミング言語のClojureの事ではない。

例えば以下では、関数オブジェクトがaseqという変数名が保持しているリストの参照を持つ。
```
scala> val haveSeq = {val aseq = Seq(1, 2, 3, 4, 5); ((index: Int) => aseq(index)) }
haveSeq: Int => Int = $$Lambda$4005/1227571506@23364fcf

scala> haveSeq(1)
res8: Int = 2

scala> haveSeq(2)
res9: Int = 3
```
一番外側で定義された変数haveSeqは、ブロック内部で生成された関数オブジェクト(クロージャ)を束縛している。
この関数オブジェクトに引数を与える事で、aseqに束縛されたリスト(Seq)の値を見る事が可能になる。

* 上記のような書き方により、JavaやScalaで指定するprivateよりも更に細かいスコープ(変数の有効範囲)の制御が可能になる。

例えば、以下のように2つの関数からのみ参照可能なHashmapを定義できる。
```
scala> val (f, g) = { val hmap = Map("ab"-> 1, "ac" -> 2);
     | (((s: String) => hmap(s)), ((s: String) => hmap("a"++s))) }
f: String => Int = $$Lambda$4011/179665104@2da7b9bf
g: String => Int = $$Lambda$4012/49123780@295afa48

scala> f("b")
java.util.NoSuchElementException: key not found: b
  at scala.collection.immutable.Map$Map2.apply(Map.scala:129)
  at .$anonfun$x$1$1(<console>:12)
  at .$anonfun$x$1$1$adapted(<console>:12)
  ... 36 elided

scala> g("b")
res17: Int = 1

scala> f("ac")
res18: Int = 2
```

* 勿論、リストやハッシュマップだけでなく、関数を保持する関数を作ったり、関数を保持する関数を保持する関数のような物も(機能的には)作れる。

* ただし、関数オブジェクトを濫用し続けると、不用意に意図しないクロージャを生成してしまう事も考えられる。
  * このような場合、GCによって回収されない参照をいつまでも保持し続けることになってしまう。
  * (とは言え、普通に書いている限りだとこのようなバグは殆ど無いかも知れない)

* クロージャを使うことで計算を遅延させることが可能。典型的な使用例がFutureによるコールバック。
  * `dao.findById(id)`がFutureを返す時、mapに渡された`row =>`以降は、Futureの結果が返ってくるまで実行されない。
```
dao.findById(id).map { row => row.name }
```
  * クロージャの性質を利用して遅延評価を手で作る事が出来る。

val x = 1
locally {
  import p.X.x
  x
}

* クロージャでリスト構造を作る。
  * ルール: データ構造やクラスは使わない。
  * 単方向連結リストリスト(いわゆるLinkedList)
  * 関数型プログラミングにおける大道芸の一つ。
  * 以下例。
```
scala> val cons = (x: Int, xs: Int => Any) => ((i: Int) => if (i == 0) x else xs(i - 1))
cons: (Int, Int => Any) => Int => Any = $$Lambda$3648/212142471@1c3cc65d

scala> val emptyF = (x: Int) => null
emptyF: Int => Int = $$Lambda$3649/558084802@db6ad80

scala> val ls = cons(4, cons(3, cons(2, emptyF)))
ls: Int => Any = $$Lambda$3650/2033873015@4f08ca31

scala> ls(1)
res57: Any = 3

scala> ls(3)
res58: Any = null

scala> ls(2)
res59: Any = 2
```
関数がAny型を返す、リストの上限を超えた時nullを返すのは、簡単のため。
consでリストを構築する。空リストはemptyFで表現する。

## 高階関数(High-order function)
* 関数オブジェクトは値として他の関数に渡したり、

## 関数合成
* 2つの関数を合成する。数学の合成関数と考え方は同じ。: f(g(x)) = (f . g)(x)
* 合成関数用の関数として、compose/andThenが用意されている。
* 例えば以下の場合、
```
val withComma = ((ls: Seq[String]) => ls.mkString(","))
val trimString = ((ls: Seq[String]) => ls.map(_.trim.toInt))
val multiply20 = ((ls: Seq[Int]) => ls.map (_ * 20).map(_.toString))
```
Stingのリストの要素を20倍してカンマ区切りの文字列にしたい。。。
```
val ls = Seq(" 20", " 30 ", "40 ", "50")
```
しかもなんか変な空白入ってる。。。
```
scala> withComma(multiply20(trimString(ls)))
res31: String = 400,600,800,1000
```
括弧が多い。。。
```
scala> (withComma compose multiply20 compose trimString)(ls)
res32: String = 400,600,800,1000
```
さらにこれを関数化したい。。。
```
scala> val multiply20withComma = withComma compose multiply20 compose trimString
multiply20withComma: Seq[String] => String = scala.Function1$$Lambda$605/1525241607@1f18de49

scala> multiply20withComma(ls)
res33: String = 400,600,800,1000
```
composeだとわかりにくい? => そこでandThen

* composeとandThenは順序が逆。
composeの場合
```
scala> (((s:String) => s.toInt) compose ((x:Int)=> x.toString))(20)
res24: Int = 20
```
andThenの場合
```
scala> (((x:Int)=> x.toString) andThen ((s:String) => s.toInt))(20)
res22: Int = 20
```
* andThenを使って前述のコードを書き直す。
```
scala> val multiply20withComma = trimString andThen multiply20 andThen withComma
multiply20withComma: Seq[String] => String = scala.Function1$$Lambda$4021/295193997@7d9759a

scala> multiply20withComma(ls)
res34: String = 400,600,800,1000
```
処理の順番が明確になり、多少は分かりやすくなった。andThenは、fluent interfaceかも知れない。。。

* 使いすぎると分かりづらくなる事も多いが、Scalazだと頻繁に使われていたりする。
* Playのアクション合成(action composition)などでも登場する。

## コンビネータ(Combinator)

## 部分関数(Partial function)

## 部分適用/カリー化(Partial apply/curring)
* 部分適用とカリー化は間違えやすいことで有名。

## 名前渡し(Call-by-name)

## 代数的データ型(Algebraic data type)
* 関数型プログラミングだと実装とデータ型を分離する傾向がある。(要出典)
  * データに実装が付随しがちなオブジェクト指向プログラミングとは少し違う。。。
* 代数的データ型とパターンマッチにより、コード
* 再帰的(帰納的)に定義される有限のデータ構造(Streamなど無限のデータ構造というのもあります)
  [具象不変コレクションクラス](http://docs.scala-lang.org/ja/overviews/collections/concrete-immutable-collection-classes.html) を参照。
* case classには、`final`を付けること推奨。
  * なぜ、final case classを付けないと行けないのかは以下を参照。
    * [Should I use the final modifier when declaring case classes? - StackOverFlow](https://stackoverflow.com/questions/34561614/should-i-use-the-final-modifier-when-declaring-case-classes)
* Option型の例

## パターンマッチ
* [Scalaのパターンマッチ - Qiita ](https://qiita.com/techno-tanoC/items/3dd3ed63d161c53f2d89)
* Scalaでは、リテラル(定数)、型によるマッチ、正規表現、構造に関するマッチなどが可能。
* データ構造(リストやタプル、case classなど)を構造的に分解して変数に代入できる。
* オブジェクトにunapplyが定義されていれば、パターンマッチが可能。
  * [パターンマッチをもっと便利に-extractor(抽出子)による拡張](http://yuroyoro.hatenablog.com/entry/20100709/1278657400)
* if-else式と比較して、データ構造に対する網羅的なマッチが可能。網羅的でない場合は警告がでる。(但し、エラーにはならない)

## リスト
* ScalaだとSeqで書くのがマナーらしい。
* 標準のArrayListとLinkedListがある。
* 関数型のLinkedList(主に単方向連結リスト)は特殊な性質がある。
* mapやfilter、foldで綺麗に書けない場合は、Scalaのリストのパターンマッチと再帰で書くやり方もある。
  * 前述のquicksortの例を参照。
* [ScalaのSeqリファレンス - Qiita](https://qiita.com/f81@github/items/75c616a527cf5c039676)
* 関数型プログラミングではリスト操作関数を多用することが多い。 => slickに繋がる
  * SQLもまた宣言型言語なので、map/filterなどの組み合わせはSQLに変換しやすいのかもしれない。。。

### コレクションメソッド
* map, filter, foldあたりが王道。flatMap, flatten

## Option, Either, Future, 例外, for式
* Option - nullを型レベルで表現する。
  * Optionはnullableな場合に使用する。
  * head, getは使わない。=> headOption, getOptionでnullableとなるようなケースは代わりの処理を用意する。
  * 意味のないマジックナンバーを埋め込まない。

* https://dwango.github.io/scala_text/error-handling.html

https://alvinalexander.com/scala/best-practice-option-some-none-pattern-scala-idioms
http://yuroyoro.hatenablog.com/entry/20100719/1279519961
catingとかいう魔境
https://stackoverflow.com/questions/40308075/scala-what-is-opt-keyword
opt、某企業の事ではない。

* Either - エラーの制御をする
  Rightは、Leftは、
  * PlayframeworkだとActionFunctionなどで使用される。
  * Right/Leftで表現しきれなくなった場合、3パターンの結果が返ってくる場合などは、代数的データ型で独自の型を定義した方がよさそう。

* Future(Success/Failure) - 非同期プログラミング
  * 例外投げても(多分)受け取ってくれないことで私の中で有名(誇張表現)。
  * JavaScriptで言う所のコールバック関数

* 例外
  * Javaと違い、非チェック例外。
  * try-catchの場合はNonFatalでキャッチする。

### for式
* for式は、map/flatMapに変換される。
http://scala-lang.org/files/archive/spec/2.12/06-expressions.html
```
for {
  a <- abcDao.find(id)
  b <- abcDao.find(a)
} yeild f(a, b)
```

## Scalaの3つのimplicit
* [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)
* implicit conversion, implicit class, implicit parameterがある。

### 暗黙の型変換(implicit conversion)
* 暗黙の型変換は推奨されていない/しない人が多い。
* 公式のドキュメントですら、"implicit conversions can have pitfalls"と書かれている。
  * [TOUR OF SCALA IMPLICIT CONVERSIONS](https://docs.scala-lang.org/tour/implicit-conversions.html)
* implicit conversionに対する否定的なコメントは以下を参照。
  * [Scalaのimplicit conversionってなんだ？](http://blog.livedoor.jp/sylc/archives/1553449.html)
  * [Scalaでimplicits呼ぶなキャンペーン](http://kmizu.hatenablog.com/entry/2017/05/19/074149)
  * [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)

### 拡張メソッド(implicit class / 既存の型を拡張する)
* 継承せずに(?)既存の型を拡張する。

### 暗黙のパラメータ(implicit parameter)

## 型関連
* 複雑な型を定義してもあんまり意味ないという側面はある。
* 気を抜いているとAnyに推論されるらしい。
* http://keens.github.io/slide/DOT_dottynitsuiteshirabetemita/
* 型パラメータ: Javaで言う所のジェネリクス。
  * `trait A[B] { def b():B; }`の`B`
  * 型パラメータで指定できる共変、反変、非変については、[型パラメータと変位指定 - ドワンゴの研修テキスト](https://dwango.github.io/scala_text/type-parameter.html) を参照。以下、自分用のメモ。
    * 共変(`[+B]`): A extends Bの時のみ、val a:G[B] = b:G[A]が可。
    * 反変(`[-B]`): A extends Bの時のみ、val a:G[A] = b:G[B]が可。
    * 非変(`[B]`): A = Bの時のみ、val a:G[A] = b:G[B]が可。
    * 上界(`[B <: A]`): BがAを継承の性質。
    * 下界(`[B >: A]`): BがAのスーパークラスである性質。
  * 型パラメータに様々な制約を付ける事で、クラス、インターフェースなしにジェネリックな関数を定義することが可能。
    * 構造的部分型を参照。
* 型エイリアス: 型に別名を付けることができる。型定義の長さが絶望的に長くなった時に有効。
  * `type String3 = (String, String, String)`
* 型コンストラクタ: 型を引数にとり別の型を生成する型。
  * プログラミングでよく目にするものとしては、Option, Either, Futureなどが典型的。
  * 型パラメータが引数にとる。
  * Option[+A]という型コンストラクタに対して、Option[String]という型を定義する時などに使われる。
  * 他の関数型言語だとFunctorなどがよく出てくる。

* 型クラス: "既存の型に後付けするタイプのインターフェース"(次の記事から引用)
   * [型クラスの雰囲気をつかんでScala標準ライブラリの型クラスを使ってみる回 - 水底](http://amaya382.hatenablog.jp/entry/2017/05/13/195913)
  * あるオブジェクトがどのような振る舞いをするかまとめた物。
  * 但し、Javaで言う所のinterfaceとは違い、後付で実装することができ、拡張に対して、開かれている。
  * https://togetter.com/li/1113557

### Any, AnyRef, AnyVal
* Anyは、全ての型の親クラス。
* AnyValは、定数系の型のすべての親クラス。
* AnyRefは、参照型となる型のすべての親クラス。
* [Scala Any](http://www.ne.jp/asahi/hishidama/home/tech/scala/any.html)

### 構造的部分型
* 動的型付け言語(Ruby, Pythonなど)は、名前でメソッドを引っ張ってくるので、例えば、
```
class A:
    def func1:
        〜

class B:
    def func1:
        〜

def func2(objX):
  objX.func1()
  〜

func2(A())
func2(B())
```
となるような、一般的な`func2`を定義できる。`func1`を持つようなオブジェクトを一般的に引き受けるような関数を定義したい。
勿論、class Aやclass Bの定義を変更することなしに。
注意)(Java)interfaceだとfunc2に与えられるオブジェクトのクラス全てにinterfaceを付けなければいけない。
例えば、idとnameを持つようなRow。
```
case class AbcRow(id: Long, name: String, paramA: String, paramB: String)
```
このRowのリストから、idとnameのタプルのリストを抽出したい。。。
以下のような関数を定義する。
```
def getIdName[R <: {val id: Long; val name: String;}](rows: Seq[R]): Seq[(Long, String)] =
    rows.map { row => (row.id, row.name) }
```
以下のように実行する。
```
scala> val row = Seq(AbcRow(1, "a", "paramA", "paramB"), AbcRow(2, "b", "paramA", "paramB"), AbcRow(3, "c", "paramA", "paramB"));
row: Seq[AbcRow] = List(AbcRow(1,a,paramA,paramB), AbcRow(2,b,paramA,paramB), AbcRow(3,c,paramA,paramB))
scala> getIdName(row)
res3: Seq[(Long, String)] = List((1,a), (2,b), (3,c))
```
* 条件を満たす型(データ型)を定義しておき、テンプレートを書く。
* これ以外だとローンパターンなど。

### Scalaの3つのdependent * type
http://wheaties.github.io/Presentations/Scala-Dep-Types/dependent-types.html#/
* Scalaのdependent * type (関数型言語で言われる所の依存型とは違う(らしい))
* path-dependent type
  * 生成された経路によって、同じpackageの同一オブジェクト(クラス)の型の場合でも、別々の型とみなされる。
  * [What is meant by Scala's path-dependent types? - StackOverFlow](https://stackoverflow.com/questions/2693067/what-is-meant-by-scalas-path-dependent-types)
* dependent method type
  * [Scala dependent method types ? - Gist](https://gist.github.com/xuwei-k/1306328/82530a4d2451b68a17f7c03448d6ab88da0bc575)
* dependent object type
  * [Dependent Object Types (DOT)](https://github.com/namin/dot)
    * "The DOT calculus proposes a new type-theoretic foundation for languages like Scala."
  * Dotty向けの型システム。Scala3以降の話なので今回は言及しない。

## 余談
### if-internal-external-conversion
if(a) f(1) else f(2)
f(if (a) 1 else 2)

### Symbol
* ScalaにもRubyと同じようなSymbolがある。先頭にquoteを付ける。
```
scala> 'sym
'sym
res2: Symbol = 'sym
```

### Scalaにおける小括弧の()と中括弧の{}違い
小括弧は式を、中括弧はブロックを表す。
* [Scalaにおける括弧()と中括弧{}の違い](http://xuwei-k.hatenablog.com/entry/20130221/1361466879)

### private[X]
アクセス修飾子priavte[X]は、パッケージX以下まで参照可能。
* [【Scala】 アクセス修飾子と限定子 - takafumi blog](http://takafumi-s.hatenablog.com/entry/2015/07/31/152642)

### Dotty
Scalaの新しいコンパイラ。
* [Dottyによる変更点と使い方 - 水底](https://qiita.com/kmizu/items/10940b4c46876ae8a12d)

### 便利なチートシート
* [SCALA CHEATSHEET SCALACHEAT](https://docs.scala-lang.org/cheatsheets/index.html)

## やらない関数型言語まわりのトピック
* 継続(continuation)
* プログラムの融合変換(program fusion, deforestation)
* なんとかモルフィズム(正確にはRecursion Schemeという。catamorphism, anamorphism, hylomorphism ...)
* モナド、コモナド(特に普段は意識する必要はない)
* モノイド
* 抽象解釈(abstract interpretation)
* コンビネータロジック、ラムダ計算、圏論
* 証明、定理証明支援系
* プログラム意味論
* 型システム
* 依存型(dependent type)

## 参考文献
* [関数合成のススメ 〜 オブジェクト指向プログラマへ捧げる関数型言語への導入その1](http://yuroyoro.hatenablog.com/entry/20120203/1328248662)
* [Scalaの経路依存型（path-dependent type）とは？](https://53ningen.com/path-dependent-types/)
* [Scala2.10.0のDependent method typesと型クラスを組み合わせた『The Magnet Pattern』がヤバい件](http://yuroyoro.hatenablog.com/entry/2013/01/23/192244)
* [Scalaに関して知っておくべきたった一つの重要な事](http://kmizu.hatenablog.com/entry/20120504/1336087466)
* [代数的データ型とshapelessのマクロによる型クラスのインスタンスの自動導出](http://xuwei-k.hatenablog.com/entry/20141207/1417940174)
* [Scalaにおける細かい最適化のプラクティス](http://xuwei-k.hatenablog.com/entry/20130709/1373330529)
* [Scala COLLECTIONS 性能特性](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)

## 読み物
* [関数型言語でのデザイン手法 - togetter](https://togetter.com/li/25283)
* [オブジェクト指向設計の原則と関数型プログラミング](https://www.infoq.com/jp/news/2014/03/oo-functional-programming)
* [Scalaで型レベルプログラミング(日本語訳)](https://github.com/yuroyoro/Japanese_Translations_of_Scala_Articles/tree/master/source/ja/type_level_programming_in_scala)
  * 4章まで(?)(2018/01/27現在)
