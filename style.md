# Scala コーディングスタイルガイドまとめ
* アクセッサ・ミューテータ
* 括弧
* 演算子
* 高階型
* 型エイリアス
  type Alpha = (String, String)
  type String3 = (String, String, String)
* 表現の簡潔さに関する特記事項
* 型推論/変数名に型を付けるかどうか
* "Void"メソッド
* みなし型
* 構造的部分型
* 宣言 / クラス
* 関数値
* スペース
* クラス要素の順序
* 波括弧
* 内包表記
* ささいな条件文

## その他コーディング時の注意点
* Futureで
* Slickでは、OnComplete or recoverを必ず書く。

https://qiita.com/kenfdev/items/139d170d5918c971e18b#implicits
* Throwableじゃなくて NonFatalにすべき

## 上記以外に 追加した方が良さそうなコーディングルール

## 複雑な機能を使用しない
http://postd.cc/5-years-of-scala-and-counting-debunking-some-myths-about-the-language-and-its-environment/

# コーディングスタイルの自動フォーマット
## [Scalastyle - Scala style checker](http://www.scalastyle.org/)
* ただのフォーマットチェッカなので、手で作業しないといけない。

## Scalariform
* sbt compile(or run)で自動的にフォーマットしてくれる
* 編集 -> build(sbt run) -> その後編集の時に、ファイルが書き換えられているという注意点はある。
* (注意点)初回導入時は大幅な変更が入る。

* IntelliJ/Eclipseで保存する際にコードフォーマットする
  社内的にはIntelliJ/Eclipse/その他エディタなど各開発者別で分かれていて、現状難しい。
  むしろ、全員でIntelliJを使ってしまうという方法もある。

## 参考文献
* [scalaの自動コードフォーマットをするために試した3つの方法](https://qiita.com/kimutyam/items/1c0b9afdbe6686087251)
* [チームのコードを均一化したい！Scalariformで自動的に美しくコードを作る方法](https://codeiq.jp/magazine/2014/03/6695/)
