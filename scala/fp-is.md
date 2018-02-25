# Scalaと関数型プログラミングとは まとめ

2018/02/11版

## このドキュメントについて
* Scalaの関数型プログラミング的な側面についてまとめた。
  * 関数の使い方(概念、用語、テクニック): 再帰、無名関数、静的スコープ、クロージャ、高階関数、合成関数
  * 頻繁に用いられるデータ型である代数的データ型の概念と使い方: 代数的データ型、パターンマッチ
* 業務でよく使われるOption/Either/Exception、及びfor式について: 例外の使い方、for式
* 静的型付き言語なので型まわりの機能: Scalaの3つのimplicit、Scalaの型、型クラス
* Scalaについて調べていたら発見した余談。
* 内容が重複するため、以下に書いてある事に関しては基本的に記載していない。
  * [Scala研修テキスト - dwango on GitHub](https://dwango.github.io/scala_text/)
* その他、関数型言語あるあると定番のネタについて記載している。

**注意: 言語の性質を説明するために、普段のプログラミングでは使ってはいけない(使うとコードが複雑になる)テクニックも紹介しています。
どの機能を使うべきか、内容をみて適宜判断してください。**

## 関数型プログラミング言語の用語
* **束縛(bind/binding)**: 変数へ代入することをこう呼ぶ習慣がある。
  * 「変数aに値を束縛する」という言い方をする(数理論理学の用語に由来。
    * [自由変数と束縛変数 - Wikipedia](https://ja.wikipedia.org/wiki/%E8%87%AA%E7%94%B1%E5%A4%89%E6%95%B0%E3%81%A8%E6%9D%9F%E7%B8%9B%E5%A4%89%E6%95%B0)
  * 特に、ローカルで定義され代入された変数は束縛変数、グローバル変数などローカルで定義されていない変数の事を自由変数と言ったりする。
    (後述するレキシカルスコープ参照)
  * 人によって「代入する」という人と「束縛する」という言い方をする人に別れる(Scalaだと「代入」派が多い印象)。
* **評価(evaluate)**: プログラムの実行、特にプログラム中の部分的な式を実行すること。

* (関数型プログラミング関係ない)オブジェクト指向用語: レシーバ
  * オブジェクト指向において、メッセージを受け取るオブジェクトの事をレシーバという。
  * あるオブジェクトのメソッドを呼び出す時、そのオブジェクトに対しメッセージ送るとみなす為、レシーバと呼ばれる。
    * [オブジェクト指向プログラミング - Wikipedia](https://ja.wikipedia.org/wiki/%E3%82%AA%E3%83%96%E3%82%B8%E3%82%A7%E3%82%AF%E3%83%88%E6%8C%87%E5%90%91%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0)
  * `value.methodA()`の`value`の事。

## 再帰(Recursion)
* 再帰とは自分自身を自分自身の中に持つような構造。
  * [再帰 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%86%8D%E5%B8%B0)
  * 関数型プログラミングでは、再帰的なデータ型と(関数の)再帰呼出しがよく使われる。

### 再帰呼出し
* 呼び出し元の関数が自分自身を呼び出すこと。
* Scalaでの再帰呼出し。
次のような階乗を行う関数の場合。(10の階乗(fact(10))は、10! = 1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10)
```scala
def fact1(n: Int): Int = if (n < 1){
  1
} else {
  n * fact1(n - 1)
}
```
* Scalaでのループはコレクション関数を使用が(多分)殆どなので、再帰呼出しはあまり使わないが、稀に使う。
* 再帰呼出しは(一般に)関数型プログラミングとの相性が良い。
  * 変数に再代入しないため、変数を常にimmutableにしたままループが書ける。
  * 再帰的なデータ型に対して、関数の再帰呼出しは相性が良い。(再帰的なデータ型を定義し、再帰呼出しで再帰的にトラバースできる)
  * プログラムを帰納的(余帰納的)に定義できる。
* 木構造のような再帰的なデータ型や、分割統治法を使ったアルゴリズムの場合は、使うとわかりやすいコードが書ける。
  * 分割統治法
    * 大きな問題を小さな部分問題に分割し、個々の小さな部分問題を解決しながら、
      その部分問題の解答結果のマージを繰り返し、最終的に元の問題を解くようなアルゴリズム。
    * [分割統治法 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%88%86%E5%89%B2%E7%B5%B1%E6%B2%BB%E6%B3%95)
    * 次のクイックソートが典型例。
  * クイックソート(あるいは、関数型プログラミングにおける偽のクイックソート)
    * Javaでクイックソート:
      [【Java】クイックソートのアルゴリズムのテスト - Qiita](https://qiita.com/gigegige/items/4817c27314a2393eb02d)
    * Scalaで関数型プログラミングのスタイルのクイックソート
```scala
def quicksort[A](ls: Seq[A])(implicit ord: Ordering[A]): Seq[A] = ls match {
    case Nil => Nil
    case a::as => quicksort(as.filter(ord.lt(_, a))) ++ Seq(a) ++ quicksort(as.filter(ord.gteq(_, a)))
}
```
これは普通の関数同様に実行する。
```scala
scala> quicksort(List(5, 6, 7, 4, 3, 10, 2, 8, 0, 3))
res38: Seq[Int] = List(0, 2, 3, 3, 4, 5, 6, 7, 8, 10)
```
* 上記は偽のクイックソート(理由はググッて下さい)。

### 木構造のデータ型 + matchによるパターンマッチで再帰を使う例
木構造で、木の末尾を`Leaf`、枝を`Node`としてcase classで定義する。
```scala
sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
```
木に対する走査の処理。
具体的にどのように走査した結果を得るかは、抽象化する。`f: (A, A) => A`の箇所。
```scala
def sum[A](t: Tree[A], f: (A, A) => A): A = t match {
  case Leaf(a) => a
  case Node(l, r) => f(sum(l, f), sum(r, f))
}
```
以下のように使用する。
```scala
scala> val d = Node(Node(Leaf(10), Leaf(20)), Leaf(1))
d: Node[Int] = Node(Node(Leaf(10),Leaf(20)),Leaf(1))

scala> sum(d, (a:Int, b:Int) => a + b)
res36: Int = 31
```
以下は、String型で文字列を返す例。
```scala
scala> val e = Node(Node(Leaf("a"), Leaf("b")), Leaf("c"))
e: Node[String] = Node(Node(Leaf(a),Leaf(b)),Leaf(c))

scala> sum(e, (a: String, b: String) => "(" ++ a ++ " " ++ b ++ ")")
res44: String = ((a b) c)
```

### 末尾最適化(Tail call optimization)
* 関数型言語で行われるコンパイラの処理の一種。
* 末尾再帰形式は関数呼び出し時に、stackを必要としないような再帰呼出しの形式。
  * 自分自身を再帰的に呼び出す際にその呼び出し以外の処理が残っていない形で自分自身を呼び出すような関数呼び出し形。
* 再帰呼出しは、コンパイル時に「末尾再帰」形式になっている場合、コンパイル時に(whileのような)スタックを消費しないループに変換される。
  * ループに変換されない場合、通常の関数呼び出しの連鎖となり、stackを消費してしまい、StackOVerFlowErrorになってしまう。
  * ループ回数が多い場合(JVMの場合、1000〜数千回以上)の場合は、末尾再帰形式でループを記述しないと、StackOverFlowになる。
    * 個人的な経験則だと1000回未満のループだと、SOFにはならない事が多い。(勿論、実装による部分が大きい)
  * 大量にループを繰り返す場合は末尾再帰が必須となる。
  * stackを消費しすぎないタイプのループの場合は、この限りではない。(関数呼び出しで消費したStackが戻される場合など。)
* Scalaの再帰は末尾最適化をする場合としない(できない)場合。
  * 末尾再帰形式になっていない場合は、末尾最適化が行われない。

次のような階乗を行う関数の場合。(10の階乗(fact(10))は、10! = 1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10)
```scala
def fact1(n: Int): Int = if (n < 1){
  1
} else {
  n * fact1(n - 1)
}
```
次の例は、自分自身を呼び出しのみ、かつ末尾再帰形式になっている場合。
```scala
def fact2(n: Int, a: Int): Int = if (n < 1){
  a
} else {
  fact2(n - 1, n * a)
}
```
計算結果を保持する変数(アキュームレータ)を一つ追加している。
末尾再帰に書き直す場合は、計算結果を保持する引数を用意し、そこに副作用の役割を担わせることも多い。
これは「変化するmutableな変数」を表している。(引数で副作用(再代入)を引き回すスタイル)

以下のコードでは`fact2(10, 1)`の計算しか行っていないが、例えば、`fact1(10000)`と`fact2(10000, 1)`では、
fact1の場合、StackOverFlowになってしまうが、fact2ではコンパイラの最適化(末尾再帰最適化)によりStackOverFlowにならない。
```scala
scala> fact2(10, 1)
res39: Int = 3628800
```
* 末尾再帰では基本的に綺麗なプログラムを書こうと考えない事がポイント(末尾再帰の時点で大して綺麗に書けてない)。
  * whileループを無理矢理、再帰に書き換えるような勢いが大切。
  * 内容によっては既に末尾再帰形式の関数になっているような関数もある。
* Scalaでは相互再帰は末尾最適化をしない。

例えば以下のようなプログラム、
```scala
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
この形式はfact2と同様に、末尾に関数呼び出し以外の処理は残っていないが、末尾最適化されない。
* 相互再帰や複雑な再帰呼出しは、Trampolineという末尾最適化と同様のスタックを消費しない書き方がある。
  * [スタックレスScala](http://halcat.org/scala/stackless/index.html)
* 原理主義的に再帰のみでゴリゴリimmutableなコードも書けるが、
  * そもそもScalaなら可能な限りコレクションを使ってループを書くべき。
  * 可読性や後でメンテナンスすることを考えるなら、Scalaの場合はwhile文やfor文が現実的な選択肢かも知れない。
    * 関数を呼び出す側から見た時に、参照透過な関数になっていればOKという考え方もアリとみなす。
* 結論としては、関数型プログラミングでのループは再帰やfor/whileのような構文ではなく、
  mapやfilterといったコレクション関数を使うのが一般的。
  * 大半のループはコレクション関数(のメソッドチェーン)で記述した方が、可読性が高く、メンテナンスもしやすい簡潔なコードが書ける。
  * コレクション関数に関しては後述。

## 無名関数(ラムダ抽象)
```scala
scala> val f = ((a: Int) => a + 1)
f: Int => Int = $$Lambda$4017/598382925@72f0dff7

scala> f(1)
res19: Int = 2

scala> f(2)
res20: Int = 3
```
* おなじみの無名関数。大体、無名関数かラムダ式でググったら出てくる。ラムダ式とはあんまり言わない(方がいい)(※個人の意見です)。
* 第一級オブジェクトとしての関数(データとして扱う事が出来る関数): 関数を渡す、値として保持できる。
* 無名関数の式が評価されると、**関数オブジェクト**が生成される。
  * 関数オブジェクトは、コンストラクタによって生成される他のJavaオブジェクトと同様に扱えるオブジェクト
    (型となるクラスを持ち、インスタンスとして扱われるように)なる。
* オブジェクトなのでデータを持たせる事もできる。(後述のクロージャを参照)
* C言語の関数のポインタと何が違うのか? / JavaのStrategyパターンと何が違うのか。
  * 関数のポインタと違い、データ(値)を保持する。
  * JavaのStrategyパターンと違い、インターフェースを必要としない。
* map/filter/reduce(fold)関数や、その他様々な高階関数に渡す時によく使用する。(後述の高階関数を参照)

## レキシカルスコープ(Lexical scope, 静的スコープ)
* "Scope"とは範囲のこと。"Lexical"とはLiterallyくらいの意味で深い意味はない。
* レキシカルスコープとは、静的に、ある変数を参照した時、どのタイミングで代入された値が参照されるかが決まるスコープのこと。
* Scalaの変数(valや引数)の有効範囲は、レキシカルスコーピングによって決定される。
* 変数の値を取り出す時、(Scalaの)レキシカルスコープでは、ネストした変数定義において最も内側で定義された変数を参照する。

以下の2つのコード。
```scala
scala> {val a = 1; ((a:Int) => ((a:Int) => a)(3))(2) }
res5: Int = 3
```
または、
```scala
scala> {val a = 1; {val a = 2; {val a = 3; a} } }
res11: Int = 3
```
この時、aは、最も内側にあるaは、ネストしている中で最も手前で定義された変数(引数)aの値を参照する。
次の例では、最も内側のスコープを抜けているので上記とは別の値を見に行く。
```scala
scala> {val a = 1; ((a:Int) => {((a:Int) => a)(3); a})(2) }
res10: Int = 2
```
または、
```scala
scala> {val a = 1; {val a = 2; {val a = 3;} a} }
res14: Int = 2
```
* レキシカルスコープ自体は、モダンな言語では一般的な仕組み。
  * Scalaだけでなく、JavaScriptやTypeScript(たぶん)、Ruby(たぶん)、Python、Groovy(たぶん)でも共通。
    * 但し、JavaScriptのvarはFunctionスコープと呼ばれるスコープ(レキシカルスコープとは若干違う)なので注意が必要。
      (文法によってスコープのとり方が異なる)
  * 今とはなっては、(静的スコープではない)動的スコープを持つ言語の方が珍しい。動的スコープを採用している言語はほとんどない。
    * 現代で実用的な動的スコープがメインの言語はEmacs Lispくらい。。。
    * 但し、implicit parameterは動的スコープ的な役割に近い。
      implicit parameterは、社内だとFutureのExecutionContextがよく使われる。

## クロージャ(Closure)
* 自由変数を含む(使用した)関数によって生成された関数オブジェクトの事をクロージャ(Closure)という。
  * 自由変数: 関数定義中にその関数のブロック内で定義されていない変数のこと。
  * クロージャは、自由変数に束縛された値(の参照)をデータとして保持する。
  * ブロックから抜けだした関数オブジェクトは、レキシカルスコーピングによって保持した変数の参照を保持し続ける。
* プログラミング言語のClojureの事ではない。
* レキシカルスコープ同様、モダンな言語では関数定義や無名関数からクロージャを生成できることが一般的。

以下では、関数オブジェクトがaseqという変数名が保持しているSeqの参照を持つ。
```scala
scala> val haveSeq = {val aseq = Seq(1, 2, 3, 4, 5); ((index: Int) => aseq(index)) }
haveSeq: Int => Int = $$Lambda$4005/1227571506@23364fcf

scala> haveSeq(1)
res8: Int = 2

scala> haveSeq(2)
res9: Int = 3
```
一番外側で定義された変数haveSeqは、ブロック内部で生成された関数オブジェクト(クロージャ)を束縛している。
この関数オブジェクトに引数を与える事で、aseqに束縛されたSeqの値にアクセスできる。

* 上記のような書き方により、JavaやScalaで指定するprivateよりも更に細かいスコープ(変数の有効範囲)の制御が可能になる。

次のように2つの関数からのみ参照可能なHashmapを定義できる。
```scala
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
* 勿論、ListやHashmapだけでなく、関数を保持する関数を作ったり、関数を保持する関数を保持する関数など色々作れる。
* ただし、関数オブジェクトを濫用し続けると、不用意に意図しないクロージャを生成してしまう事も考えられる。
  このような場合、GCによって回収されない参照をいつまでも保持し続けることになってしまう。
  (とは言え、普通に書いている限りだとこのようなバグは殆ど無いかも知れない)
* クロージャにより変数はそのスコープの外を出ても有効である場合がある。
  つまり、ローカル変数は、変数を定義した関数本体が終了しても生き残る。
  * 変数が生存している(有効である)期間のことを**エクステント(extent)**という。
* クロージャとは逆に、関数内に自由変数を含まないような関数のことを**コンビネータ(combinator)**という。
  ただし、Scalaだと、パーサコンビネータ以外ではコンビネータという言い方はあまりされない(みたい)。
* クロージャを使うことで計算を遅延(将来に実行)させられる。典型的な使用例がFutureによるコールバック。
  * 遅延させたい計算は常に無名関数のシンタックスで囲うことで遅延させる事が出来る。
`dao.findById(id)`がFutureを返す時、mapに渡された`row =>`以降は、Futureの結果が返ってくるまで実行されない。
```scala
dao.findById(id).map { row => row.name }
```
クロージャとは直接関係ないが、無名関数の性質を応用することで**if関数**のようなものも作ることが出来る。
つまり関数呼び出し時に引数が実行されない形の書き方ができる。
```scala
scala> def ifFunction(b: Boolean, f: () => Unit, g: () => Unit) = if (b) f() else g()
ifFunction: (b: Boolean, f: () => Unit, g: () => Unit)Unit

scala> ifFunction(true, { () => println("a"); }, { () => println("b"); })
a

scala> ifFunction(false, { () => println("a"); }, { () => println("b"); })
b
```
無名関数で囲わない書き方の場合、次のようになる。
```scala
scala> def ifFunction(b: Boolean, f: Unit, g: Unit) = if (b) f else g
ifFunction: (b: Boolean, f: Unit, g: Unit)Unit

scala> ifFunction(false, { println("a"); }, { println("b"); })
a
b
```
関数呼び出しは、引数を評価した後、その結果が関数に渡されて関数自体が実行される。
そのため、通常の呼び出しだと、引数が評価されてしまうが、引数を関数化することで、
引数の評価を遅延させる(実質的には実行させない)事ができる。

* クロージャのみでリスト構造(データ構造)を作ることが可能。
  * ルール: データ構造やクラスは使わない。
  * 関数型プログラミングにおける大道芸の一つ。

単方向連結リスト(いわゆるLinkedList)の実装。
```scala
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
* 関数オブジェクトは値として他の関数に渡したり、関数を受け取る、変数に束縛するなど、Javaオブジェクトのような扱いが可能。
* 関数を引数としたり、戻り値として使用する関数の事を高階関数という。
  * 関数に関数を渡す時は、無名関数として渡す(mapなど)こともできるし、定義された名前付きの関数を渡すこともできる。
    * 特に、無名関数を渡すパターンは、コレクション関数を使用する時によく使う。

無名関数を他の関数に渡す。
```scala
scala> Seq(1, 2, 3).map(i => i + 2)
res0: Seq[Int] = List(3, 4, 5)
```
定義済みの他の関数に渡す。
```scala
scala> def add2(i: Int): Int = i + 2
add2: (i: Int)Int

scala> Seq(1, 2, 3).map(add2)
res4: Seq[Int] = List(3, 4, 5)
```
* 高階関数を使用することで、ほぼ無制限にプログラム中の任意のロジック(具体的なコード)を抽象化できる。
  * ちなみに、データ型や関数の定義などは抽象化できない。。。
  * 抽象化したい部分を関数化し、差分のみをそれぞれ別関数にして、抽象化した関数に差分の関数を渡すという方法。
  * コード全体の至る所で使用できて、DRYに書けるというメリットはあるが。
    やり過ぎると原型を留めなくなるので注意が必要。
* 部分適用/カリー化(Partial application / Curring)
  * 複数の引数を取る関数を一つの引数のみを取る関数に書き換えることをカリー化という。
  * 途中まで値を代入して、途中からの値を別の高階関数の中で代入させたい時などに使用する。

以下がカリー化の例。上はカリー化されていない関数。下が上の関数をカリー化した関数。
```scala
scala> def sum(a: Int, b: Int, c: Int): Int = a + b + c
sum: (a: Int, b: Int, c: Int)Int

scala> def sum(a: Int)(b: Int)(c: Int): Int = a + b + c
sum: (a: Int)(b: Int)(c: Int)Int
```
次のようにして、途中までの値を入れておく。
```scala
scala> sum(1)(2) _
res1: Int => Int = $$Lambda$3054/194743804@57e5ed15
```
そして、次のように使うことができる。
```scala
scala> Seq(1, 2, 3).map(sum(1)(2))
res3: Seq[Int] = List(4, 5, 6)
```
* 途中まで引数の値を適用し別の無名関数を生成する書き方を部分適用という。
  * 部分適用とカリー化は間違えやすいことで有名。
    * [カリー化 != 部分適用](http://kmizu.hatenablog.com/entry/20091216/1260969166)

## 関数合成
* 2つの関数を合成する。数学の合成関数と考え方は同じ。: f(g(x)) = (f . g)(x)
* 合成関数用の関数として、compose/andThenが用意されている。
* ちなみに、Scalaだとcompose使いたいケースでは、メソッドチェーンで書く事が多いのであんまり使わない。

次の場合、
```scala
def withComma(ls: Seq[String]) = ls.mkString(",")
def trimString(ls: Seq[String]) = ls.map(_.trim.toInt)
def multiply20(ls: Seq[Int]) = ls.map (_ * 20).map(_.toString)
```
StingのSeqの要素を20倍してカンマ区切りの文字列にしたい。。。
```scala
val ls = Seq(" 20", " 30 ", "40 ", "50")
```
しかもなんか変な空白入ってる。。。
```scala
scala> withComma(multiply20(trimString(ls)))
res31: String = 400,600,800,1000
```
括弧が多い。。。
```scala
scala> (withComma compose multiply20 compose trimString)(ls)
res32: String = 400,600,800,1000
```
さらにこれを関数化したい。。。
```scala
scala> val multiply20withComma = withComma compose multiply20 compose trimString
multiply20withComma: Seq[String] => String = scala.Function1$$Lambda$605/1525241607@1f18de49

scala> multiply20withComma(ls)
res33: String = 400,600,800,1000
```
引数を定義して関数化すると、変数が増えて冗長になるので、関数を合成の場合は、`compose`のみで組み合わせを表現し関数を定義できる。
composeだとわかりにくい? そんな時の為にandThenという関数が用意されている。

* composeとandThenは順序が逆。
composeの場合
```scala
scala> (((s:String) => s.toInt) compose ((x:Int)=> x.toString))(20)
res24: Int = 20
```
andThenの場合
```scala
scala> (((x:Int)=> x.toString) andThen ((s:String) => s.toInt))(20)
res22: Int = 20
```
andThenを使って前述のコードを書き直す。
```scala
scala> val multiply20withComma = trimString andThen multiply20 andThen withComma
multiply20withComma: Seq[String] => String = scala.Function1$$Lambda$4021/295193997@7d9759a

scala> multiply20withComma(ls)
res34: String = 400,600,800,1000
```
処理の順番が明確になり、分かりやすくなった。

* ライブラリやフレームワークなどのコードで時々登場する。
  * 使いすぎると分かりづらくなる事も多いが、Scalazだと頻繁に使われていたりする。
  * Playframeworkのアクション合成(action composition)などでも類似の概念が登場する。
  * ただし、Scalaの場合、メソッドチェーンで書くほうが一般的なようなので、そこまで使わないかも知れない。
* 上記のような変数の登場しない合成による関数定義の仕方を**ポイントフリースタイル(point-free style)**という。

## 部分関数(Partial function)
* ある特定の引数に対してのみ値を返す関数。
* [ScalaのPartialFunctionが便利ですよ](http://yuroyoro.hatenablog.com/entry/20100705/1278328898)

## 代数的データ型(Algebraic data type)
* 端的に言うと、以下のようなデータ定義の仕方。
```scala
sealed trait Alphabet
case object Alpha extends Alphabet
case class Beta(name: String) extends Alphabet
case class Gamma(name: String, n: Int) extends Alphabet
```
* 引数の戻り値型や処理結果のパターンや、Seq型で表現できないようなリスト構造、木構造、
  その他必要に応じてポリモーフィックに変化するデータ型を表す際に使用する。
* ざっくり説明すると、代数的データ型とは、直積型による構造体と、コンストラクタと直和型によるポリモーフィズムを表した型。
  * Scala風に言うと、複数のcase objectとcase classを単一のtraitでまとめたもの。
  * 全てを使う必要はない。必要に応じて必要なオブジェクトを使う。
* 代数的データ型は、コンストラクタと、直積型、直和型から構成される。
  * 代数的("Algebraic")という単語は、直和型と直積型に由来している。
    [Algebraic data type - HaskellWiki](https://wiki.haskell.org/Algebraic_data_type)
* 代数的データ型を返す関数の型は継承元のtraitで、条件に応じて、様々なtraitの型のインスタンスを返す。
```scala
def func(n: Int): Alphabet =
  if (n < 10) Alpha
  else if (n < 30) Beta("aaa")
  else Gamma("bbb", n)
```
* 以下のようにパターンマッチで各データパターンごとに分解できる。
```scala
xxx.func(x) match {
  case Alpha => 〜 ...
  case Beta(name) => 〜 (この中では変数nameが使える) ...
  case Gamma(name, n) => 〜 ...
}
```
* 普段よく使っている代数的データ型の一つがOption型
  * [scala/src/library/scala/Option.scala](https://github.com/scala/scala/blob/2.13.x/src/library/scala/Option.scala)
```scala
sealed abstract class Option[+A] extends Product with Serializable
case object None extends Option[Nothing]
final case class Some[+A](value: A) extends Option[A]
```
* 割と他の関数型言語だと定番の書き方。
  * 参考: [代数的データ型とパターンマッチによる言語比較](https://qiita.com/xmeta/items/91dfb24fa87c3a9f5993)
  * (余談)代数的データ型を一般化したGADT(Generalized Algebraic Data Type)というのもある。
* 関数型プログラミングだと実装とデータ型を分離する傾向がある。(要出典)
  * 分離されたデータ型が代数的データ型。
  * データ型(クラス)に実装が付随しているオブジェクト指向とは異なる。
* 代数的データ型は、再帰的(帰納的)に定義される有限のデータ構造(Streamなど無限のデータ構造というのもある)
  * 参照: [具象不変コレクションクラス | Scala Documentation](http://docs.scala-lang.org/ja/overviews/collections/concrete-immutable-collection-classes.html)
* (余談)一応、case classには、`final`を付けた方がいい: [Should I use the final modifier when declaring case classes? - StackOverFlow](https://stackoverflow.com/questions/34561614/should-i-use-the-final-modifier-when-declaring-case-classes)

## パターンマッチ
* [Scalaのパターンマッチ - Qiita](https://qiita.com/techno-tanoC/items/3dd3ed63d161c53f2d89)
* Scalaでは、リテラル(定数)、正規表現、構造(タプル、case objectやcase classなど)、代数的データ型に関するマッチなどが可能。
* データ型のインスタンス(ListやTuple、Case classなど)を構造的に分解して変数に代入できる。
  * オブジェクトにunapplyが定義されていれば、パターンマッチが可能。
  * [パターンマッチをもっと便利に-extractor(抽出子)による拡張](http://yuroyoro.hatenablog.com/entry/20100709/1278657400)
* if-else式とは違い、データ型に対する網羅的にパターンマッチを行う。網羅的でない場合は警告がでる。(但し、エラーにはならない。)
  **パターン漏れが防げるので積極的に活用していきたい。**
* データ型に対する分岐か、それ以外かでif-elseとの使い分けができる。

### Listに対するパターンマッチ
次のコードはパターンマッチで書き換えた方が、ロジックがシンプルになる。
```scala
if (ls.isEmpty) 1 else ls.head
```
は、リスト`ls`についてのパターンマッチ、
```scala
ls match {
p  case Nil => 1
  case x::xs => x
}
```
と書き直せる。else節でls.headを使う時に、lsを手前の条件節でチェックしているかどうかを考慮する必要が無くなる。

### Optionに対するパターンマッチ
次のようなケースも、リスト同様に書き直せる。
```scala
if (x.isEmpty) "hogehoge" else x.get.toString
```
存在するSome/Noneパターンを列挙する。
```scala
x match {
  case Some(x) => x.toString
  case None => "hogehoge"
}
```
Either(Right/Left)やTry(Success/Failure)の場合も同様にして、分解できる。

### 代数的データ型に対するパターンマッチ
以下のような代数的データ型に対するパターンマッチ。
```scala
sealed trait Alphabet
case object Alpha extends Alphabet
case class Beta(name: String) extends Alphabet
case class Gamma(name: String, n: Int) extends Alphabet
```
次のように書く。書き方はOption等と同じ。
```scala
x match {
  case Alpha => "A"
  case Beta(name) => "B(" + name + ")"
  case Gamma(name, n) => "C(" + name + ")"
}
```

### パターンマッチに条件を追加
* パターンマッチの場合、構造のチェックと同時に、データ型から値の取り出し、変数への束縛まで行う。
* ただし、以下のような書き方をした場合、パターンマッチの本来の意味はなくなる(ただのif-else式と基本的に同じ意味しかなくなる)。
```scala
x match {
 case _ if x.isEmpty => "hogehoge"
 case _ => x.get.toString
}
```
* パターンマッチでのifによる条件追加は以下のようなケースだと有効。
  パターンマッチの基本的な機能である構造的な変数の束縛、パターンの列挙(とそのチェック)の両方を使用しているため。
```scala
x match {
 case Some(x) if x == "a" => "hogehoge"
 case Some(x) => "fugafuga"
 case None => "piyopiyo"
}
```
* 1caseに複数パターンのマッチも可能。
```scala
n match {
  case 1 | 2 | 3 => "hogehoge"
  case _ => "hoge"
```

### 代数的データ型とパターンマッチ
* Javaのポリモーフィズムでは、処理が各クラスごと分散してしまうというデメリットがある。
* 代数的データ型とパターンマッチでは、データ型はデータ型ごとに定義し、ポリモーフィックな処理はパターンマッチで一箇所に記述させる。

[Compositeパターン - Wikipedia](https://ja.wikipedia.org/wiki/Composite_%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3) から引用してきた例(長かったのでコードの一部を改変している)。
```java
interface FileInterface {
	public void ls(int depth);
	public boolean add(FileInterface c);
}
class File implements FileInterface {
	private String name;
	public File(String name) { this.name = name; }
	public void ls(int depth) {
		System.out.println("depth(" + depth + ") file:" + this.name);
	}
	public boolean add(FileInterface c) { return false; }
}
class Folder implements FileInterface {
	private String name;
	private List<FileInterface> fileList = new ArrayList<FileInterface>();
	public Folder(String name) { this.name = name; }
	public void ls(int depth) {
		System.out.println("depth(" + depth + ") folder:" + name);
		for (FileInterface file : fileList) { file.ls(depth + 1); }
	}
	public boolean add(FileInterface c) { return this.fileList.add(c); }
}
```
上記は代数的データ型とパターンマッチで書き直せる。
代数的データ型は以下のように定義できる。
```scala
sealed trait FileInterface
case class File(name: String) extends FileInterface
case class Folder(name: String, f: scala.collection.mutable.ListBuffer[FileInterface]) extends FileInterface
```
次のようにデータを作る。
```scala
scala> val prj = Folder("crud-prj", ListBuffer(File("README.md"), File("build.sbt"), Folder("src", ListBuffer(File("Helloworld.scala"), File("XXXDao.scala"), File("ExampleController.scala")))))
```
ディレクトリ階層を表示する関数。
```scala
def ls(files: FileInterface, depth: Int): Unit = files match {
  case File(name) => println("depth(" + depth + ") file:" + name)
  case Folder(name, children) => {
    println("depth(" + depth + ") folder:" + name)
    children.foreach{ child => ls(child, depth + 1) }
  }
}
```
ディレクトリに要素を追加する関数。
```scala
def add(files: FileInterface, element: FileInterface): Boolean = files match {
  case File(name) => false
  case Folder(name, children) => { children += element; true }
}
```
Javaと同様の関数を書くならば、上記の書き方で問題ない。ところで、上記の関数は木構造をmutableに変更してしまう。
一般に関数型プログラミングは副作用(データの破壊的な変更)を回避するため、その点において、上記の書き方は問題がある。
そこで、immutableなadd関数を容易する必要がある。
immutableな関数を容易し、rootから書き換える。例えば、次のような関数が考えられる。
```scala
def addI(files: FileInterface, elem: FileInterface, path: Seq[String]): FileInterface = (files, path) match {
  case (Folder(name, children), Nil) =>
    Folder(name, children :+ elem)
  case (Folder(name, children), x::xs) =>
    Folder(name, children.map {
        case f @ Folder(name, c) if name == x => addI(f, elem, xs)
        case a => a
    } )
  case _ => throw new RuntimeException("ファイルに要素を追加しようとしたのでエラー。")
}
```
同様にcase classもimmutableにする
```scala
sealed trait FileInterface
case class File(name: String) extends FileInterface
case class Folder(name: String, f: Seq[FileInterface]) extends FileInterface
```
この関数をrootに対して適用する。次のようなコード。
```scala
val root =
Folder("project", Seq(Folder("app", Seq(Folder("controller", Seq(File("HomeController.scala"),
                                                                 File("ContentController.scala"))),
                                        Folder("view",       Seq(File("hello.scala.html"))))),
                      File("README.md")))
```
次のように実行する。
```scala
scala> addI(root, File("AnotherController.scala"), Seq("app", "controller"))
res24: FileInterface =
Folder(project, List(Folder(app, List(Folder(controller,List(File(HomeController.scala),
                                                             File(ContentController.scala),
                                                             File(AnotherController.scala))),
                                      Folder(view, List(File(hello.scala.html))))),
                     File(README.md)))
```
* (注意):ただし、よりScalaらしく実装するなら、
  File/Folderのようなデータ構造は、Traversableトレイトの具象クラスとして実装するほうが多分正解。
  * [Traversable トレイト | Scala Documentation](https://docs.scala-lang.org/ja/overviews/collections/trait-traversable.html)

## リスト構造
* **関数型プログラミングといえばリスト**(要出典)
  * リストに何でもデータを保存したがる言語もあるくらい頻繁に用いられるほか、
    大抵の関数型プログラミング言語にはリストを操作するための関数群が大量に用意されている事が多い。
  * 関数型プログラミングにおける単方向連結リストは重要な役割を持っている。
    (が、Scalaの場合、色々調べてはみたものの、そんなに使われている様子はない)
    * 再帰(やプログラムの証明)やimmutableなデータ型と、単方向連結リストは相性がいい。
* 関数型プログラミングでよく使われる単方向連結リスト。
  * consとnil(もしくはempty listなど)によって構成される。
  * consは先頭の要素に対する参照を一つ持ち、後続のリストに対する参照を持つ。
    * 後続のリストが存在しない場合は、空リストを参照する。
  * cons(1, cons(2, cons(3, nil)))のようにリストを構成する。
* Scalaのリスト
  * Traversable <- Seq <- IndexedSeq, LinearSeqの順で継承されている。
    * Scalaの場合、IndexedSeqの方がパフォーマンス上、好ましい場合もありうるため、リストを使う時はSeqの方が一般的に使われる。
  * コレクションのヒエラルキー: [MUTABLE AND IMMUTABLE COLLECTIONS | Scala Documentation](https://docs.scala-lang.org/overviews/collections/overview.html)
* 関数型言語でのループは主に、リストとリスト操作関数の組み合わせで記述する。
  * 大抵のループ処理はリスト系の関数の組み合わせだけで書けてしまう事が殆ど。(要出典)
  * Javaだとfor構文で書くべき所は、リスト操作関数の組み合わせになる。
```scala
for (int i = 0; i < n; i++){
  〜
}
```
と書いていた箇所は、

```scala
(0 to n).foreach { i =>
  〜
}
```
と書ける。この時、`(0 to n)`は、0からnまでの数値が入ったリスト。
* 代表的なリスト操作関数
  * map: 元のリストの各要素を別のデータ構造に移し替える(入れ替える)場合や、リストの各要素を個々に処理する場合などに使える。
  * filter: 元のリストの各要素のうち、特定の要素だけを抜き出す。
  * fold: リストの各要素を集計する/まとめるような処理を書く場合に使用する合計値を出す場合。
  * collect: これは一般的な関数型言語にある関数ではないが、Scalaだとmapとfilterを組み合わせる場合に使える。
  * その他、sort、reverse、sum、min/max、take(先頭からn個取り出す)、zip(複数のリストの各要素をペアにする)など色々あるため、
    "scala seq"などでググると色々出てくる。
  * ループで複雑な処理をしたい場合は、色々調べてみると、大抵の場合、丁度いい感じの関数が見つかることが多い。
* 昔流行った(?)、MapReduceは上記のmap関数とreduce(foldとほぼ同様の)関数に由来している。
* mapやfilter、foldで綺麗に書けない場合は、Scalaのリストのパターンマッチと再帰で書くやり方もある。(quicksortを参照)
* [ScalaのSeqリファレンス - Qiita](https://qiita.com/f81@github/items/75c616a527cf5c039676)
* 関数型プログラミングではリスト操作関数を多用される。この考え方をSQLに持ち込もうと考えるとSlickに繋がる(多分)。
  * SQLもまた宣言型言語なので、map/filterなどの組み合わせはSQLに変換しやすいのかもしれない。
* 頭の中で抽象的なリストの形を変形させていくプロセスをメソッドチェーンのコードに落とし込む。
* Listと同様にSetやHashmap、その他データ型でも明示的なループを書かずに組み合わせでコードを記述出来るような
  コレクション関数が多数用意されている筈なので、随時調べた方がいい。
  * 関数型言語にはデータ型に対する抽象化された関数がライブラリに大量に用意されているということがよくある。
    * これの極端な例がScalaz。

## 例外(Option, Either, Exception)

### Option - nullableを型レベルで表現する。
* Option型では値が入っている時に、`Some(値)`、値がない時に`None`で表現する。
* [Optional(2018)年あけましておめでとうございます](https://moneyforward.com/engineers_blog/2018/01/05/optional2018/)
  * まさに、2018年はOption元年と言った感じがある。(上記の言語はSwift)
  * Option外し忘れには注意。
    Scalaの場合は、`Some(2018)年あけましておめでとうございます`になる。
* Optionは値がnullableな場合に使用する。未定義処理や、想定外の値を返す時にnullを返していたようなケース。
* 基本的にはnull(or empty)チェック専用の構文だと思っている。(※個人の意見です。)
* メリット
  * データが存在しない場合に、意味のないマジックナンバーや空文字、空オブジェクトを埋め込む必要がなくなる。
  * Java製のライブラリなどでnullableが怪しいやつはとりあえずOptionで囲っておくと、nullはNoneになる。
```scala
scala> Option(null)
res6: Option[Null] = None
```
* head, getは使わない。
  * headOption, getOptionでnullableとなるようなケースは代わりの処理を用意する。
  * headやgetで値が存在しない場合、例外が投げられるため。
  * 値が存在しないケースというのは、通常、想定範囲内のケースであるため、nullやempty時に例外が投げられるのは好ましくない。
* Option型の使い方が色々: [Scala best practice: How to use the Option/Some/None pattern](https://alvinalexander.com/scala/best-practice-option-some-none-pattern-scala-idioms)
* catchingやallCatchで任意の例外クラスをOptionやEitherにラップしてくれる関数も用意されている。
  * 詳細は次の記事を参照: [Scalaでの例外処理 - Either,Option,util.control.Exception](http://yuroyoro.hatenablog.com/entry/20100719/1279519961)
```scala
scala> catching(classOf[NumberFormatException]) opt "foo".toInt
res7: Option[Int] = None
```

### Either - エラーを戻り値で表現する。
```scala
scala> def f(n: Int): Either[String, Int] = if (n < 0) Left("wrong n value") else Right(n)
f: (n: Int)Either[String,Int]
```
* 正常/エラーを戻り値で表す。
  * 正常系の値が入っている時(正常に処理が終了した時)にRightでラップ。: `Right(値)`
  * 異常系の値が入っている、または、エラーメッセージ等の場合にLeftでラップ。 : `Left(異常な値やエラーメッセージなど)`
* Either型で値を返す事で、戻り値からその後続の処理において、正常系、エラー系、どちらの処理をすればいいのか、
  型で表現でき、パターンマッチで対応できる。
  * PlayframeworkだとActionFunction(アクション合成)などで使用されている。
* Right/Leftで表現しきれなくなった場合、3パターンの結果が返ってくる場合などは、代数的データ型で独自の型を定義した方がよさそう。
* (余談)Scalaでは、デフォルトで`Either[A, B]`を`A Either B`と書くことができる。2つの型パラメータを持つ場合、常に書けるらしい。
```scala
scala> val x: Int Either String = Left(1)
x: Either[Int,String] = Left(1)
```

### 例外(Exception)
* Javaと違い、Scala非チェック例外。
  * 非チェック例外: 関数にExceptionクラスを列挙する必要が無くなるが、関数呼び出し時にはどの例外が返ってくるか分からなくなる。
  * 非チェック例外なので、呼び出し元(を書く人)は呼び出し先が例外を投げてくるのか、
    その場合はどのような挙動にすべきか、考慮しなくなりがち。
* try-catchでキャッチする場合は、NonFatalでキャッチする。
  * NonFatalはパターンマッチで例外をキャッチする時に、致命的なエラーでないエラーのみをキャッチする。
  * [Scala 2.10.0 Try ＆ NonFatal](http://d.hatena.ne.jp/Kazuhira/20130124/1359036747)
* 例外の扱い方色々: [scala.util.control.Exception._を使ったサンプル集](http://seratch.hatenablog.jp/entry/20111126/1322309305)
* 例外を投げるとその関数は全域関数でなくなる。いわゆる純粋な関数でなくなる。
  * ※全ての引数のパターンに対して戻り値が定まっている関数のことを全域関数という。
    例外以外にも、特定の値を引数として渡した時に無限ループになるような関数も全域関数ではない。
* Eitherとかとの使い分け。(※以下、個人の主観です。)
  * 他言語だと、Haskellは純粋な関数ではMaybe(Scalaで言う所のOption)、Eitherを使うようだが、
    それ以外のそこまでこだわらない言語だと割とフランクに投げるイメージがある。
  * Java風に簡単なチェックやバリデーションにまで、例外を投げられると辛い。(いちいちcatchしないといけなくなるので)
  * 全てを放棄して、フレームワークに処理を任せる場合のみ例外を投げた方がいい気がする。
    * 例外を投げる時は、finally系の処理がない事が前提。
  * リカバリーの処理が必須なら、Option/Eitherで返される方が明示的になる。
    * Future系のrecover関数とOption/Eitherどちらが書きやすさで選ぶ?
    * 特にユーザの入力系はExceptionよりもOption/Eitherの方がその後の処理が継続しやすい。
    * 例外による余計なジャンプが無くなるため、例外をcatchし損ねる事がなくなり、finally漏れによるバグが無くなる。
      * 呼び出し元の関数 → 例外を想定していない関数 → 例外創出を前提とした関数の組み合わせで呼び出しが発生した時、
        例外を想定していない関数内の処理で問題が発生するリスクが常に存在する。
* [エラー処理 - dwango on GitHub](https://dwango.github.io/scala_text/error-handling.html)
* 非同期プログラミング時(Futureを使っている場合など)に、例外の挙動はさらに複雑になる。
  * onCompleteやrecover(recoverFrom)などの記述がないと、**例外は基本的に握りつぶされる**。
  * Futureの周りをtry-catchで囲っても意味はない。try-catchを抜けた後でFutureが別スレッドで実行される。

ダメな例。
```scala
try {
  Future { throw new RuntimeException("未来のエラー") }
} catch {
  case e => println("未来のエラーを事前に防ぎました！")
}
```
* Scalaで例外を投げまくるコードを書く時は常に注意が必要。

## for式/for内包記法(For comprehension)
```scala
for {
  a <- abcDao.find(id)
  b <- abcDao.find(a)
} yeild f(a, b)
```
* Scalaのfor文はJavaなどと同様に、foreach文の役割を持つ。
* ただし、yield節を追加することで、for-yield式(for内包記法)となり、map/flatMap/filter(With)を使ったジェネレータとなる。
  * 基本的にはListのジェネレータを他のデータ型(OptionやFuture)向けに一般化したものとして考えるのが筋。
* map/flatMap/withFilter等が定義されたデータ型に対して、これらの関数を使用したコードの別の書き方を提供する。
  * よくTwitterなどでモナドがほしいという人がいるが、実は本当に求めているのは、モナドそのものではなく、
    このfor-yield風の構文の事だったりする(らしい)。。。
* FutureやOption、Eitherなどでは、後続の処理を記述するために、オブジェクトの末尾にmap/flatMapを使用する。
  しかし、このような書き方は、ネストが深くなると、可読性が落ち、括弧の対応関係を追いづらくなる。
  また、どの結果がどの変数に代入されているかも読み取ることが難しくなる。
* for式を導入することで、変数束縛の対応関係が明確になり、また、括弧の数が減少し、処理の流れが明確になる。

例えば、このネスト。
```scala
abcDao.find(id) // 戻り値はFuture[Option[String]]
  .flatMap {
    case a => a.map(f)  // aはOption[String]
      .map {
        case b => abcDao.findByName(b) // 戻り値は、Future[Option[String]]
          .map { c =>　(a, c) } }.getOrElse( ... ) }
```
for式で書き換えることで各関係が明確になる。
```scala
for {
  a <- abcDao.find(id)
  b = a.map(f)
  c <- abcDao.findByName(b)
} yield (a, c)
```
* for式は、コンテクストとなる型(コンストラクタ)は必ず一つしか持てない。
  * Future用のfor式は、Future型専用、Option型のfor式は、Option型専用になる。
  * Scalazのモナド変換子だと複数のコンテクストを合成した(複数のコンテクストを持つ)新たなのコンテクストを作ることもできるが。。。
* yieldの手前のfor式内で使える記法は、主に3つ。そして最後にyield節がくる。
  * flatMap: `a <- b`
    : for式と同じコンテクストの型(コンストラクタ)を持つ式(`b`)があり、その結果がfor式内でアンラップされた変数`a`に代入される。
  * map: `a = b`
    : for式が表しているコンテクストとは無関係な型を持つ`b`を変数`a`に代入する。
  * filter(With): `if exp`
    : ガード節。filterとも言う。`exp`がfalseだった場合、後続の処理を実行しない。(FutureだとFailureとなる)
    * [Scala Future with filter in for comprehension - StackOverFlow](https://stackoverflow.com/questions/17869624/scala-future-with-filter-in-for-comprehension)
  * yield(map)
    : yield節の式の結果をfor式の型(コンストラクタ)でラップした結果を返す。
* 関連したテクニック
  * 式の実行結果が不要な場合は、次のようにアンダースコアを使う事で、余計な変数を避けることが出来る。

```scala
for {
  _ <- abcDao.find(id)
} yield ...
```
_を使うことで余計な束縛を回避している。

  * for式中の=形式の束縛は、for式内でfor式のコンテクストとは無関係な処理を実行できる。

次の書き方、
```scala
for {
  〜
  b　= methodA(a)
} yield ...
```
は、Futureがコンテクストの場合、以下の書き方と同じ。
```scala
for {
  〜
  b　<- Future { methodA(a) }
} yield ...
```
flatMapがmapになっていることが分かる。

  * 複数のコンテクストを持つようなfor式(例えば、Future[Option[_]]をコンテクストに持つ)は、
    生Scalaでは無理だが、flatMapなど型クラスを充実させることで実現可能。
    * この類の実装をしているのはScalaz。
    * 複数のコンテクストを組み合わせて新しいコンテクストを作ることも可能。
      (モナドトランスフォーマーでググると出てくる)
* for式はネストしたmap/flatMap/fiterWith(filter)に変換される。
  * このため、mapが複雑にネストするケースやflatMapやfilterを多用するコードは一旦、for式の使用を検討したほうがいい。
    * プログラムがネストしすぎるのは可読性の観点から好ましくないため。
  * 一般的には、map/flatMapなどのネストよりはコードが読みやすくなる(はず)。
  * [For Comprehensions and For Loops](http://scala-lang.org/files/archive/spec/2.12/06-expressions.html)

for式がある時、
```scala
for {
  a <- abcDao.find(id)
  b <- abcDao.find(a)
} yeild f(a, b)
```
コンパイル時に次のように、map/flatMapに展開される。
```scala
abcDao.find(id)
  .flatMap {
    case a => abcDao.find(a)
      .map { case b => f(a, b) } }
```
* (その他)Try型を使うことで例外もfor-yield形式で書ける。
  * [Scalaでの例外 - SlideShare](https://www.slideshare.net/TakashiKawachi/scala-16023052)

## Scalaの3つのimplicit
* [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)
* implicit conversion, implicit class, implicit parameterがある。
* implicit: 暗黙の〜
* implicit修飾子を付けて定義した場合、コンパイラが適宜、必要なメソッドや型を探索して自動的に適用してくれる。

### 暗黙の型変換(implicit conversion)
* 型変換(キャスト)する関数をimplicitに定義しておくことで自動的にキャストしてくれる。

暗黙の型変換の定義。
```scala
implicit def d2i(d: Double):Int = d.toInt
```
implicit定義前。
```scala
scala> val x:Int = 3.14
<console>:11: error: type mismatch;
 found   : Double(3.14)
 required: Int
       val x:Int = 3.14
                   ^
```
implicit定義後。
```scala
scala> val x:Int = 3.14
x: Int = 3
```
* 暗黙の型変換は推奨されていない/しない人が多い。
  * 公式のドキュメントですら、"implicit conversions can have pitfalls"と書かれている。
    * [TOUR OF SCALA IMPLICIT CONVERSIONS | Scala Documentation](https://docs.scala-lang.org/tour/implicit-conversions.html)
  * implicit conversionに対する否定的なコメント。
    * [Scalaのimplicit conversionってなんだ？](http://blog.livedoor.jp/sylc/archives/1553449.html)
    * [Scalaでimplicits呼ぶなキャンペーン](http://kmizu.hatenablog.com/entry/2017/05/19/074149)
    * [Scala implicit修飾子 まとめ - Qiita](https://qiita.com/tagia0212/items/f70cf68e89e4367fcf2e)

### 拡張メソッド(implicit class / 既存の型を拡張する)
* 既存の型を拡張し、メソッドを追加する。(拡張メソッドについてはドワンゴの研修資料を参照)
  * pimp my libraryパターンと言われる。
  * ちなみに、C#やTypeScriptにも同名の類似した機能がある。
* レシーバにメソッドを生やす事ができるのが特徴。

String型に、空文字ならNone、文字があればSomeで値を包む関数を定義したい場合、以下のように拡張できる。
```scala
implicit class OptionString(str: String){
  def opt(): Option[String] = if (str.isEmpty) None else Some(str)
}
```
これは、次のように使える。
```scala
scala> "".opt
res2: Option[String] = None

scala> "abc".opt
res3: Option[String] = Some(abc)
```

### 暗黙のパラメータ(implicit parameter)
* 暗黙に受け渡しされる引数。
* implicit修飾子により定義された変数を暗黙的にimplicit修飾子が付けられた引数に代入する。
* 単に暗黙的に引数を補完するだけだが、暗黙のパラメータを応用することで型クラスを実装することができる。(型クラス参照)

次のコードでは、二番目の引数をimplicitとしている。
```scala
scala> implicit val x = 20
x: Int = 20

scala> def f(a: Int)(implicit b: Int):Int = a + b
f: (a: Int)(implicit b: Int)Int

scala> f(1)
res4: Int = 21
```
* コンパイラが自動的に暗黙のパラメータを認識し、必要としている時に自動的にimplicitパラメータを探索する。
* (多分)静的型付言語に動的スコープ(静的スコープの記述を参照)を導入したような物(?)
  * Implicit Parameters: Dynamic Scoping with Static Typesというタイトルの論文がある。
  * 関数を実行する場所やその瞬間によって値をコロコロ変えることが出来る。
  * 暗黙のパラメータ自体は、Haskellの型クラスをエミュレートするために実装されたらしい。
* 引数の数が大量にある、または、同じような引数を取る関数を大量に呼び出している時などに有効と思われる。
* 割とホントに見えない所で代入が発生しているので、普段意識せずに使ってる人が多そうな機能の一つ。
  * 多分いちばん使われているのはFuture。
    Futureは本来、ExecutionContextを引数に取るが、ec自体が各クラスでimplicitに宣言されているため、
    implicitに代入されている。(試しにExecutionContextのimplicit修飾子を外してみると分かりやすいはず)
* 暗黙のパラメータに関するエラー
  * 暗黙のパラメータを必要としているにもかかわらず、必要となるimplicitな変数が定義されていない場合はエラーが出る。
    * あるクラスの関数を別のクラスの関数に移した時(ControllerクラスのコードをServiceクラスに移した時)などに、
      この手のエラーがよく発生する。
    * 移植元のimplicitな変数と移植先のimplicitの変数の何が違うかを考えると見つけやすい。エラーメッセージもヒントになる。
  * 暗黙のパラメータによる複数の代入候補が存在する場合にもエラーが発生する。(一応優先順位はあるらしいが)
* 暗黙のパラメータによる代入を許したくない場合は、明示的に引数を指定することでそれを回避できる。
前述の例で言うと、次のように書けば、暗黙のパラメータは回避される。
```scala
scala> f(1)(30)
res5: Int = 31
```
* implicit parameterを使用することで、型クラスを構成できる。(←ここが重要)(後述の型クラス参照)

## Scalaと型
* Scalaの型推論は漸進的型付と呼ばれ、基本的に前から推論していく。
  * (余談)HaskellやOCamlの型推論は、Hindley-Minler(の派生)と呼ばれる推論方式。
    この方法は、最も一般的な型を自動的に導出していく手法で、通常の場合、いわゆる型注釈(型ヒント)に相当するものが不要。
* 型注釈: 変数や引数などに対する型の指定。いわゆる、`val x: String = 〜`のコロンの後ろの型指定のこと。
* 型パラメータ: Javaで言う所のジェネリクス。
  * `trait A[B] { def b():B; }`の`B`
  * 型パラメータで指定できる共変、反変、非変については、[型パラメータと変位指定 - dwango on GitHub](https://dwango.github.io/scala_text/type-parameter.html) を参照。以下、自分用のメモ。
    * 共変(`[+B]`): A extends Bの時のみ、val a:G[B] = b:G[A]が可。
    * 反変(`[-B]`): A extends Bの時のみ、val a:G[A] = b:G[B]が可。
    * 非変(`[B]`): A = Bの時のみ、val a:G[A] = b:G[B]が可。
    * 上界(`[B <: A]`): BがAを継承の性質。
    * 下界(`[B >: A]`): BがAのスーパークラスである性質。
  * 型パラメータに様々な制約を付ける事で、クラス、インターフェースなしにジェネリックな関数を定義できる。(構造的部分型を参照)
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
  * 但し、Javaで言う所のinterfaceとは違い、後付で実装でき、拡張に対して開かれている。
    * [型クラスに関するここ数日の議論 - togetter](https://togetter.com/li/1113557)
  * implicit parameterに関する分かりやすい説明と実装方法は、
    * [implicit - dwango on GitHub](https://dwango.github.io/scala_text/implicit.html)
  * 大雑把に言うと。
    1. 振る舞いをまとめたtrait Aを作る。
    2. traitの実装を作る。この時、implicitに定義する。
    3. Aで使っている関数を使ったコードBを実装する。
    4. 以降、Bに型ごとに機能を追加したい場合は、trait Aの実装を追加することで、コードBの機能が様々な型で使えるようになる。
    * しかし、上記のような言い方をすると結局Javaのインターフェースと一緒じゃんって言われる。。。
  * Scalazに型クラスを使用したコードが大量に載っている。
  * 参考文献
    * [Scala の implicit parameter は型クラスの一種とはどういうことなのか](http://nekogata.hatenablog.com/entry/2014/06/30/062342)
    * [Type Classes as Objects and Implicits](http://ropas.snu.ac.kr/~bruno/papers/TypeClasses.pdf)
* Any型
  * Anyは、全ての型の親クラス。JavaのObject型に相当。
  * [Scala Any](http://www.ne.jp/asahi/hishidama/home/tech/scala/any.html)
  * 気を抜いているとAnyに推論される。
```scala
scala> val x = if (2 < 1) 1 else "a"
x: Any = a
```

### 構造的部分型(Structural subtyping)
* 特定の性質を持った型を型パラメータとして定義(宣言)できる。
  * 部分的な型を定義するという意味では、traitに近いが、オブジェクトを使用する側でのみ必要な型を定義するという点が異なる。
    (traitはあくまでクラスの性質を定義するものだが、構造的部分型は引数や変数に代入可能な一般的なオブジェクトの型を定義する。)
  * 動的型付け言語におけるDuck-typingの性質を静的型付言語で使用したい場合に使用できる。
    * Duck-typing: もしもそれがアヒルのように歩き、アヒルのように鳴くのなら、それはアヒルである。
    * [ダック・タイピング - Wikipedia](https://ja.wikipedia.org/wiki/%E3%83%80%E3%83%83%E3%82%AF%E3%83%BB%E3%82%BF%E3%82%A4%E3%83%94%E3%83%B3%E3%82%B0)
  * 引数や変数などに必要な最も一般的な型を必要な箇所で定義することで、静的に型付のままDuck-typing的な性質を実現する。
* 動的型付け言語(Ruby, Pythonなど)は、名前でメソッドを引っ張ってくる。
```python
class A:
    def func1(self, i):
        〜

class B:
    def func1(self, i):
        〜

def func2(objX):
  objX.func1(1)
  〜

func2(A())
func2(B())
```
となるような、一般的な`func2`を定義できる。Scalaでも、`func1`を持つようなオブジェクトを一般的に引き受けるような関数を定義したい。
勿論、class Aやclass Bの定義を変更することなしに。そして、関数`func1`を持つオブジェクトは知らされることなく常に増えていく。。。
もちろん、(Javaの)interfaceや(Scalaの)traitだとfunc2に与えられるオブジェクトのクラス全てにinterfaceを付けなければいけない。

例えば、idとnameを持つようなRow型について考える。
```scala
case class AbcRow(id: Long, name: String, paramA: String, paramB: String)
case class DefRow(id: Long, name: String, idA: Int, flagB: Boolean)
case class GhiRow(id: Long, name: String, id: Long)
case class JklRow(id: Long, name: String, messageA: String)
```
このRowのリスト(Seq[AbcRow]やSeq[DefRow]のようなオブジェクト)から、
idとnameのタプルのリストを抽出する一般的な関数を定義したい。。。
この場合、次のような関数を定義できる。
```scala
def getIdName[R <: {val id: Long; val name: String;}](rows: Seq[R]): Seq[(Long, String)] =
    rows.map { row => (row.id, row.name) }
```
これは次のようにも書ける。
```scala
def getIdName(rows: Seq[{val id: Long; val name: String;}]): Seq[(Long, String)] =
    rows.map { row => (row.id, row.name) }
```
Long型のidとString型のnameを持つ最も一般的な型を引数にもち、その型のインスタンスからidとnameを抽出する関数を定義できる。
他の型(クラス)定義を書き換えること無く、アドホックに静的な型チェックを維持した関数を定義できる。
(Duck-typingの性質を引き継げる。)
次のように実行する。
```scala
scala> val row = Seq(AbcRow(1, "a", "paramA", "paramB"), AbcRow(2, "b", "paramA", "paramB"), AbcRow(3, "c", "paramA", "paramB"));
row: Seq[AbcRow] = List(AbcRow(1,a,paramA,paramB), AbcRow(2,b,paramA,paramB), AbcRow(3,c,paramA,paramB))
scala> getIdName(row)
res3: Seq[(Long, String)] = List((1,a), (2,b), (3,c))
```
* 条件を満たす型(データ型)を定義しておき、テンプレートを書く。
* (余談)この辺の型の推論をOCamlだと自動でやってくれる。。。Scalaは割と自分で書かないといけないという面倒くささはある。
* 上記以外だとローンパターン(ローンパターン自体は、構造的部分型の特殊な利用パターンの一種)などで頻繁に使うことができる。

### Scalaの3つのdependent * type
* [Dependent Types in Scala](http://wheaties.github.io/Presentations/Scala-Dep-Types/dependent-types.html#/)
* Scalaのdependent type。(普通、関数型言語で言われる所の依存型とは違う)
* path-dependent type
  * 生成された経路によって、同じpackageの同一オブジェクト(クラス)の型の場合でも、別々の型とみなされる。
  * [What is meant by Scala's path-dependent types? - StackOverFlow](https://stackoverflow.com/questions/2693067/what-is-meant-by-scalas-path-dependent-types)
* dependent method type
  * [Scala dependent method types ? - Gist](https://gist.github.com/xuwei-k/1306328/82530a4d2451b68a17f7c03448d6ab88da0bc575)
* dependent object type
  * [Dependent Object Types (DOT)](https://github.com/namin/dot)
    * "The DOT calculus proposes a new type-theoretic foundation for languages like Scala."
  * Dotty向けの型システム。Scala3以降の話なので今回は言及しない。

## その他関数型プログラミングでよくやる習慣みたいなもの

### 雑なローカル変数
* 割とローカル変数の名前の付け方は雑
  * 関数名だと、f, g, h, ....
  * インデックスだと、i, j, k, m, n, ...
  * リストは、ls, lst, xs, ys, zs, ...
    * 特に、リストの先頭をx、後続のリストをxsなどという書き方をする習慣がある。
    * 例えば、`case x::xs => ...` のような書き方をよくする。
  * 型パラメータは、大文字始まりで、A, B, C, ...

## 余談

### Symbol
* ScalaにもRubyと同じようなSymbolがある。先頭にquoteを付ける。
  * PlayのTwirlテンプレートでタグの属性を設定する時などによく使われている。
```scala
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
* [SCALA CHEATSHEET SCALACHEAT | Scala Documentation](https://docs.scala-lang.org/cheatsheets/index.html)

### 用語集
* [Glossary | Scala Documentation](https://docs.scala-lang.org/glossary/index.html)

## 関数型言語まわりのトピック
上記で触れなかったとっピック。

* 名前渡し(Call-by-name)、評価戦略/遅延評価
  * プログラムの実行順序のこと。
  * Scalaでも例えばstream型だと、通常のリストとは異なる挙動を行う。
* プロパティベースのテスト
  * 特定のケースのみを対象としたテストケースではなく、関数の引数に対する戻り値の性質をテストする。
  * 固有のテストケースを持たないと言う意味で従来の関数に対するユニットテストとは大きく異なる。(詳細は調べて！)
* DSL(ドメイン特化言語(いわゆるデザインパターンの一種?)。OOPだけでなく関数型プログラミングでも割と使う)
* Future(※あんまり関数型プログラミング関係無い気もする)
* 継続/限定継続(continuation)
  * プログラムの実行時のある瞬間をとらえたオブジェクト。Javaなどの例外やC言語のsetjmp/longjmpを一般化したもの。
  * 例外は呼び出し元に戻るタイプのジャンプであるのに対して、継続(の実行)は、呼び出し先に戻ることができる。
    * 例外が積み上げたコールスタックを減らして呼び出し元の場所(catch節)に戻るのに対し、
      継続は(必要に応じて)コールスタックを積み上げ(たり書き換えたりし)て元の場所に戻る事ができる。
  * ただし、最近は(コンパイラやインタプリタの実装以外では)殆ど使われない。どちらかというと、プログラミング言語処理系の話題。
  * 他の言語だと、Scchemeのcall/cc、Haskellの継続モナド、Rubyのなんか実装できてしまった継続が有名。
  * 限定継続は継続のうち、継続の範囲(継続により移動できる範囲)を狭めた物。
* プログラムの融合変換/プログラム運算(program fusion, deforestation, program caliculation)
  * 関数型言語のプログラムは大抵式で表されるので、式を等式変形するような方法で別のプログラムに書き換える事ができる。
  * Scalaだと、例えば、xxxlist.map(f).map(g)とxxxlist.map(f compose g)は同じ意味を持つ。
* Recursion Scheme
  * fold/unfold系関数の一般化。
  * catamorphism, anamorphism, hylomorphism ...など名前が付いている。
  * Recursion Schemeに基づくプログラムの融合変換などもある。
  * ScalaだとMatryoshkaというライブラリが有名。
* ファンクタ、アプリカティブファンクタ、モナド、コモナド
  * 特に公式の用語集(以下)にも出てこないので、Scalaでは特に普段は意識する必要はない(と思われる)。
    * [Glossary | Scala Documentation](https://docs.scala-lang.org/glossary/)
  * モナドはモナド則と呼ばれる関数の合成規則を持った型クラスの事。よく副作用を表現する時に使われる。(勿論、副作用以外も表すことができる)
    * モナド同様、ファンクタはファンクタ則を、アプリカティブファンクタはアプリカティブファンクタ則がある型クラスのこと。
    * ファンクタ、アプリカティブファンクタ、モナドの順に制約(性質的な縛り)が強くなる。
    * コモナドはその双対(圏論の用語なので詳しく知りたい場合はその辺りを参照)
  * 単に副作用を表現するだけでなく、自分でカスタマイズできる事から愛好者も多い(と思われる。特にHaskeller)。
  * ScalaだとScalazというライブラリでよく使われる。
* モノイド(foldまわりでよく使われる二項演算の一般化)
  * モナド同様、ScalaだとScalazにモノイドの定義がある。
* 抽象解釈(abstract interpretation、)
  * プログラムの抽象的な実行のこと。静的型付けと同じ、静的解析の一種。
  * 似たようなものとしてSymbolic Execution(記号的実行)というのもある。
  * Scalaだと、[Jadom](https://github.com/jandom-devel/Jandom) というのがあるらしいが。。。
* コンビネータロジック、ラムダ計算、圏論
  * 関数型プログラミングの基礎理論となった概念。
  * 特に無名関数はラムダ計算に、モナドや代数的データ型は圏論に由来する。
* 証明、定理証明支援系
* プログラム意味論
  * 文字通りプログラムの意味の表現。雑に言うと、プログラムを数学的なオブジェクトで表すこと。
  * 実用的なプログラミングではまず使わない。
* 型システム
  * この辺はどちらかというと研究分野に近いが、型推論のための推論規則などがよく研究されている。
* 依存型(dependent type)
  * 一般的なプログラムの型付けより詳細な型を付けることが出来る。例えば0以上のInt型など。
  * ScalaだとStainlessというライブラリがある。

## 参考文献
* [関数合成のススメ 〜 オブジェクト指向プログラマへ捧げる関数型言語への導入その1](http://yuroyoro.hatenablog.com/entry/20120203/1328248662)
* [Scalaの経路依存型（path-dependent type）とは？](https://53ningen.com/path-dependent-types/)
* [Scala2.10.0のDependent method typesと型クラスを組み合わせた『The Magnet Pattern』がヤバい件](http://yuroyoro.hatenablog.com/entry/2013/01/23/192244)
* [Scalaに関して知っておくべきたった一つの重要な事](http://kmizu.hatenablog.com/entry/20120504/1336087466)
* [代数的データ型とshapelessのマクロによる型クラスのインスタンスの自動導出](http://xuwei-k.hatenablog.com/entry/20141207/1417940174)
* [Scalaにおける細かい最適化のプラクティス](http://xuwei-k.hatenablog.com/entry/20130709/1373330529)
* [Scala COLLECTIONS 性能特性 | Scala Documentation](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)

## 練習問題
* [S-99: Ninety-Nine Scala Problems](http://aperiodic.net/phil/scala/s-99/)

## 読み物
* [関数型言語でのデザイン手法 - togetter](https://togetter.com/li/25283)
* [オブジェクト指向設計の原則と関数型プログラミング](https://www.infoq.com/jp/news/2014/03/oo-functional-programming)
* [Scalaで型レベルプログラミング(日本語訳)](https://github.com/yuroyoro/Japanese_Translations_of_Scala_Articles/tree/master/source/ja/type_level_programming_in_scala)
  * 4章まで(?)(2018/01/27現在)
* [Scalaは関数型プログラミング言語ではない](http://delihiros.hatenablog.jp/entry/2012/05/01/032433)
