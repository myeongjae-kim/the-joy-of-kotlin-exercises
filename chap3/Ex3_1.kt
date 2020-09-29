fun main() {
  val square: (Int) -> Int = { it * it }
  val triple: (Int) -> Int = { it * 3 }

  val composed = compose(square, triple)

  val result = composed(2)
  assert(result == 36)
}

fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
  return { f(g(it)) }
}
