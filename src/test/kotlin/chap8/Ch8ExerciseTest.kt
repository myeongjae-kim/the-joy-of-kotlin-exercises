package chap8

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.List
import kotlin.test.assertEquals

class Ch8ExerciseTest {

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            assertEquals(List(1, 2, 3).lengthMemoized(), 3)
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            val emptyList = List<Int>().headSafe()
            assertEquals(emptyList.getOrElse(999), 999)

            val list = List(1).headSafe()
            assertEquals(list.getOrElse(999), 1)
        }
    }
}