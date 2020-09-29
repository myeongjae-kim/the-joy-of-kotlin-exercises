fun square(n:  Int) = n * n

fun triple(n: Int) = n * 3

fun main() {
  val composed = compose(::square, ::triple)

  val result = composed(2)
  assert(result == 36)
}

fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
  return { f(g(it)) }
}
