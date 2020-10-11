package chap7

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Ch7ExercisesTest {

    sealed class Either<out E, out A> {
        abstract fun <B> map(f: (A) -> B): Either<E, B>
        abstract fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B>

        fun getOrElse(defaultValue: () -> @UnsafeVariance A): A = when(this) {
            is Left -> defaultValue()
            is Right -> this.value
        }

        fun orElse(default: () -> Either<@UnsafeVariance E, @UnsafeVariance A>): Either<E, A> = map { this }.getOrElse(default)

        internal class Left<out E, out A>(private val value: E): Either<E, A>() {
            override fun toString(): String = "Left($value)"
            override fun <B> map(f: (A) -> B): Either<E, B> = Left(value)
            override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B> = Left(value)
        }

        internal class Right<out E, out A>(internal val value: A): Either<E, A>() {
            override fun toString(): String = "Right($value)"
            override fun <B> map(f: (A) -> B): Either<E, B> = Right(f(this.value))
            override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B> = f(this.value)
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
            assertEquals(Either.left<String, Int>("error").map(f).toString(), "Left(error)")
        }
    }

    @Nested
    inner class Ex02 {
        @Test
        fun solve() {
            val f: (Int) -> Either<String, String> = { Either.right((it + 1).toString()) }

            val result: Either<String, String> = Either.right<String, Int>(1).flatMap(f)

            assertEquals(result.toString(), "Right(2)")
            assertEquals(Either.left<String, Int>("error").flatMap(f).toString(), "Left(error)")
        }
    }

    @Nested
    inner class Ex03 {
        @Test
        fun solve() {
            val default: () -> Int = { 2 }
            val defaultEither: () -> Either<String, Int> = { Either.right(2) }

            val result1: Int = Either.right<String, Int>(1).getOrElse(default)
            val result2: Either<String, Int> = Either.right<String, Int>(1).orElse(defaultEither)

            val result3: Int = Either.left<String, Int>("error").getOrElse(default)
            val result4: Either<String, Int> = Either.left<String, Int>("error").orElse(defaultEither)

            assertEquals(result1, 1)
            assertEquals(result2.toString(), "Right(1)")

            assertEquals(result3, 2)
            assertEquals(result4.toString(), "Right(2)")
        }
    }
}
