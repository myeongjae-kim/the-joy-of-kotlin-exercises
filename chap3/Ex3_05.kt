// 빡대가리가 된 기분이었다... C언어 배울때 2차원 포인터 배열 이해하는 기분. 그러나 해냈다.
fun <T,U,V> compose(f: (U) -> V): ((T) -> U) -> (T) -> V = { g -> { x -> f(g(x)) }}

// Just a wrapper to create a value function with type parameters.
// Only classes, interfaces, and functions declared with `fun` can defined type parameters.
fun <T,U,V> createComposeValueFunction(): ((U) -> V) -> ((T) -> U) -> (T) -> V = { f -> { g -> { x -> f(g(x)) }}}

fun main() {
  val square: (Int) -> Int = { it * it }
  val triple: (Int) -> Int = { it * 3 }
  assert(compose<Int,Int,Int>(square)(triple)(2) == 36)
  assert(createComposeValueFunction<Int,Int,Int>()(square)(triple)(2) == 36)

  val intToDouble: (Int) -> Double = { it * 1.0 }
  val doubleToString: (Double) -> String = { it.toString() }
  assert(compose<Int,Double,String>(doubleToString)(intToDouble)(1) == "1.0")
  assert(createComposeValueFunction<Int,Double,String>()(doubleToString)(intToDouble)(1) == "1.0")
}
