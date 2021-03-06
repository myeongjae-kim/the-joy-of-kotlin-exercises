package chap7

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.Either
import util.Result
import kotlin.IllegalStateException
import kotlin.RuntimeException
import kotlin.test.assertEquals

class Ch7ExercisesTest {

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

    @Nested
    inner class Ex08 {
        @Test
        fun solve() {
            val success = Result(1) { it == 1 }
            val empty = Result(1) { it == 2 }
            val failure1 = Result(null, "what")
            val failure2 = Result(1, "what") { it == 2 }

            assertEquals(success.toString(), "Success(1)")
            assertEquals(empty.toString(), "Empty")
            assertEquals(failure1.toString(), "Failure(what)")
            assertEquals(failure2.toString(), "Failure(Argument 1 does not match condition: what)")
        }
    }

    @Nested
    inner class Ex09 {
        @Test
        fun solve() {
            val success = Result(1) { it == 1 }
            val empty = Result(1) { it == 2 }
            val failure1 = Result(null, "what")
            val failure2 = Result(1, "what") { it == 2 }

            success.forEach {
                assertEquals(it, 1)
            }

            // below lambdas shouldn't be called.
            empty.forEach { throw RuntimeException() }
            failure1.forEach { throw RuntimeException() }
            failure2.forEach { throw RuntimeException() }
        }
    }

    @Nested
    inner class Ex10 {
        @Test
        fun solve() {
            val success = Result(1) { it == 1 }
            val empty = Result(1) { it == 2 }
            val failure1 = Result(null, "what")
            val failure2 = Result(1, "what") { it == 2 }

            val onSuccess: (Int) -> Unit = {
                assertEquals(it, 1)
            }

            val onEmpty: () -> Unit = {
                println("it is empty")
            }

            val onFailure1: (RuntimeException) -> Unit = {
                assert(it is NullPointerException)
            }

            val onFailure2: (RuntimeException) -> Unit = {
                assert(it is IllegalStateException)
            }

            success.forEachOrElse(onSuccess, onFailure1, onEmpty)
            empty.forEachOrElse(onSuccess, onFailure1, onEmpty)
            failure1.forEachOrElse(onSuccess, onFailure1, onEmpty)
            failure2.forEachOrElse(onSuccess, onFailure2, onEmpty)
        }
    }

    @Nested
    inner class Ex11 {
        @Test
        fun solve() {
            val success = Result(1) { it == 1 }
            val empty = Result(1) { it == 2 }
            val failure1 = Result(null, "what")
            val failure2 = Result(1, "what") { it == 2 }

            val onSuccess: (Int) -> Unit = {
                assertEquals(it, 1)
            }

            val onEmpty: () -> Unit = {
                println("it is empty")
            }

            val onFailure1: (RuntimeException) -> Unit = {
                assert(it is NullPointerException)
            }

            val onFailure2: (RuntimeException) -> Unit = {
                assert(it is IllegalStateException)
            }

            success.forEachOrElse(onSuccess = onSuccess)
            empty.forEachOrElse(onEmpty = onEmpty)
            failure1.forEachOrElse(onFailure = onFailure1)
            failure2.forEachOrElse(onFailure = onFailure2)
        }
    }

    fun <A, B> lift(f: (A) -> B): (Result<A>) -> Result<B> = { it.map(f) }

    @Nested
    inner class Ex12 {
        @Test
        fun solve() {
            val a: Result<Int> = Result(1)
            val f: (Int) -> String = { (it + 1).toString() }

            assertEquals(lift(f)(a).toString(), "Success(2)")
        }
    }

    fun <A, B, C> lift2(f: (A) -> (B) -> C): (Result<A>) -> (Result<B>) -> Result<C> =
        { a ->
            { b ->
                a.map(f).flatMap { b.map(it) }
            }
        }

    fun <A, B, C, D> lift3(f: (A) -> (B) -> (C) -> D): (Result<A>) -> (Result<B>) -> (Result<C>) -> Result<D> =
        { a ->
            { b ->
                { c ->
                    a.map(f).flatMap { f2 -> b.map(f2).flatMap { f3 -> c.map(f3) } }
                }
            }
        }

    @Nested
    inner class Ex13 {
        @Test
        fun solve() {
            val f2: (Int) -> (Int) -> Int = { a -> { b -> a + 1 + b + 1 } }
            val f3: (Int) -> (Int) -> (Int) -> Int = { a -> { b -> { c -> a + 1 + b + 1 + c + 1 } } }

            val resultLift2 = lift2(f2)
            val resultLift3 = lift3(f3)

            val result = Result(1)

            assertEquals(resultLift2(result)(result).toString(), "Success(4)")
            assertEquals(resultLift3(result)(result)(result).toString(), "Success(6)")
        }
    }

    fun <A, B, C> map2(oa: Result<A>, ob: Result<B>, f: (A) -> (B) -> C): Result<C> = lift2(f)(oa)(ob)

    @Nested
    inner class Ex14 {
        @Test
        fun solve() {
            val a = Result(1)
            val b = Result(2)
            val f: (Int) -> (Int) -> Int = { x -> { y -> x + y } }

            assertEquals(map2(a, b, f).toString(), "Success(3)")
        }
    }
}
