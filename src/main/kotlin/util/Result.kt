package util

import java.io.Serializable

sealed class Result<out A> : Serializable {

    abstract fun <B> map(f: (A) -> B): Result<B>
    abstract fun <B> flatMap(f: (A) -> Result<B>): Result<B>
    abstract fun mapFailure(message: String): Result<A>
    abstract fun forEach(effect: (A) -> Unit)
    abstract fun forEachOrElse(
            onSuccess: (A) -> Unit = {},
            onFailure: (RuntimeException) -> Unit = {},
            onEmpty: () -> Unit = {}
    )

    internal object Empty : Result<Nothing>() {
        override fun toString(): String = "Empty"

        override fun <B> map(f: (Nothing) -> B): Result<B> = Empty

        override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> = Empty

        override fun mapFailure(message: String): Result<Nothing> = this

        override fun forEach(effect: (Nothing) -> Unit) { }

        override fun forEachOrElse(
                onSuccess: (Nothing) -> Unit,
                onFailure: (RuntimeException) -> Unit,
                onEmpty: () -> Unit
        ) {
            onEmpty()
        }
    }

    internal class Failure<out A>(internal val exception: RuntimeException) : Result<A>() {
        override fun toString(): String = "Failure(${exception.message})"

        override fun <B> map(f: (A) -> B): Result<B> = Failure(this.exception)

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> = Failure(this.exception)

        override fun mapFailure(message: String): Result<A> = Failure(RuntimeException(message, exception))

        override fun forEach(effect: (A) -> Unit) { }

        override fun forEachOrElse(
                onSuccess: (A) -> Unit,
                onFailure: (RuntimeException) -> Unit,
                onEmpty: () -> Unit
        ) {
            onFailure(this.exception)
        }
    }

    internal class Success<out A>(internal val value: A) : Result<A>() {
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

        override fun forEach(effect: (A) -> Unit) { effect(this.value) }

        override fun forEachOrElse(
                onSuccess: (A) -> Unit,
                onFailure: (RuntimeException) -> Unit,
                onEmpty: () -> Unit
        ) {
            onSuccess(this.value)
        }
    }

    companion object {
        operator fun <A> invoke(): Result<A> = Empty

        operator fun <A> invoke(a: A? = null): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> Success(a)
        }

        operator fun <A> invoke(a: A? = null, message: String): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> Success(a)
        }

        operator fun <A> invoke(a: A? = null, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> if (p(a)) Success(a) else Empty
        }

        operator fun <A> invoke(a: A? = null, message: String, p: (A) -> Boolean): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> when {
                (p(a)) -> Success(a)
                else -> Failure(IllegalStateException("Argument $a does not match condition: $message"))
            }
        }

        fun <A> failure(message: String): Result<A> = Failure(IllegalStateException(message))
        fun <A> failure(exception: RuntimeException): Result<A> = Failure(exception)
        fun <A> failure(exception: Exception): Result<A> = Failure(IllegalStateException(exception))
    }

    fun getOrElse(defaultValue: @UnsafeVariance A): A = when (this) {
        is Success -> this.value
        else -> defaultValue
    }

    fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> = when (this) {
        is Success -> this
        else ->
            try {
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