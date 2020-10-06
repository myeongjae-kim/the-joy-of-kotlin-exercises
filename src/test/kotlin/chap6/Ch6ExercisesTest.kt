package chap6

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
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
}
