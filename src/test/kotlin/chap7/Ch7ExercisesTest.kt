package chap7

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Ch7ExercisesTest {

    sealed class Either<out E, out A> {
        abstract fun <B> map(f: (A) -> B): Either<E, B>

        internal class Left<out E, out A>(private val value: E): Either<E, A>() {
            override fun toString(): String = "Left($value)"
            override fun <B> map(f: (A) -> B): Either<E, B> = Left(value)
        }

        internal class Right<out E, out A>(private val value: A): Either<E, A>() {
            override fun toString(): String = "Right($value)"
            override fun <B> map(f: (A) -> B): Either<E, B> = Right(f(this.value))
        }

        companion object {
            fun <E, A> left(value: E): Either<E, A> = Left(value)
            fun <E, A> right(value: A): Either<E, A> = Right(value)
        }
    }

    @Nested
    inner class Ex01 {
        @Test
        fun solve() {
            val f: (Int) -> String = { (it + 1).toString() }

            val result: Either<String, String> = Either.right<String, Int>(1).map(f)

            assertEquals(result.toString(), "Right(2)")
        }
    }
}
