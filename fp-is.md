# Scalaと関数型プログラミングとは まとめ
* Javaエンジニア用Scalaユーザ向け
* そういう傾向があるという話。
* 同じ関数型言語でも結構言語によって慣習が違う。
  (例えば、同じオブジェクト指向でもJavaとObjective-CとSmalltalk、Python、JavaScriptではかなり雰囲気が違うのと同じ)

## なぜ、関数型プログラミングをするのか?

### デザインパターン
の多くが不要になる。

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
  代数的データ型 + パターンマッチを使う。
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
* 再代入を行わない
* データ型では代入は、最初の1回のみ。
* データ構造を破壊しないことで、思考コスト、オブジェクトがどこで書き換えられるかの調査コストが減る。(副作用のないプログラミング)
  * どこでデータが書き換えられるかを考える必要が無くなる。
  * 関数の入力と出力のみに注目すればいい。
    (参照透過性: 引数に同じ値を与えれば同じ戻り値を得ることができる関数を参照透過性な関数という)
  * 入力に対して、想定した出力が得られれば、正しい関数であると言える。(プロパティベースのテスト(関数に対するユニットテスト))
  * (注)別に関数型プログラミングだからというわけではない。Javaなど他の言語でもある程度同じことが可能。
    * 但し、Javaは破壊的な変更に対して保証してくれませんよ。。。。

### その他
* そもそも静的に型付されるかどうかはあんまり関係ないですよ。。。

## ポイント
* 関数型プログラミングはデータ構造と関数を分ける傾向がある。
* Scalaは再帰のサポートが難しい。

## 無名関数(ラムダ抽象)/高階関数
* 大体、無名関数かラムダ式でググったら出てくる。ラムダ式とはあんまり言わない
* 第一級オブジェクトとしての関数(データとして扱う事が出来る関数)
* オブジェクトなのでデータを持たせる事ができる。後述のレキシカルスコープ参照
* map { a => a }の←これ
* 関数を渡す、値として保持することができる。
* C言語の関数のポインタと何が違うのか? / JavaのStrategyパターンと何が違うのか。
  * 関数のポインタと違い、データを保持することができる。
  * JavaのStrategyパターンと違い、インターフェースを必要としない。

## レキシカルスコープ/静的スコープ
この時、aは、
```
{ a => { (a) => a }}
```
* 動的スコープを採用している言語はほとんどない。但し、implicit parameterは動的スコープ的な役割に近い

## 再帰
* あんまり使わないが、たまに使う。
* 木構造のような再帰的なデータ型があると使う。
* リストについても使えるが、後述の通りあまり使わない
* Scalaの再帰は末尾最適化をする場合としない場合がある。
* Trampolineで末尾最適化をする。

## 代数的データ型
* 関数型プログラミングだと実装とデータ型を分離する傾向がある。
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

## 関数合成

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

## やらない関数型言語まわりのトピック
* 継続(continuation)
* プログラムの融合変換(program fusion, deforestation)
* なんとかモルフィズム(正確にはRecursion Schemeという。catamorphism, anamorphism, hylomorphism ...)
* モナド、コモナド(特に普段は意識する必要はない)
* 抽象解釈(abstract interpretation)
* コンビネータロジック、ラムダ計算、圏論
* 証明、定理証明支援系
* プログラム意味論
* 型システム
* 依存型(dependent type) (path-dependent type)

## Dotty
https://qiita.com/kmizu/items/10940b4c46876ae8a12d

## 参考文献
* path-dependent type: https://53ningen.com/path-dependent-types/
* [Scalaに関して知っておくべきたった一つの重要な事](http://kmizu.hatenablog.com/entry/20120504/1336087466)
* [代数的データ型とshapelessのマクロによる型クラスのインスタンスの自動導出](http://xuwei-k.hatenablog.com/entry/20141207/1417940174)
* [Scalaにおける細かい最適化のプラクティス](http://xuwei-k.hatenablog.com/entry/20130709/1373330529)
* [Scala COLLECTIONS 性能特性](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)
* [Scalaでimplicits呼ぶなキャンペーン](http://kmizu.hatenablog.com/entry/2017/05/19/074149)
