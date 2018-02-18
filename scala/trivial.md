# Scalaのすごいどうでもいい文法

Twirlテンプレートで既に使ってるかも知れないが、
```scala
scala> 'aaa
res7: Symbol = 'aaa
```

矢印の記法はtupleを表す。ただし、この記法でパターンマッチはできない。
```scala
scala> 'a -> 'b
res2: (Symbol, Symbol) = ('a,'b)
```

パターンマッチは分解する前のオブジェクトを選択する時に@が使える。
```scala
case list @ List(1, _*) => s"$list"
```
https://alvinalexander.com/scala/how-to-use-pattern-matching-scala-match-case-expressions

https://www.scala-lang.org/files/archive/spec/2.11/04-basic-declarations-and-definitions.html#repeated-parameters


ObserveErrors.md
