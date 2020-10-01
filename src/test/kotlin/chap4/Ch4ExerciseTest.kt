package chap4

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Factorial {
    private lateinit var fact: (Int) -> Int;
    init {
        fact = { if (it == 1) it else it * fact(it - 1) }
    }

    val factorial = fact
}

class Ch4ExerciseTest {

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            fun inc(n: Int): Int = n + 1
            fun dec(n: Int): Int = n - 1

            tailrec fun add(a: Int, b:Int): Int = if (b <= 0) a else add(inc(a), dec(b))

            assertEquals(add(5, 10), 15)
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            fun factorialRecursive(n: Int): Int = if (n == 0) 1 else n * factorialRecursive(n - 1)

            fun factorialTailRecursive(n: Int): Int {
                tailrec fun f(result: Int, n: Int): Int = if (n == 1) result else f(result * n, n - 1)

                return f(1, n)
            }

            val factorialTailRecursiveLambda: (Int) -> Int = { n ->
                tailrec fun f(result: Int, n: Int): Int = if (n == 1) result else f(result * n, n - 1)
                f(1, n)
            }

            assertEquals(factorialRecursive(5), 120)
            assertEquals(factorialTailRecursive(5), 120)
            assertEquals(factorialTailRecursiveLambda(5), 120)
            assertEquals(Factorial.factorial(5), 120)
        }
    }
}
