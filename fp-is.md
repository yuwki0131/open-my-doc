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
* 「状態」が変わらない。
* 再代入を行わないだけではなく、データもまた(いわゆるクラスのインスタンスが一度生成されたらそれ以降は)不変である必要がある。
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
* 副作用とは、数学には存在し得ない計算機のみがもつ特有な作用のこと。。。らしいです。
  * 出典: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.79.733&rep=rep1&type=pdf
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

* (関数型プログラミング関係ない)オブジェクト指向用語:レシーバ
  * オブジェクト指向において、メッセージを受け取るオブジェクトの事をレシーバという。
  * あるオブジェクトのメソッドを呼び出す時、そのオブジェクトに対しメッセージ送るとみなす為、レシーバと呼ばれる。
    * [オブジェクト指向プログラミング - Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%AA%E3%83%96%E3%82%B8%E3%82%A7%E3%82%AF%E3%83%88%E6%8C%87%E5%90%91%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0)
  * value.methodA()のvalueの事。

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
```scala
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
  * 関数型言語で行われるコンパイラの処理(プログラムの変換)の一種。
  * 再帰呼出しは、コンパイル時に「末尾再帰」形式になっている場合、コンパイル時に(whileのような)ループに変換される。
    * ループに変換されない場合、再帰は、通常の関数呼び出しの連鎖となり、stackを消費してしまい、StackOVerFlowErrorになってしまう。
    * ループ回数が多い場合(JVMの場合、1000〜数千回以上)の場合は、末尾再帰形式でループを記述しないと、StackOverFlowになる。
      * 個人的な経験則だと1000回未満のループだと、SOFにはならない事が多い。(勿論、実装による部分が大きい)
    * 大量にループを繰り返す場合は末尾再帰が必須となる。
    * stackを消費しすぎないタイプのループの場合は、この限りではない。(例えば、消費したstackが戻される場合など)
  * 末尾再帰形式は関数呼び出し時に、stackを必要としないような再帰呼出しの形式。
    * 自分自身を再帰的に呼び出す際にその呼び出し以外の処理が残っていない形で自分自身を呼び出すような関数呼び出し形。
  * Scalaの再帰は末尾最適化をする場合としない(できない)場合。
    * 末尾再帰形式になっていない場合は、末尾最適化が行われない。
例えば、次のような階乗を行う関数の場合。(10の階乗(fact(10))は、10! = 1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10)
```
def fact1(n: Int): Int = if (n < 1){
  1
} else {
  n * fact1(n - 1)
}
```
    * 自分自身を呼び出しのみ、かつ末尾再帰形式になっている場合
      * 以下の関数では、計算結果を保持する変数(アキュームレータ)を一つ追加している。
```
def fact2(n: Int, a: Int): Int = if (n < 1){
  a
} else {
  fact2(n - 1, n * a)
}
```
以下では、`fact2(10, 1)`の計算しか行っていないが、例えば、`fact1(10000)`と`fact2(10000, 1)`では、
fact1の場合、StackOverFlowになってしまうが、fact2ではコンパイラの最適化(末尾再帰最適化)によりStackOverFlowにならない。
```
scala> fact2(10, 1)
res39: Int = 3628800
```
        * 末尾再帰では基本的に綺麗なプログラムを書こうと考えない事がポイント(末尾再帰の時点で大して綺麗に書けてない)。
        * 末尾再帰形式では、計算の途中結果を保存する引数を追加する事が多い。
        * もちろん、プログラムの種類によっては、末尾再帰形式を気にしなくても、末尾再帰形式になってしまうケースもある。
        * 末尾再帰形式にするためには、whileループを無理矢理、再帰に書き換えるような勢いが大切。
        * 末尾再帰形式の引数はいわゆる「変化するmutableな変数」を表している。(引数で副作用(再代入)を引き回すスタイル)
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
この形式も同様に、関数呼び出し以外の処理は残っていないが、末尾最適化されない。

* [スタックレスScala](http://halcat.org/scala/stackless/index.html)
* Trampolineで末尾最適化をすることが可能。
  (TODO: Trampolineで末尾再帰の例を入れる)
* なので結局、原理主義的に再帰のみでゴリゴリimmutableなコードも書ける(はずだ)が、
  * 可読性や後でメンテナンスすることを考えるなら、Scalaの場合はwhile文なりfor文を使った方が現実的な場合もあるかも知れない。
  * 関数全体で見ると、参照透過な関数になっていればOKという考え方(もアリ)。
  * だだし、関数型プログラミングでのループは、再帰やfor/whileのような構文ではなく、
    mapやfilterといったリスト(コレクション)関数を使うのが一般的。
    大半のループはコレクション関数で記述した方が、可読性が高く、メンテナンスもしやすい簡潔なコードが書ける。
    * コレクション関数に関しては後述。

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
  * 関数オブジェクトは、コンストラクタによって生成される他のJavaオブジェクトと同類のオブジェクト
    (型となるクラスを持ち、インスタンスとして扱われるように)なる。
* オブジェクトなのでデータを持たせる事ができる。後述のレキシカルスコープ参照。
* C言語の関数のポインタと何が違うのか? / JavaのStrategyパターンと何が違うのか。
  * 関数のポインタと違い、データ(値)を保持することができる。(データ保持に関してはクロージャを参照)
  * JavaのStrategyパターンと違い、インターフェースを必要としない。

## レキシカルスコープ(Lexical scope, 静的スコープ)
* "Scope"とは範囲のこと。"Lexical"とはLiterallyくらいの意味で深い意味はない。
* レキシカルスコープとは、静的に、ある変数を参照した時、どのタイミングで代入された値が参照されるかが決まる。
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
* レキシカルスコープにより、関数定義中にその関数のブロック内で定義されていない変数を持たせることができ、そのような変数を自由変数という。
* 自由変数を含む(使用した)関数によって生成された関数オブジェクトの事をクロージャ(Closure)という。
  * クロージャは、自由変数に束縛された値(の参照)をデータとして保持する。
  * ブロックから抜けだした関数オブジェクトは、レキシカルスコーピングによって保持した変数の参照を保持し続ける。
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

* クロージャにより変数はそのスコープの外を出ても有効である場合がある。
  つまり、クロージャにより、ローカル変数は、関数本体が終了しても生き残る。
  変数が生存している(有効である)期間のことを**エクステント**という。

* クロージャとは逆に、関数内に自由変数を含まないような関数のことを**コンビネータ**という。
  ただし、Scalaだと、パーサコンビネータ以外ではコンビネータという言い方はあまりされない。

* クロージャを使うことで計算を遅延(将来に実行)させることが可能になる。典型的な使用例がFutureによるコールバック。
  * `dao.findById(id)`がFutureを返す時、mapに渡された`row =>`以降は、Futureの結果が返ってくるまで実行されない。
```
dao.findById(id).map { row => row.name }
```
(TODO: 👇)
val x = 1
locally {
  import p.X.x
  x
}

* クロージャのみでリスト構造(データ構造)を作ることが可能。
  * ルール: データ構造やクラスは使わない。
  * 関数型プログラミングにおける大道芸の一つ。
  * 例えば、単方向連結リスト(いわゆるLinkedList)は、以下のように簡単に実装することができる。
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
* 関数オブジェクトは値として他の関数に渡したり、関数を受け取る、変数に保持するなどの処理を記述することができる。
* いわゆるJavaの他のオブジェクトと同様の扱いが可能。
* 他の関数に渡す例。
```
scala> def add2(i: Int): Int = i + 2
add2: (i: Int)Int

scala> Seq(1, 2, 3).map(add2)
res4: Seq[Int] = List(3, 4, 5)
```
* 変数に保持しておく例。
```
scala> val x = add2 _
x: Int => Int = $$Lambda$3166/789982130@34bd23ed

scala> Seq(1, 2, 3).map(x)
res5: Seq[Int] = List(3, 4, 5)
```

* 部分適用/カリー化(Partial apply/curring)
  (TODO: 説明を書く)
  * 部分適用とカリー化は間違えやすいことで有名。
  * 部分適用は、必要な引数の一部まで渡す。
  * カリー化は、複数引数の関数を1引数の関数の
    * implicit parameterなどでよく使われる。

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

## 部分関数(Partial function)
  (TODO: 説明を書く)
http://tanakh.jp/posts/2011-12-25-partial-function-considered-harmful.html
http://yuroyoro.hatenablog.com/entry/20100705/1278328898

## 名前渡し(Call-by-name)
* 遅延評価の一種で、意味合いとしては、処理の実行を遅らせる為に使用する手法。関数呼び出し時の引数の値の渡し方の一種。
  * (余談)値の渡し方にはいくつか種類があり、call-by-nameの他にcall-by-value、call-by-reference、call-by-needなどがある。
  * (余談)Haskellなどの遅延評価は「必要渡し」(call-by-need)と呼ばれ、引数の値を計算せずに関数を実行する。
    関数の実行途中で引数の値が必要になった時に初めて、引数の値を計算する処理を始める。
    例えば、f(g(1), h(2))のような関数呼び出しがあった場合、
    Javaなどでは、g(1) → h(2) → f(g(1)の結果, h(2)の結果)という順で関数が呼び出されるが、
    Haskellでは、fを実行 → g(1)やh(2)の結果が必要になれば、適宜実行 → fの処理が終了という流れになる。
  (TODO: 説明を書く)

## 代数的データ型(Algebraic data type)
  (TODO: 説明を書く)
* 関数型プログラミングだと実装とデータ型を分離する傾向がある。(要出典)
  * データに実装が付随しがちなオブジェクト指向プログラミングとは少し違う。。。
* 再帰的(帰納的)に定義される有限のデータ構造(Streamなど無限のデータ構造というのもあります)
  * [具象不変コレクションクラス](http://docs.scala-lang.org/ja/overviews/collections/concrete-immutable-collection-classes.html) を参照。
* case classには、`final`を付けること推奨。
  * なぜ、final case classを付けないと行けないのかは以下を参照。
    * [Should I use the final modifier when declaring case classes? - StackOverFlow](https://stackoverflow.com/questions/34561614/should-i-use-the-final-modifier-when-declaring-case-classes)
* Option型の例
  (TODO: 説明を書く)

## パターンマッチ
* [Scalaのパターンマッチ - Qiita ](https://qiita.com/techno-tanoC/items/3dd3ed63d161c53f2d89)
* Scalaでは、リテラル(定数)、型によるマッチ、正規表現、構造に関するマッチなどが可能。
* データ構造(リストやタプル、case classなど)を構造的に分解して変数に代入できる。
* オブジェクトにunapplyが定義されていれば、パターンマッチが可能。
  * [パターンマッチをもっと便利に-extractor(抽出子)による拡張](http://yuroyoro.hatenablog.com/entry/20100709/1278657400)
* if-else式と比較して、データ構造に対する網羅的なマッチが可能。
  網羅的でない場合は警告がでる。(但し、エラーにはならない)
  **パターン漏れが防げるので積極的に活用していきたい。**
* リストに対するパターンマッチ
  * 例えば、以下のコードはパターンマッチで書き換えた方が、ロジックがシンプルになる。
```
if (ls.isEmpty) 1 else ls.head
```
は、リスト`ls`についてのパターンマッチ、
```
ls match { case Nil => 1; case x::xs => x }
```
と書き直せる。コードが長くなった時に、else節で、ls.headを使う時に、lsが空かどうかを手前の条件節でチェックしているかどうかを
考慮する必要が無くなる。
* Optionに対するパターンマッチ
  * 例えば、次のようなケースも、リストと同様に書き直せる。
```
if (x.isEmpty) "hogehoge" else x.get.toString
```
存在するパターンを以下のように、列挙する。
```
x match { case None => "hogehoge"; case Some(x) => x.toString }
```
* パターンマッチの場合、構造のチェックと同時に、データ型から値の取り出し、変数への束縛まで行う。
* ただし、以下のような書き方をした場合、パターンマッチの本来の意味はなくなる(ただのif-else式と基本的に同じ意味しかなくなる)。
```
x match {
 case _ if x.isEmpty => "hogehoge"
 case _ => x.get.toString
}
```
* パターンマッチでのifによる条件追加は以下のようなケースだと有効。
```
x match {
 case Some(x) if x == "a" => "hogehoge"
 case Some(x) => "fugafuga"
 case None => "piyopiyo"
}
```

## リスト
  (TODO: 説明を書く)
* ScalaだとSeqで書くのがマナーらしい。
* 標準のArrayListとLinkedListがある。
* 関数型のLinkedList(主に単方向連結リスト)は特殊な性質がある。
* 関数型言語でのループは主に、リストとリスト操作関数の組み合わせで記述する。
  * Javaだとforeach構文で書くべき所は、リスト操作関数の組み合わせになる。
```
for (int i = 0; i < n; i++){
  〜
}
```
と書いていた箇所は、

```
(0 to n).forEach { i =>
}
```
と書ける。この時、`(0 to n)`は、0からnまでの数値が入ったリスト。
* 代表的なリスト操作関数
  * map: 元のリストの各要素を別のデータや
  * filter: 元のリストの各要素のうち、特定の要素だけを抜き出す。
  * fold: リストの各要素を集計するような処理を書く場合に使用する合計値を出す場合
  * その他、sort、reverse、sum、min/max、take(先頭からn個取り出す)、
* mapやfilter、foldで綺麗に書けない場合は、Scalaのリストのパターンマッチと再帰で書くやり方もある。
  * 前述のquicksortの例を参照。
* [ScalaのSeqリファレンス - Qiita](https://qiita.com/f81@github/items/75c616a527cf5c039676)
* 関数型プログラミングではリスト操作関数を多用することが多い。 => slickに繋がる
  * SQLもまた宣言型言語なので、map/filterなどの組み合わせはSQLに変換しやすいのかもしれない。。。

### コレクションメソッド
  (TODO: 説明を書く)
* map, filter, foldあたりが王道。flatMap, flatten

## Option, Either, Future, 例外, for式
* Option - nullableを型レベルで表現する。
  * Optionは値がnullableな場合に使用する。未定義処理や、想定外の値を返す時にnullを返していたようなケース。
  * head, getは使わない。
    * headOption, getOptionでnullableとなるようなケースは代わりの処理を用意する。
    * headやgetで値が存在しない場合、例外が投げられるため。
    * 値が存在しないケースというのは、通常、想定範囲内のケースであるため、nullやempty時に例外が投げられるのは好ましくない。
  * Int型などでも意味のないマジックナンバーを埋め込む必要がなくなる。
  * 基本的にはnull(or empty)チェック専用の構文だと思っている。(※個人の意見です。)
  (TODO: 説明を書く)

* https://dwango.github.io/scala_text/error-handling.html

https://alvinalexander.com/scala/best-practice-option-some-none-pattern-scala-idioms
http://yuroyoro.hatenablog.com/entry/20100719/1279519961
catingとかいう魔境

* Either - エラーの制御をする
  (TODO: 説明を書く)
  Rightは、Leftは、
  * PlayframeworkだとActionFunctionなどで使用される。
  * Right/Leftで表現しきれなくなった場合、3パターンの結果が返ってくる場合などは、代数的データ型で独自の型を定義した方がよさそう。

* Future(Success/Failure) - 非同期プログラミング
  (TODO: 説明を書く)
  * 例外投げても(多分)受け取ってくれないことで私の中で有名(誇張表現)。
  * JavaScriptで言う所のコールバック関数

* 例外
  (TODO: 例外の説明を書く)
  * Javaと違い、非チェック例外。
  * try-catchの場合はNonFatalでキャッチする。

### for式
* for式は、map/flatMapに変換される。
(TODO: for式の説明を書く)
http://scala-lang.org/files/archive/spec/2.12/06-expressions.html
以下のfor式があった時、コンパイル時に次のように、map/flatMapに展開される。
```
for {
  a <- abcDao.find(id)
  b <- abcDao.find(a)
} yeild f(a, b)
```
* mapがネストした場合、非常にコードが読みづらくなるため、for式で書けるなら、for式で書いた方が無難。

## Scalaの3つのimplicit
* [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)
* implicit conversion, implicit class, implicit parameterがある。
* implicit: 暗黙の〜
* implicit修飾子を付けて定義した場合、コンパイラが適宜、必要なメソッドや型を探索して自動的に適用してくれる。

### 暗黙の型変換(implicit conversion)
* 型変換(キャスト)する関数をimplicitに定義しておくことで自動的にキャストしてくれる。
  * 以下が定義の例。
```
implicit def d2i(d: Double):Int = d.toInt
```
  * implicit定義前
```
scala> val x:Int = 3.14
<console>:11: error: type mismatch;
 found   : Double(3.14)
 required: Int
       val x:Int = 3.14
                   ^
```
  * implicit定義後
```
scala> val x:Int = 3.14
x: Int = 3
```
* 暗黙の型変換は推奨されていない/しない人が多い。
* 公式のドキュメントですら、"implicit conversions can have pitfalls"と書かれている。
  * [TOUR OF SCALA IMPLICIT CONVERSIONS](https://docs.scala-lang.org/tour/implicit-conversions.html)
* implicit conversionに対する否定的なコメントは以下を参照。
  * [Scalaのimplicit conversionってなんだ？](http://blog.livedoor.jp/sylc/archives/1553449.html)
  * [Scalaでimplicits呼ぶなキャンペーン](http://kmizu.hatenablog.com/entry/2017/05/19/074149)
  * [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)
* 暗黙の型変換、利用するメソッドが複数あると、どっちを使えばいいのか分からなくなるので、エラーが出る。

### 拡張メソッド(implicit class / 既存の型を拡張する)
  (TODO: 説明を書く)
* 継承せずに(?)既存の型を拡張する。
* ちなみに、C#やTypeScriptにも同名の類似した機能がある。
* 例えば、String型に空だったら、None、そうでなかったら、Someで値を包んだ関数を定義したい場合、以下のように拡張できる。
```
implicit class OptionString(str: String){
  def opt(): Option[String] = if (str.isEmpty) None else Some(str)
}
```
これは、次のように使える。
```
scala> "".opt
res2: Option[String] = None

scala> "abc".opt
res3: Option[String] = Some(abc)
```
* レシーバにメソッドを生やす事ができるのが特徴。

* [Scala の implicit parameter は型クラスの一種とはどういうことなのか](http://nekogata.hatenablog.com/entry/2014/06/30/062342)
### 暗黙のパラメータ(implicit parameter)
*
  (TODO: 説明を書く)

## 型関連
* 複雑な型を定義してもあんまり意味ないという側面はある。
* 気を抜いているとAnyに推論されるらしい。
* http://keens.github.io/slide/DOT_dottynitsuiteshirabetemita/
* Scalaの型推論は漸進的型付と呼ばれ、基本的に前から推論していく。
  * (余談)HaskellやOCamlの型推論は、Hindley-Minlerと呼ばれる型システム(やその派生)により型を推論していく。
    この方法は、最も一般的な型を自動的に導出していく手法で、通常の場合、いわゆる型注釈(型ヒント)に相当するものが不要。
* 型注釈: 変数や引数などに対する型の指定。いわゆる、`val x: String = 〜`のコロンの後ろの型指定のこと。
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
  * 実装方法については、拡張メソッドを参照。
* Any, AnyRef, AnyVal
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
となるような、一般的な`func2`を定義できる。Scalaでも、`func1`を持つようなオブジェクトを一般的に引き受けるような関数を定義したい。
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
* 評価戦略
* DSL(ドメイン特化言語)
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

## 練習問題的なやつ
* [S-99: Ninety-Nine Scala Problems](http://aperiodic.net/phil/scala/s-99/)

## 読み物
* [関数型言語でのデザイン手法 - togetter](https://togetter.com/li/25283)
* [オブジェクト指向設計の原則と関数型プログラミング](https://www.infoq.com/jp/news/2014/03/oo-functional-programming)
* [Scalaで型レベルプログラミング(日本語訳)](https://github.com/yuroyoro/Japanese_Translations_of_Scala_Articles/tree/master/source/ja/type_level_programming_in_scala)
  * 4章まで(?)(2018/01/27現在)
* [Scalaは関数型プログラミング言語ではない](http://delihiros.hatenablog.jp/entry/2012/05/01/032433)
