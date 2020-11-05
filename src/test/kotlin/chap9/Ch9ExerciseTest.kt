package chap9

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.Lazy
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
}