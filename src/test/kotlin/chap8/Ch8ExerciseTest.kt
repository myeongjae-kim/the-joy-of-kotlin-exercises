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

    @Nested
    inner class Ex08 {

        @Test
        fun solve() {
            val list1 = List(1, 2, 3)
            val list2 = List(4, 5, 6, 7)

            val result = List.zipWith(list1, list2) { a ->
                { b -> a + b }
            }

            assertEquals(result.toString(), "[5, 7, 9, NIL]")
        }
    }

    @Nested
    inner class Ex09 {

        @Test
        fun solve() {
            val list1 = List(1, 2)
            val list2 = List(3, 4, 5)

            val result = List.product(list1, list2) { a ->
                { b -> "$a$b" }
            }

            assertEquals(result.toString(), "[13, 14, 15, 23, 24, 25, NIL]")
        }
    }

    @Nested
    inner class Ex11 {

        @Test
        fun solve() {
            val list: List<Pair<Int, Int>> = List(Pair(1, 2), Pair(3, 4))

            assertEquals(List.unzip(list).toString(), "([1, 3, NIL], [2, 4, NIL])")
        }
    }

    @Nested
    inner class Ex12 {

        @Test
        fun solve() {
            val list = List(1, 2, 3)

            assertEquals(list.getAt(0).toString(), "Success(1)")
            assertEquals(list.getAt(1).toString(), "Success(2)")
            assertEquals(list.getAt(2).toString(), "Success(3)")
            assertEquals(list.getAt(3).toString(), "Failure(Index out of bound)")
        }
    }

    @Nested
    inner class Ex14 {
        @Test
        fun solve() {
            val list = List(1, 2)

            val result0 = list.splitAt(-1)
            val result1 = list.splitAt(0)
            val result2 = list.splitAt(1)
            val result3 = list.splitAt(2)
            val result4 = list.splitAt(3)

            assertEquals(result0.toString(), "([NIL], [1, 2, NIL])")
            assertEquals(result1.toString(), "([NIL], [1, 2, NIL])")
            assertEquals(result2.toString(), "([1, NIL], [2, NIL])")
            assertEquals(result3.toString(), "([1, 2, NIL], [NIL])")
            assertEquals(result4.toString(), "([1, 2, NIL], [NIL])")
        }
    }
}