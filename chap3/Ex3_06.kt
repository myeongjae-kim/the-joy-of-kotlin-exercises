fun <T,U,V> higherCompose(): ((U) -> V) -> ((T) -> U) -> (T) -> V = { f -> { g -> { x -> f(g(x)) }}}

fun <T,U,V> composeAndThen(): ((T) -> U) -> ((U) -> V) -> (T) -> V = { f -> { g -> {x -> g(f(x)) }}}

fun main() {
  val square: (Int) -> Int = { it * it }
  val triple: (Int) -> Int = { it * 3 }
  assert(higherCompose<Int,Int,Int>()(square)(triple)(2) == 36)
  assert(composeAndThen<Int,Int,Int>()(square)(triple)(2) == 12)

  val intToDouble: (Int) -> Double = { it * 1.0 }
  val doubleToString: (Double) -> String = { it.toString() }
  assert(higherCompose<Int,Double,String>()(doubleToString)(intToDouble)(1) == "1.0")
  assert(composeAndThen<Int,Double,String>()(intToDouble)(doubleToString)(1) == "1.0")
}
