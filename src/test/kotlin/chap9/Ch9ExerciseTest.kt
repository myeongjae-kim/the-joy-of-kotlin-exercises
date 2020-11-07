package chap9

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.Lazy
import util.List
import util.Stream
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
}
