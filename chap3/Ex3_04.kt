fun square(n: Int) = n * n

fun triple(n: Int) = n * 3

typealias IntUnaryOp = (Int) -> Int

fun main() {
  // val compose: ((Int) -> Int) -> ((Int) -> Int) -> ((Int) -> Int) = { f -> {g -> { x -> f(g(x)) } } }
  val compose: (IntUnaryOp) -> (IntUnaryOp) -> IntUnaryOp = { f -> { g -> { x -> f(g(x)) }}}
  assert(compose(::square)(::triple)(2) == 36)

  val squareValueFunction: IntUnaryOp = { it * it }
  val tripleValueFunction: IntUnaryOp = { it * 3 }

  assert(compose(squareValueFunction)(tripleValueFunction)(2) == 36)
}
