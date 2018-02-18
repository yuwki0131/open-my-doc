# scala-slickについて
* Futureで色々やるので大変。

## monadic joinの黄昏
* slickでtableどうしのjoinをする時、join関数を使うと思う。
* しかし、素のコードでJoinしてしまうと大変な事になる。

```scala
row <- AbcTable if row.id === id
```

table1 join table2 on table1.id === table2.id

```scala
abc  <- AbcTable if row.id === id
defg <- DefgTable if defg.id === abc.defgId
```
