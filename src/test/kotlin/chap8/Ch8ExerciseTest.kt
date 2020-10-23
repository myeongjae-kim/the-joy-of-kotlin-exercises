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

    // Problem 8.4 is meaningless...

    // 8.5 my implementation
    fun <A> flattenResult(list: List<Result<A>>): List<A> = list.coFoldRight(List()) { elem ->
        { acc ->
            when (elem) {
                is Result.Success -> acc.cons(elem.value)
                else -> acc
            }
        }
    }

    // book's implementation
    fun <A> flattenResult2(list: List<Result<A>>): List<A> = list.flatMap { result ->
        result.map { List(it) }.getOrElse(List())
    }

    @Nested
    inner class Ex05 {
        @Test
        fun solve() {
            val expected = "[1, 2, 4, NIL]"
            val list = List(Result(1), Result(2), Result(), Result(4))

            assertEquals(flattenResult(list).toString(), expected)
            assertEquals(flattenResult2(list).toString(), expected)
        }
    }

}