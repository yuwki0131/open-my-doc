# scalazメモ

## 資料

https://speakerdeck.com/iwamatsu0430/scalazdeuebuapurimocha-la-head-cha-la

http://xuwei-k.hatenablog.com/entry/20130517/1368814058

http://xuwei-k.github.io/scalaz-docs/

# listmonad、forっぽいやつ

```
scala> List(1,2,3) >> List(1, 2, 3)
List(1,2,3) >> List(1, 2, 3)
res0: List[Int] = List(1, 2, 3, 1, 2, 3, 1, 2, 3)
```

# 無理っぽいやつ
* validation使えそうだが、forは無理

# 使えそうなやつ

# 面白いやつ
* scalaz.Alpha / scalaz.Digit : アルファベットの代数的データ型
* Either3 : Eitherでいいってことか...?
