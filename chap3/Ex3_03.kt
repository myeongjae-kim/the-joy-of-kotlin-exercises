
fun addFunction(a: Int): (Int) -> Int = { b -> a + b }
fun addFunctionAlternative(a: Int): (Int) -> Int = { a + it }

typealias IntBinOp = (Int) -> (Int) -> Int

fun main() {
  val addLambda: IntBinOp = { a -> { b -> a + b}}

  assert(addFunction(1)(2) == 3)
  assert(addFunctionAlternative(1)(2) == 3)
  assert(addLambda(1)(2) == 3)
}
