package chap6

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
import kotlin.math.pow
import kotlin.test.assertEquals

class Ch6ExercisesTest {

    sealed class Option<out A> {

        abstract fun isEmpty(): Boolean

        abstract fun <B> map(f: (A) -> B): Option<B>

        fun getOrElse(default: @UnsafeVariance A): A = when(this) {
            None -> default
            is Some -> this.value
        }

        fun getOrElse(default: () -> @UnsafeVariance A): A = when(this) {
            None -> default()
            is Some -> this.value
        }

        fun <B> flatMap(f: (A) -> Option<B>): Option<B> = this.map(f).getOrElse(None)

        fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> = map { this }.getOrElse(default)

        fun filter(p: (A) -> Boolean): Option<A> = flatMap { if (p(it)) this else None }

        internal object None: Option<Nothing>() {

            override fun isEmpty(): Boolean = true

            override fun toString(): String = "None"

            override fun equals(other: Any?): Boolean = other === None

            override fun hashCode(): Int = 0

            override fun <B> map(f: (Nothing) -> B): Option<B> = None
        }

        internal data class Some<out A>(internal val value: A): Option<A>() {

            override fun isEmpty() = false

            override fun <B> map(f: (A) -> B): Option<B> = Some(f(this.value))
        }

        companion object {

            operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
                null -> None
                else -> Some(a)
            }
        }
    }

    @Nested
    inner class Ex01 {
        @Test
        fun solve() {
            assertEquals(Option(1).getOrElse(0), 1)
            assertEquals(Option<Int>().getOrElse(0), 0)
        }
    }

    fun <T: Comparable<T>> max(list: List<T>): Option<T> = Option(list.maxByOrNull { it })

    fun getDefault(): Int = throw RuntimeException()

    @Nested
    inner class Ex02 {
        @Test
        fun solve() {
            val max1 = max(listOf(3, 5, 7, 2, 1)).getOrElse(::getDefault)
            assertEquals(max1, 7)

            val exception = assertThrows<RuntimeException> { max(listOf<Int>()).getOrElse(::getDefault) }
            assertEquals(exception.javaClass, RuntimeException::class.java)
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            assertEquals(Option<Double>().map(Double::toString), Option())
            assertEquals(Option(1.0).map(Double::toString).getOrElse { throw RuntimeException() }, "1.0")
        }
    }

    @Nested
    inner class Ex04 {

        @Test
        fun solve() {
            val f: (Double) -> Option<String> = { it -> Option(it.toString()) }

            assertEquals(Option(1.0).flatMap(f).getOrElse(""), "1.0")
            assertEquals(Option<Double>().flatMap(f).getOrElse(""), "")
        }
    }

    @Nested
    inner class Ex05 {

        @Test
        fun solve() {
            assertEquals(Option(1.0).orElse { Option(0.0) }.getOrElse(-1.0).toString(), "1.0")
            assertEquals(Option<Double>().orElse { Option(0.0) }.getOrElse(-1.0).toString(), "0.0")
        }
    }

    @Nested
    inner class Ex06 {

        @Test
        fun solve() {
            assertEquals(Option(1).filter { it == 1 }.getOrElse(0), 1)
            assertEquals(Option(1).filter { it != 1 }.getOrElse(0), 0)
        }
    }

    @Nested
    inner class Ex07 {
        // my implementation
        val myVariance: (List<Double>) -> Option<Double> = {
            Option(it)
                    .filter { l -> l.isNotEmpty() }
                    .map { l -> l.sum() / l.size }
                    .map { m -> it.fold(0.0, {acc, x -> (x - m).pow(2.0) + acc}) / it.size }
        }

        // book's implementation
        val mean: (List<Double>) -> Option<Double> = { list ->
            when {
                list.isEmpty() -> Option()
                else -> Option(list.sum() / list.size)
            }
        }

        val variance: (List<Double>) -> Option<Double> = { list ->
            mean(list).flatMap { m ->
                mean(list.map { x -> (x - m).pow(2) })
            }
        }

        @Test
        fun solve() {
            assertEquals(myVariance(listOf(1.0, 2.0, 3.0)), variance(listOf(1.0, 2.0, 3.0)))
        }
    }

    fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = {
        try {
            it.map(f)
        } catch (e: Exception) {
            Option()
        }
    }

    @Nested
    inner class Ex08 {

        @Test
        fun solve() {
            val f: (Int) -> String = { it.toString() }
            val lifted = lift(f)

            val a = Option(1)
            val b = lifted(a)

            assertEquals(b.getOrElse(""), "1")
        }
    }

    @Nested
    inner class Ex09 {

        @Test
        fun solve() {
            val f: (Int) -> String = { throw RuntimeException() }
            val lifted = lift(f)

            val a = Option(1)
            val b = lifted(a)

            assertEquals(b.getOrElse(""), "")
        }
    }
}
