fun square(n: Int) = n * n

fun triple(n: Int) = n * 3

fun main() {
  val composed = compose(::square, ::triple)

  val result = composed(2)
  assert(result == 36)
}

fun <T, U, V> compose(f: (U) -> V, g: (T) -> U): (T) -> V = { f(g(it)) }
