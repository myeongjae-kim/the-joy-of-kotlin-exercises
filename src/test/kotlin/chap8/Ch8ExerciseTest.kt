package chap8

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.List
import kotlin.test.assertEquals
import util.Result

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
            val emptyList = List<Int>()
            assertEquals(emptyList.headSafe().getOrElse(999), 999)

            val list = List(1, 2)
            assertEquals(list.headSafe().getOrElse(999), 1)
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            val emptyList = List<Int>()
            assertEquals(emptyList.lastSafe().getOrElse(999), 999)

            val list = List(1, 2)
            assertEquals(list.lastSafe().getOrElse(999), 2)
        }
    }
}