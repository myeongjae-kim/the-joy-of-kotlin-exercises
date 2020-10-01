package chap4

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
}
