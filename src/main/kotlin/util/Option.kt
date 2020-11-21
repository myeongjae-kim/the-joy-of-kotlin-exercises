package util

sealed class Option<out A> {

    abstract fun isEmpty(): Boolean

    abstract fun <B> map(f: (A) -> B): Option<B>

    fun getOrElse(default: @UnsafeVariance A): A = when (this) {
        None -> default
        is Some -> this.value
    }

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        None -> default()
        is Some -> this.value
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = this.map(f).getOrElse(None)

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> = map { this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> = flatMap { if (p(it)) this else None }

    internal object None : Option<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "None"

        override fun equals(other: Any?): Boolean = other === None

        override fun hashCode(): Int = 0

        override fun <B> map(f: (Nothing) -> B): Option<B> = None
    }

    internal data class Some<out A>(internal val value: A) : Option<A>() {

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
