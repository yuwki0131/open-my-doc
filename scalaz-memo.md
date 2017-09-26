# scalazメモ

# listmonad、forっぽいやつ

```
scala> List(1,2,3) >> List(1, 2, 3)
List(1,2,3) >> List(1, 2, 3)
res0: List[Int] = List(1, 2, 3, 1, 2, 3, 1, 2, 3)
```

#
