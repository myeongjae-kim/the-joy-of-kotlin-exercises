package chap9

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.Lazy
import util.List
import util.Stream
import util.Result
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.test.assertEquals

class Ch9ExerciseTest {

    @Nested
    inner class Ex01 {
        fun or(a: Lazy<Boolean>, b: Lazy<Boolean>): Boolean = if (a()) true else b()

        @Test
        fun solve() {
            val first = Lazy {
                println("Evaluating first")
                true
            }

            val second = Lazy<Boolean> {
                println("Evaluating second")
                throw IllegalStateException()
            }

            println(first() || second())
            println(first() || second())
            println(or(first, second))
        }
    }

    @Nested
    inner class Ex02 {
        @Test
        fun solve() {
            fun constructMessage(greetings: Lazy<String>, name:Lazy<String>): Lazy<String> =
                    Lazy { "${greetings()}, ${name()}!" }

            val greetings = Lazy {
                println("Evaluating greetings")
                "Hello"
            }

            val name1: Lazy<String> = Lazy {
                println("Evaluating name")
                "Mickey"
            }

            val name2: Lazy<String> = Lazy {
                println("Evaluating name")
                "Donald"
            }

            val defaultMessage = Lazy {
                println("Evaluating default message")
                "No greetings when time is odd"
            }

            val message1 = constructMessage(greetings, name1)
            val message2 = constructMessage(greetings, name2)
            val condition = Random(System.currentTimeMillis()).nextInt() and 1 == 0
            println(if (condition) message1() else defaultMessage())
            println(if (condition) message1() else defaultMessage())
            println(if (condition) message2() else defaultMessage())
        }
    }

    @Nested
    inner class Ex03 {
        @Test
        fun solve() {
            val constructMessage: (Lazy<String>) -> (Lazy<String>) -> Lazy<String> =
                    { greetings ->
                        { name ->
                            Lazy { "${greetings()}, ${name()}!"}
                        }
                    }

            val greetings = Lazy {
                println("Evaluating greetings")
                "Hello"
            }

            val name1: Lazy<String> = Lazy {
                println("Evaluating name")
                "Mickey"
            }

            val name2: Lazy<String> = Lazy {
                println("Evaluating name")
                "Donald"
            }

            val defaultMessage = Lazy {
                println("Evaluating default message")
                "No greetings when time is odd"
            }

            val greetingsString = constructMessage(greetings)

            val message1 = greetingsString(name1)
            val message2 = greetingsString(name2)
            val condition = Random(System.currentTimeMillis()).nextInt() and 1 == 0
            println(if (condition) message1() else defaultMessage())
            println(if (condition) message1() else defaultMessage())
            println(if (condition) message2() else defaultMessage())
        }
    }

    @Nested
    inner class Ex04 {
        @Test
        fun solve() {
            fun lift2(f: (String) -> (String) -> String): (Lazy<String>) -> (Lazy<String>) -> Lazy<String> =
                    { s1 ->
                        { s2 ->
                            Lazy { f(s1())(s2()) }
                        }
                    }

            val consMessage: (String) -> (String) -> String =
                    { greetings ->
                        { name ->
                            "$greetings, $name!"
                        }
                    }

            val lazyConsMessage = lift2(consMessage)

            assertEquals("Hello, world!", lazyConsMessage(Lazy {"Hello"})(Lazy {"world"})())
        }
    }

    @Nested
    inner class Ex05 {
        @Test
        fun solve() {
            val consMessage: (String) -> (String) -> String =
                    { greetings ->
                        { name ->
                            "$greetings, $name!"
                        }
                    }

            val lazyConsMessage = Lazy.lift2(consMessage)

            assertEquals("Hello, world!", lazyConsMessage(Lazy {"Hello"})(Lazy {"world"})())
        }
    }

    @Nested
    inner class Ex06 {
        @Test
        fun solve() {
            assertEquals(Lazy { 1 }.map { it.toString() }(), "1")
        }
    }

    @Nested
    inner class Ex07 {
        @Test
        fun solve() {
            assertEquals(Lazy { 1 }.flatMap { Lazy{ it.toString() } }(), "1")
        }
    }

    @Nested
    inner class Ex08 {
        @Test
        fun solve() {
            assertEquals(Lazy.sequence(List(Lazy { 1 }, Lazy { 2 }))().toString(), "[1, 2, NIL]")
        }
    }

    @Nested
    inner class Ex09 {
        @Test
        fun solve() {
            assertEquals(Lazy.sequenceResult(List(Lazy { 1 }, Lazy { 2 }))().getOrElse(List()).toString(), "[1, 2, NIL]")
        }
    }

    @Nested
    inner class Ex11 {
        @Test
        fun solve() {
            val stream: Stream<Int> = Stream.repeat { 1 }
            assertEquals(stream.head().toString(), "Success(1)")
            assertEquals(stream.tail().getOrElse(Stream()).head().toString(), "Success(1)")
        }
    }

    @Nested
    inner class Ex12 {
        @Test
        fun solve() {
            val stream: Stream<Int> = Stream.from(1)

            var limited = stream.takeAtMost(4)

            var i = 1
            while (!limited.isEmpty()) {
                assertEquals(limited.head().getOrElse(-1), i++)
                limited = limited.tail().getOrElse(Stream())
            }
        }
    }

    @Nested
    inner class Ex13 {
        @Test
        fun solve() {
            val stream = Stream.from(1)

            val dropped = stream.dropAtMost(4)

            assertEquals(dropped.head().toString(), "Success(5)")

            val dropped2 = Stream.cons(Lazy { 1 }, Lazy { Stream() }).dropAtMost(4)

            assertEquals(dropped2.head().toString(), "Empty")
        }
    }

    @Nested
    inner class Ex14 {

        fun random(): Int {
            val rnd = ThreadLocalRandom.current().nextInt()
            println("evaluating $rnd")
            return rnd
        }

        @Test
        fun solve() {
            val stream = Stream.repeat(::random).dropAtMost(60000).takeAtMost(60000)
            stream.head().forEach(::println)
        }
    }

    @Nested
    inner class Ex15 {

        @Test
        fun solve() {
            val expected = ("[${(60000..119999).joinToString(", ")}, NIL]")

            val stream = Stream.from(0).dropAtMost(60000).takeAtMost(60000)
            assertEquals(expected,  stream.toList().toString())
        }
    }

    @Nested
    inner class Ex16 {

        @Test
        fun solve() {
            assertEquals("[1, 2, 3, NIL]", Stream.from(1).takeAtMost(3).toList().toString())
            assertEquals("[1, 3, 5, NIL]", Stream.iterate(1) {it + 2}.takeAtMost(3).toList().toString())

            fun inc(i: Int): Int = (i + 1).let {
                println("generating $it")
                it
            }

            val list = Stream
                    .iterate(0, ::inc)
                    .takeAtMost(60000)
                    .dropAtMost(10000)
                    .takeAtMost(10)
                    .toList()

            assertEquals("[10000, 10001, 10002, 10003, 10004, 10005, 10006, 10007, 10008, 10009, NIL]",
                    list.toString())
        }
    }

    @Nested
    inner class Ex17 {

        @Test
        fun solve() {
            assertEquals("[0, 1, 2, 3, NIL]", Stream.from(0).takeWhile { it < 4 }.toList().toString())
        }
    }

    @Nested
    inner class Ex18 {

        @Test
        fun solve() {
            assertEquals(
                    "[10, 11, NIL]",
                    Stream.from(0)
                            .dropWhile { it < 10 }
                            .takeAtMost(2)
                            .toList().toString())
        }
    }

    @Nested
    inner class Ex19 {

        @Test
        fun solve() {
            assert(
                    Stream.iterate(0) {
                        println("evaluated: ${it + 1}")
                        it + 1
                    }.exists { it == 10 })
        }
    }

    @Nested
    inner class Ex20 {

        @Test
        fun solve() {
            val sum = Stream.from(1).takeAtMost(10).foldRight(Lazy {0}) { elem ->
                { acc ->
                    acc() + elem
                }
            }

            assertEquals(sum, 55)
        }
    }


    @Nested
    inner class Ex21 {

        @Test
        fun solve() {
            assertEquals("[1, 2, 3, 4, NIL]",
                    Stream.from(1).takeWhileViaFoldRight { it < 5 }.toList().toString())
        }
    }

    @Nested
    inner class Ex22 {

        @Test
        fun solve() {
            assertEquals(
                    "Success(0)",
                    Stream.from(0).headSafeViaFoldRight().toString())

            assertEquals(
                    "Empty",
                    Stream<Int>().headSafeViaFoldRight().toString())
        }
    }

    @Nested
    inner class Ex23 {

        @Test
        fun solve() {
            var evaluated = false

            val s: Stream<String> = Stream.from(1).takeAtMost(3).map {
                evaluated = true
                it.toString()
            }

            assert(!evaluated)

            assertEquals("[1, 2, 3, NIL]", s.toList().toString())

            assert(evaluated)
        }
    }

    @Nested
    inner class Ex24 {

        @Test
        fun solve() {
            var evaluatedValue = 0

            val s: Stream<Int> = Stream.from(0).takeAtMost(4).filter {
                evaluatedValue = it
                it and 1 == 1
            }

            assertEquals(1, evaluatedValue)

            assertEquals("[1, 3, NIL]", s.toList().toString())

            assertEquals(3, evaluatedValue)
        }
    }

    @Nested
    inner class Ex25 {

        @Test
        fun solve() {
            val s1 = Stream.from(1).takeAtMost(3)
            val s2 = Stream.from(4).takeAtMost(3)

            assertEquals(
                    "[1, 2, 3, 4, 5, 6, NIL]",
                    s1.append(Lazy { s2 }).toList().toString())
        }
    }

    @Nested
    inner class Ex26 {

        @Test
        fun solve() {
            val f: (Int) -> Stream<Int> = { Stream.cons(Lazy {it}, Lazy { Stream() }) }

            assertEquals(
                    "[1, 2, 3, NIL]",
                    Stream.from(1).takeAtMost(3).flatMap(f).toList().toString())
        }
    }

    @Nested
    inner class Ex27 {

        @Test
        fun solve() {
            val s = Stream.from(1)
            var evaluated = 0

            assertEquals(
                    "Success(3)",
                    s.find {
                        evaluated = it
                        it == 3
                    }.toString())

            assertEquals(evaluated, 3)

            assertEquals("Empty", Stream.from(1).takeAtMost(3).find { it == 4 }.toString())
        }
    }

    @Nested
    inner class Ex28 {

        @Test
        fun solve() {
            fun fibs(): Stream<Int> = Stream.iterate(Pair(0, 1)) {
                Pair(it.second, it.first + it.second)
            }.map { it.second }

            val s = fibs()

            assertEquals("[1, 1, 2, 3, 5, 8, 13, 21, 34, 55, NIL]", s.takeAtMost(10).toList().toString())
        }
    }

    @Nested
    inner class Ex29 {

        @Test
        fun solve() {
            assertEquals(
                    "[1, 2, 3, NIL]",
                    Stream.from(1).takeAtMost(3).toList().toString())

            fun fibs(): Stream<Int> = Stream.unfold(Pair(0, 1)) {
                Result(Pair(it.second, Pair(it.second, it.first + it.second)))
            }

            assertEquals("[1, 1, 2, 3, 5, 8, 13, 21, 34, 55, NIL]",
                    fibs().takeAtMost(10).toList().toString())
        }
    }
}
