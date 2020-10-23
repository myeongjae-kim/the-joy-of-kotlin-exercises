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

    @Nested
    inner class Ex06 {
        fun <A> sequence(list: List<Result<A>>): Result<List<A>> =
                list.coFoldRight(Result(List())) { elem ->
                    { acc ->
                        acc.flatMap { list -> elem.map { list.cons(it) } }
                    }
                }

        @Test
        fun solve() {
            val expected = "Empty"
            val list = List(Result(1), Result(2), Result(), Result(4))

            assertEquals(sequence(list).toString(), expected)
            assertEquals(sequence(List(Result(1), Result(2))).toString(), "Success([1, 2, NIL])")
        }
    }

    fun <A> sequence(list: List<Result<A>>): Result<List<A>> = traverse(list) { it }

    fun <A, B> traverse(list: List<A>, f: (A) -> Result<B>): Result<List<B>> =
        list.coFoldRight(Result(List())) { elem: A ->
            { acc: Result<List<B>> ->
                acc.flatMap { list -> f(elem).map { list.cons(it) } }
            }
        }

    @Nested
    inner class Ex07 {

        @Test
        fun solve() {
            val expected = "Empty"
            val list = List(Result(1), Result(2), Result(), Result(4))

            assertEquals(sequence(list).toString(), expected)
            assertEquals(sequence(List(Result(1), Result(2))).toString(), "Success([1, 2, NIL])")
        }
    }
}