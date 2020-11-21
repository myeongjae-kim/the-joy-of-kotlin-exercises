package chap8

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.List
import util.Option
import util.Result
import util.range
import util.unfold
import java.math.BigInteger
import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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

    @Nested
    inner class Ex16 {
        @Test
        fun solve() {
            assert(List(1, 2) == List(1, 2))
            assert(List(1, 2) != List(1))
            assert(List('a', 'b') != List(1))

            assert(List(1, 2).startsWith(List(1)))
            assert(!List(1, 2).startsWith(List(2)))
            assert(List(1, 2).startsWith(List()))
            assert(List<Int>().startsWith(List()))
            assert(!List<Int>().startsWith(List(1)))

            assert(List(1, 2, 3).hasSubList(List(1, 2)))
            assert(List(1, 2, 3).hasSubList(List(2, 3)))
            assert(List(1, 2, 3).hasSubList(List(1)))
            assert(List(1, 2, 3).hasSubList(List(2)))
            assert(List(1, 2, 3).hasSubList(List(3)))
            assert(!List(1, 2, 3).hasSubList(List(1, 3)))
            assert(List<Int>().hasSubList(List()))
        }
    }

    @Nested
    inner class Ex17 {
        @Test
        fun solve() {
            val list = List(1, 2, 3, 4, 5)
            val grouped = list.groupBy { it and 1 }

            assert(grouped[0]?.equals(List(2, 4)) ?: false)
            assert(grouped[1]?.equals(List(1, 3, 5)) ?: false)
        }
    }

    @Nested
    inner class Ex18 {
        @Test
        fun solve() {
            val result = unfold(0) { i ->
                if (i < 10)
                    Option(Pair(i, i + 1))
                else
                    Option()
            }

            assertEquals(result, List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
        }
    }

    @Nested
    inner class Ex19 {
        @Test
        fun solve() {
            assertEquals(range(0, 3), List(0, 1, 2))
        }
    }

    @Nested
    inner class Ex20 {
        @Test
        fun solve() {
            assert(List(1, 2).exists { it == 1 })
            assert(!List(1, 2).exists { it == 3 })
        }
    }

    @Nested
    inner class Ex21 {
        @Test
        fun solve() {
            assert(List(1, 3, 5).forAll { it and 1 == 1 })
            assert(!List(1, 3, 5, 6).forAll { it and 1 == 1 })
        }
    }

    @Nested
    inner class Ex22 {
        @Test
        fun solve() {
            assertEquals(
                "[[1, 2, NIL], [3, 4, NIL], NIL]",
                List(1, 2, 3, 4).splitListAt(2).toString()
            )

            // my implementation
            assertEquals(
                "[[1, 2, NIL], [3, 4, NIL], [5, 6, NIL], [7, 8, NIL], NIL]",
                List(1, 2, 3, 4, 5, 6, 7, 8).myDivide(2).toString()
            )

            assertEquals(
                "[[1, NIL], [2, NIL], [3, NIL], [4, NIL], NIL]",
                List(1, 2, 3, 4).myDivide(100).toString()
            )

            assertEquals(
                "[NIL]",
                List<Int>().myDivide(2).toString()
            )

            // book's implementation
            assertEquals(
                "[[1, 2, NIL], [3, 4, NIL], [5, 6, NIL], [7, 8, NIL], NIL]",
                List(1, 2, 3, 4, 5, 6, 7, 8).divide(2).toString()
            )

            assertEquals(
                "[[1, NIL], [2, NIL], [3, NIL], [4, NIL], NIL]",
                List(1, 2, 3, 4).divide(100).toString()
            )

            assertEquals(
                "[NIL]",
                List<Int>().divide(2).toString()
            )
        }
    }

    @Nested
    inner class Ex23 {
        private val random = Random()

        @Test
        @Disabled // result is different with the book...
        fun solve() {
            val testLimit = 35000

            val testList: List<Long> = range(0, testLimit).map {
                random.nextInt(30).toLong()
            }

            val es2 = Executors.newFixedThreadPool(2)
            val es4 = Executors.newFixedThreadPool(4)
            val es8 = Executors.newFixedThreadPool(8)

            testSerial(5, testList, System.currentTimeMillis())
            println("Duration serial 1 thread: ${testSerial(10, testList, System.currentTimeMillis())}")
            testParallel(es2, 5, testList, System.currentTimeMillis())
            println("Duration parallel 2 threads: ${testParallel(es2,10, testList, System.currentTimeMillis())}")
            testParallel(es4, 5, testList, System.currentTimeMillis())
            println("Duration parallel 4 threads: ${testParallel(es4,10, testList, System.currentTimeMillis())}")
            testParallel(es8, 5, testList, System.currentTimeMillis())
            println("Duration parallel 8 threads: ${testParallel(es8,10, testList, System.currentTimeMillis())}")
            es2.shutdown()
            es4.shutdown()
            es8.shutdown()
        }

        private val f = { a: BigInteger -> { b: Long -> a.add(BigInteger.valueOf(fib(b))) } }
        private val g = { a: BigInteger -> { b: BigInteger -> a.add(b) } }

        private fun testSerial(n: Int, list: List<Long>, startTime: Long): Long {
            repeat((0 until n).count()) {
                println("Result:  ${list.foldLeft(BigInteger.ZERO, f)}")
            }
            return System.currentTimeMillis() - startTime
        }

        private fun testParallel(es: ExecutorService, n: Int, list: List<Long>, startTime: Long): Long {
            repeat((0 until n).count()) {
                list.parFoldLeft(es, BigInteger.ZERO, f, g).forEachOrElse(
                    { println("Result: $it") },
                    { println("Exception:  ${it.message}") },
                    { println("Empty result") }
                )
            }
            return System.currentTimeMillis() - startTime
        }

        private fun fib(x: Long): Long {
            return when (x) {
                0L -> 0
                1L -> 1
                else -> fib(x - 1) + fib(x - 2)
            }
        }
    }
}
