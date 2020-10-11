package chap7

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.Serializable
import java.lang.IllegalStateException
import java.lang.RuntimeException
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

    sealed class Result<out A>: Serializable {

        abstract fun<B> map(f: (A) -> B): Result<B>
        abstract fun<B> flatMap(f: (A) -> Result<B>): Result<B>
        abstract fun mapFailure(message: String): Result<A>

        internal object Empty: Result<Nothing>() {
            override fun toString(): String = "Empty"

            override fun <B> map(f: (Nothing) -> B): Result<B> = Empty

            override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> = Empty

            override fun mapFailure(message: String): Result<Nothing> = this
        }

        internal class Failure<out A>(internal val exception: RuntimeException): Result<A>() {
            override fun toString(): String = "Failure(${exception.message})"

            override fun <B> map(f: (A) -> B): Result<B> = Failure(this.exception)

            override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = Failure(this.exception)

            override fun mapFailure(message: String): Result<A> = Failure(RuntimeException(message, exception))
        }

        internal class Success<out A>(internal val value: A): Result<A>() {
            override fun toString(): String = "Success($value)"

            override fun <B> map(f: (A) -> B): Result<B> = try {
                Success(f(this.value))
            } catch (e: RuntimeException) {
                Failure(e)
            } catch (e: Exception) {
                Failure(RuntimeException(e))
            }

            override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = try {
                f(this.value)
            } catch (e: RuntimeException) {
                Failure(e)
            } catch (e: Exception) {
                Failure(RuntimeException(e))
            }

            override fun mapFailure(message: String): Result<A> = this
        }

        companion object {
            operator fun <A> invoke(): Result<A> = Empty

            operator fun <A> invoke(a: A? = null): Result<A> = when(a) {
                null -> Failure(NullPointerException())
                else -> Success(a)
            }
        }

        fun <A> failure(message: String): Result<A> = Failure(IllegalStateException(message))
        fun <A> failure(exception: RuntimeException): Result<A> = Failure(exception)
        fun <A> failure(exception: Exception): Result<A> = Failure(IllegalStateException(exception))

        fun getOrElse(defaultValue: @UnsafeVariance A): A = when(this) {
            is Success -> this.value
            else -> defaultValue
        }

        fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> = when(this) {
            is Success -> this
            else -> try {
                defaultValue()
            } catch (e: RuntimeException) {
                Failure(e)
            } catch (e: Exception) {
                Failure(RuntimeException(e))
            }
        }

        fun filter(p: (A) -> Boolean, failureMessage: String): Result<A> = flatMap {
            if (p(it))
                this
            else
                failure(failureMessage)
        }

        fun filter(p: (A) -> Boolean): Result<A> {
            return filter(p, "Condition not matched")
        }

        fun exists(p: (A) -> Boolean): Boolean = map(p).getOrElse(false) // alternative:  filter(p) is Success
    }

    @Nested
    inner class Ex04 {
        @Test
        fun solve() {
            val success: Result<Int> = Result(1)
            val failure: Result<Int> = Result(null)

            val f: (Int) -> String = Int::toString
            val fFlat: (Int) -> Result<String> = { Result(f(it)) }

            val fException: (Int) -> String = { throw RuntimeException("e") }
            val fFlatException: (Int) -> Result<String> = { throw RuntimeException("e") }

            assertEquals(success.map(f).toString(), "Success(1)")
            assertEquals(failure.map(f).toString(), "Failure(null)")
            assertEquals(success.map(fException).toString(), "Failure(e)")
            assertEquals(failure.map(fException).toString(), "Failure(null)")

            assertEquals(success.flatMap(fFlat).toString(), "Success(1)")
            assertEquals(failure.flatMap(fFlat).toString(), "Failure(null)")
            assertEquals(success.flatMap(fFlatException).toString(), "Failure(e)")
            assertEquals(failure.flatMap(fFlatException).toString(), "Failure(null)")

            assertEquals(success.getOrElse(2), 1)
            assertEquals(failure.getOrElse(2), 2)

            val fElse: () -> Result<Int> = { Result(2) }
            val fElseException: () -> Result<Int> = { throw RuntimeException("e") }

            assertEquals(success.orElse(fElse).toString(), "Success(1)")
            assertEquals(failure.orElse(fElse).toString(), "Success(2)")
            assertEquals(success.orElse(fElseException).toString(), "Success(1)")
            assertEquals(failure.orElse(fElseException).toString(), "Failure(e)")
        }
    }


    @Nested
    inner class Ex05 {
        @Test
        fun solve() {
            val i1 = Result(1)
            val i2 = Result(2)
            val p: (Int) -> Boolean = { it == 1 }

            assertEquals(i1.filter(p).toString(), "Success(1)")
            assertEquals(i2.filter(p).toString(), "Failure(Condition not matched)")
        }
    }

    @Nested
    inner class Ex06 {
        @Test
        fun solve() {
            val i1 = Result(1)
            val i2 = Result(2)
            val p: (Int) -> Boolean = { it == 1 }

            assertEquals(i1.exists(p), true)
            assertEquals(i2.exists(p), false)
        }
    }

    @Nested
    inner class Ex07 {
        @Test
        fun solve() {
            val i1 = Result(1)
            val i2 = Result(2)
            val p: (Int) -> Boolean = { it == 1 }

            assertEquals(i1.filter(p).mapFailure("what").toString(), "Success(1)")
            assertEquals(i2.filter(p).mapFailure("what").toString(), "Failure(what)")
        }
    }
}
