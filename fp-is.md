# Scalaと関数型プログラミングとは まとめ
* Javaエンジニア用Scalaユーザ向け
* そういう傾向があるという話。
* 同じ関数型言語でも結構言語によって慣習が違う。
  (例えば、同じオブジェクト指向でもJavaとObjective-CとSmalltalk、Python、JavaScriptではかなり雰囲気が違うのと同じ)
* 関数型プログラミング一般の話をするのは難しい。
  https://twitter.com/esumii/status/638588331459153920
* というわけで関数型プログラミングっぽい話をします。

## なぜ、関数型プログラミングをするのか?

### デザインパターン
の多くが不要になる。(語弊のある言い方をすれば)

#### Gofのデザインパターン

* Strategy
  アプリケーションで使用するアルゴリズムやコードを動的に切り替える。
  https://ja.wikipedia.org/wiki/Strategy_%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3
  => Scalaだと切り替える為のインターフェースなどは不要、単に関数を受け渡すだけになる。
  または型クラスを定義する。
* Template Method
  上に同じ
* Factory Method
  部分適用(Partial Application)が使える。
  https://www.ibm.com/developerworks/jp/java/library/j-ft10/
* Visitor
  代数的データ型 + パターンマッチ(+ 再帰)を使う。
* Composite
  上に同じ
* Interpreter
  上に同じ
* Singletonパターン
  classやtraitではなくobjectで定義 or Singletonアノテーション
  この辺は(多分?)Javaと一緒

#### Gofのデザインパターン以外

* Null Object
  オブジェクトはOption型で定義して、部分関数(Partial Function)を使う。(参照)
* Balking
  ガード節とも。これのScala版のいい方法がわからない。あえて言うなら、for-yield式がそれに相当。
* Immutable object
  積極的にmutableにしなければ、オブジェクトは全てimmutable
* Future
  ScalaのFuture

オブジェクト指向プログラミングにおけるデザインパターンのいくつかが不要になる(標準で使えるようになる)

[デザインパターン紹介(Gof以外のデザインパターン)](http://www.hyuki.com/dp/dpinfo.html#Balking)

### immutable
* 「状態」が変わらない
* 再代入を行わない
* データ型では代入は、最初の1回のみ。
* データ構造を破壊しないことで、思考コスト、オブジェクトがどこで書き換えられるかの調査コストが減る。
  * どこでデータが書き換えられるかを考える必要が無くなる。
  * 関数の入力と出力のみに注目すればいい。
    (参照透過性: 引数に同じ値を与えれば同じ戻り値を得ることができる関数を参照透過な関数という)
  * 入力に対して、想定した出力が得られれば、正しい関数であると言える。(プロパティベースのテスト(関数に対するユニットテスト))
  * (注)別に関数型プログラミングだからというわけではない。Javaなど他の言語でもある程度同じことが可能。
    * 但し、Javaは破壊的な変更をしていないことの保証のサポートが弱い(finalを使えばその限りではない)
    * Scalaではimmutableなデータ型やTuple、case classなどを使っている限りでは変更されていないことを保証する仕組みが多数

### 副作用が(他の関数型言語よりも少ない)
* 割と少ないか少なくなるように意識した言語設計(変数の破壊的代入を前提としないような書き方)

## これから説明する内容
プログラム全体の設計の話というよりは細かいテクニカルな話が中心になります。
* replの紹介
* 関数型プログラミングなので、当然関数の使い方をメインに説明します。
    (再帰、無名関数、静的スコープ、高階関数、合成関数、名前渡し)
* 関数型プログラミングで頻繁に用いられるデータ型である代数的データ型
* 業務でよく使われるOption/Either/Future及びfor式について
* Scalaは静的型付言語なので、型の話をします。
* Scalaの余談
* 一番最後に、今回は詳細は話しませんが、関数型プログラミングまわりの技術について紹介します。

## (まずはじめに)replによるローカル実行テスト
TODO: 以下のセクションを書く
* シンタックスの動作確認や型の確認ができる。
* Scalaのスモールサイズの文法を学ぶのにはうってつけ

## 再帰
* 再帰(recursion)とは自分自身を自分自身の中に持つような構造。
  再帰的なデータ構造や、再帰呼出しなど。
  [再帰 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%86%8D%E5%B8%B0)
* Scalaでのループはコレクション関数を使用することが(多分)殆どなので、再帰呼出しは、あまり使わないが、たまに使う。
  (コレクション関数は後述)
* リスト(構造)についても使えるが、コレクション関数を使用すればいい場合が多い(要出典)ので殆ど使わないはず。
* 木構造のような再帰的なデータ型がある場合や分割統治法を使ったアルゴリズムの場合は、使うとわかりやすいコードが書ける。
  * 分割統治法
    大きな問題を小さな部分問題に分割し、個々の小さな部分問題を解決しながら、
    その部分問題の解答結果のマージを繰り返し、最終的に元の問題を解くようなアルゴリズム。
    [分割統治法 - Wikipedia](https://ja.wikipedia.org/wiki/%E5%88%86%E5%89%B2%E7%B5%B1%E6%B2%BB%E6%B3%95)
    以下の例のQuicksortが典型例。
  * Quicksort(あるいは、関数型プログラミングにおける偽のQuicksort)
    (TODO: 偽のQuicksortの例を書く)
  * 木構造のデータ型 + matchによるパターンマッチで再帰を使う例
  (TODO: 木構造による再帰の例を書く)

* 末尾最適化
  * Scalaの再帰はをする場合としない場合がある。
    * 末尾再帰形式になっていない場合は、末尾最適化が行われない。
      (TODO: 末尾再帰でない関数の例)
    * 自分自身を呼び出しのみ、かつ末尾再帰形式になっている場合
      (TODO: 自分自身を呼び出す末尾再帰の例)
    * 相互再帰では末尾最適化をしない。
      (TODO: 相互再帰の例)

* Trampolineで末尾最適化をする。
  (TODO: Trampolineで末尾再帰の例を入れる)
* なので結局、原理主義的に再帰のみでゴリゴリimmutableなコードも書けるが、
  可読性や後でメンテナンスすることを考えるなら、Scalaの場合はwhile文なりfor文を使った方が現実的な場合もあるかも知れない。
* ちなみにFutureで再帰する場合
  (TODO: サンプルを入れる)

## 無名関数(ラムダ抽象)
TODO: 以下のセクションを書く
* 大体、無名関数かラムダ式でググったら出てくる。ラムダ式とはあんまり言わない
* 第一級オブジェクトとしての関数(データとして扱う事が出来る関数)
* オブジェクトなのでデータを持たせる事ができる。後述のレキシカルスコープ参照
* map { a => a }の←これ
* 関数を渡す、値として保持することができる。
* C言語の関数のポインタと何が違うのか? / JavaのStrategyパターンと何が違うのか。
  * 関数のポインタと違い、データを保持することができる。
  * JavaのStrategyパターンと違い、インターフェースを必要としない。

## レキシカルスコープ/静的スコープ
TODO: 以下のセクションを書く
この時、aは、
```
{ a => { (a) => a }}
```
* 動的スコープを採用している言語はほとんどない。但し、implicit parameterは動的スコープ的な役割に近い

## 高階関数

## 関数合成

## コンビネータ

## 部分関数

## 名前渡し

## 代数的データ型
* 関数型プログラミングだと実装とデータ型を分離する傾向がある。(要出典)
  * データに実装が付随しがちなオブジェクト指向プログラミングとは少し違う。。。
* オブジェクトが破壊されないことの保証
  * 一つはval(Javaで言うところのfinalを付ける)によるデータ型
  * またははimmutableな標準型、immutableなhashmap、list、etc...
  * 最後の一つはTupleによるプログラミング、否、case classによるデータ定義
* case classには、`final`を付けることが必須
  なぜ、final case classを付けないと行けないのかは以下を参照。
  https://stackoverflow.com/questions/34561614/should-i-use-the-final-modifier-when-declaring-case-classes

## リスト
* ScalaだとSeqで書くのがマナーらしい
* 標準のArrayListとLinkedListがある。
* 関数型のLinkedList(主に単方向連結リスト)は特殊な性質がある。

* 例えば
  case class AbcRow(id: Long, name: String, abc: String)
  のSeq[AbcRow] からidとnameを抽出する

### コレクションメソッド
* map, filter, foldあたりが王道。flatMap, flatten

    [ScalaのSeqリファレンス](https://qiita.com/f81@github/items/75c616a527cf5c039676)

* 関数型プログラミングではリスト操作関数を多用することが多い。 => slickに繋がる
  * SQLもまた宣言型言語なので、map/filterなどの組み合わせはSQLに変換しやすいのかもしれない。。。

## Future, for式

## 型まわりの話
* 複雑な型を定義してもあんまり意味ないという側面はある。
* 特に普段の業務で使いまくるのかは疑問
* 気を抜いているとAnyに推論されるらしい。
* http://keens.github.io/slide/DOT_dottynitsuiteshirabetemita/

### 暗黙の型変換(implicit conversion)

### 拡張メソッド(implicit class / 既存の型を拡張する)

### implicitな型パラメータ
* 社内だとFutureが

### 構造的部分型

### 型クラス

## 余談
### Scalaにおける小括弧の()と中括弧の{}違い
小括弧は式を、中括弧はブロックを表す。
* [Scalaにおける括弧()と中括弧{}の違い](http://xuwei-k.hatenablog.com/entry/20130221/1361466879)

### private[X]
アクセス修飾子priavte[X]は、パッケージX以下まで参照可能。
* [【Scala】 アクセス修飾子と限定子 - takafumi blog](http://takafumi-s.hatenablog.com/entry/2015/07/31/152642)

### Dotty
Scalaの新しいコンパイラ。
* [Dottyによる変更点と使い方 - 水底](https://qiita.com/kmizu/items/10940b4c46876ae8a12d)

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
* 依存型(dependent type) (dependent type/path-dependent type/dependent method type/dependent object type)

## 参考文献
* path-dependent type: https://53ningen.com/path-dependent-types/
* [Scalaに関して知っておくべきたった一つの重要な事](http://kmizu.hatenablog.com/entry/20120504/1336087466)
* [代数的データ型とshapelessのマクロによる型クラスのインスタンスの自動導出](http://xuwei-k.hatenablog.com/entry/20141207/1417940174)
* [Scalaにおける細かい最適化のプラクティス](http://xuwei-k.hatenablog.com/entry/20130709/1373330529)
* [Scala COLLECTIONS 性能特性](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)
* [Scalaでimplicits呼ぶなキャンペーン](http://kmizu.hatenablog.com/entry/2017/05/19/074149)
