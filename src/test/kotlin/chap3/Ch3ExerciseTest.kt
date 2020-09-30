package chap3

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

typealias IntUnaryOp = (Int) -> Int
typealias IntBinOp = (Int) -> (Int) -> Int

class Ch3ExerciseTest {

    @Nested
    inner class Ex01 {
        private fun square(n: Int) = n * n
        private fun triple(n: Int) = n * 3
        private fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
            return { f(g(it)) }
        }

        @Test
        fun test() {
            val composed = compose(::square, ::triple)

            val result = composed(2)
            assertEquals(result, 36)
        }
    }

    @Nested
    inner class Ex02 {
        private fun square(n: Int) = n * n
        private fun triple(n: Int) = n * 3
        private fun <T, U, V> compose(f: (U) -> V, g: (T) -> U): (T) -> V = { f(g(it)) }

        @Test
        fun test() {
            val composed = compose(::square, ::triple)

            val result = composed(2)
            assertEquals(result, 36)
        }
    }

    @Nested
    inner class Ex03 {
        private fun addFunction(a: Int): (Int) -> Int = { b -> a + b }
        private fun addFunctionAlternative(a: Int): (Int) -> Int = { a + it }

        @Test
        fun test() {
            val addLambda: IntBinOp = { a -> { b -> a + b } }

            assertEquals(addFunction(1)(2), 3)
            assertEquals(addFunctionAlternative(1)(2), 3)
            assertEquals(addLambda(1)(2), 3)
        }
    }

    @Nested
    inner class Ex04 {
        private fun square(n: Int) = n * n
        private fun triple(n: Int) = n * 3

        @Test
        fun test() {
            // val compose: ((Int) -> Int) -> ((Int) -> Int) -> ((Int) -> Int) = { f -> {g -> { x -> f(g(x)) } } }
            val compose: (IntUnaryOp) -> (IntUnaryOp) -> IntUnaryOp = { f -> { g -> { x -> f(g(x)) } } }
            assertEquals(compose(::square)(::triple)(2), 36)

            val squareValueFunction: IntUnaryOp = { it * it }
            val tripleValueFunction: IntUnaryOp = { it * 3 }

            assertEquals(compose(squareValueFunction)(tripleValueFunction)(2), 36)
        }
    }

    @Nested
    inner class Ex05 {
        // 빡대가리가 된 기분이었다... C언어 배울때 2차원 포인터 배열 이해하는 기분. 그러나 해냈다.
        private fun <T, U, V> compose(f: (U) -> V): ((T) -> U) -> (T) -> V = { g -> { x -> f(g(x)) } }

        // Just a wrapper to create a value function with type parameters.
        // Only classes, interfaces, and functions declared with `fun` can defined type parameters.
        private fun <T, U, V> createComposeValueFunction(): ((U) -> V) -> ((T) -> U) -> (T) -> V = { f -> { g -> { x -> f(g(x)) } } }

        @Test
        fun test() {
            val square: (Int) -> Int = { it * it }
            val triple: (Int) -> Int = { it * 3 }
            assertEquals(compose<Int, Int, Int>(square)(triple)(2), 36)
            assertEquals(createComposeValueFunction<Int, Int, Int>()(square)(triple)(2), 36)

            val intToDouble: (Int) -> Double = { it * 1.0 }
            val doubleToString: (Double) -> String = { it.toString() }
            assertEquals(compose<Int, Double, String>(doubleToString)(intToDouble)(1), "1.0")
            assertEquals(createComposeValueFunction<Int, Double, String>()(doubleToString)(intToDouble)(1), "1.0")
        }
    }

    @Nested
    inner class Ex06 {
        private fun <T, U, V> higherCompose(): ((U) -> V) -> ((T) -> U) -> (T) -> V = { f -> { g -> { x -> f(g(x)) } } }
        private fun <T, U, V> composeAndThen(): ((T) -> U) -> ((U) -> V) -> (T) -> V = { f -> { g -> { x -> g(f(x)) } } }

        @Test
        fun test() {
            val square: (Int) -> Int = { it * it }
            val triple: (Int) -> Int = { it * 3 }
            assertEquals(higherCompose<Int, Int, Int>()(square)(triple)(2), 36)
            assertEquals(composeAndThen<Int, Int, Int>()(square)(triple)(2), 12)

            val intToDouble: (Int) -> Double = { it * 1.0 }
            val doubleToString: (Double) -> String = { it.toString() }
            assertEquals(higherCompose<Int, Double, String>()(doubleToString)(intToDouble)(1), "1.0")
            assertEquals(composeAndThen<Int, Double, String>()(intToDouble)(doubleToString)(1), "1.0")
        }
    }

    @Nested
    inner class Ex07 {
        private fun <A, B, C> partialA(a: A, f: (A) -> (B) -> C): (B) -> C = f(a)

        @Test
        fun test() {
            val alreadyAdded = partialA<Int, Double, String>(1, { x -> { y -> (x + y).toString() } })

            assertEquals(alreadyAdded(2.0), "3.0")
        }
    }

    @Nested
    inner class Ex08 {
        private fun <A, B, C> partialA(b: B, f: (A) -> (B) -> C): (A) -> C = { a -> f(a)(b) }

        @Test
        fun test() {
            val alreadyAdded = partialA<Int, Double, String>(1.0, { x -> { y -> (x + y).toString() } })

            assertEquals(alreadyAdded(2), "3.0")
        }
    }

    @Nested
    inner class Ex09 {
        private fun <A, B, C, D> func(a: A, b: B, c: C, d: D): String = "$a, $b, $c, $d"
        private fun <A, B, C, D> funcCurried(a: A): (B) -> (C) -> (D) -> String = { b -> { c -> { d -> "$a, $b, $c, $d" } } }
        private fun <A, B, C, D> curried(): (A) -> (B) -> (C) -> (D) -> String = { a -> { b -> { c -> { d -> "$a, $b, $c, $d" } } } }

        @Test
        fun test() {
            assertEquals(func("A", "B", "C", "D"), funcCurried<String, String, String, String>("A")("B")("C")("D"))
            assertEquals(func("A", "B", "C", "D"), curried<String, String, String, String>()("A")("B")("C")("D"))
        }
    }
}
