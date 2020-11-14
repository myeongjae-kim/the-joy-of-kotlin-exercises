package util

sealed class Stream<out A>{
    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<A>
    abstract fun tail(): Result<Stream<A>>

    abstract fun takeAtMost(n: Int): Stream<A>
    abstract fun dropAtMost(n: Int): Stream<A>
    abstract fun toList(): List<out A>

    abstract fun takeWhile(p: (A) -> Boolean): Stream<A>
    abstract fun dropWhile(p: (A) -> Boolean): Stream<A>

    fun exists(p: (A) -> Boolean): Boolean = exists(this, p)

    private object Empty: Stream<Nothing>() {
        override fun head(): Result<Nothing> = Result()
        override fun tail(): Result<Nothing> = Result()
        override fun isEmpty(): Boolean = true

        override fun takeAtMost(n: Int): Stream<Nothing> = this
        override fun dropAtMost(n: Int): Stream<Nothing> = this
        override fun toList(): List<out Nothing> = List()

        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this
        override fun dropWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this
    }

    private class Cons<out A>(
            val hd: Lazy<A>,
            val tl: Lazy<Stream<A>>
    ): Stream<A>() {
        override fun head(): Result<A> = Result(hd())
        override fun tail(): Result<Stream<A>> = Result(tl())
        override fun isEmpty(): Boolean = false

        override fun takeAtMost(n: Int): Stream<A> = if (n <= 0) {
            Empty
        } else {
            Cons(hd, Lazy { tl().takeAtMost(n - 1)} )
        }

        override fun dropAtMost(n: Int): Stream<A> = dropAtMost(n, this)

        override fun toList(): List<out A> = toList(this)

        override fun takeWhile(p: (A) -> Boolean): Stream<A> =
                if (p(hd()))
                    cons(hd, Lazy{ tl().takeWhile(p) })
                else
                    Empty

        override fun dropWhile(p: (A) -> Boolean): Stream<A> = dropWhile(this, p)
    }

    companion object {
        fun <A> cons(
                hd: Lazy<A>,
                tl: Lazy<Stream<A>>
        ): Stream<A> = Cons(hd, tl)

        operator fun <A> invoke(): Stream<A> = Empty

        fun from(i: Int): Stream<Int> = iterate(i) { it + 1 }

        fun <A> iterate(seed: A, f: (A) -> A): Stream<A> = cons(Lazy { seed }, Lazy { iterate(f(seed), f) })

        fun <A> repeat(f: () -> A): Stream<A> = cons(Lazy { f() }, Lazy { repeat(f) })

        tailrec fun <A> dropAtMost(n: Int, stream: Stream<A>): Stream<A> = when {
            n > 0 -> when (stream) {
                is Cons -> dropAtMost(n - 1, stream.tl())
                else -> stream
            }
            else -> stream
        }

        fun <A> toList(stream: Stream<A>): List<A> {
            tailrec fun <A> toList(stream: Stream<A>, list: List<A>): List<A> {
                return when (stream) {
                    is Cons -> toList(stream.tl(), list.cons(stream.hd()))
                    Empty -> list
                }
            }

            return toList(stream, List()).reverse()
        }

        tailrec fun <A> dropWhile(s: Stream<A>, p: (A) -> Boolean): Stream<A> = when {
            s is Cons && p(s.hd()) -> dropWhile(s.tl(), p)
            else -> s
        }

        tailrec fun <A> exists(s: Stream<A>, p: (A) -> Boolean): Boolean = when (s) {
            is Cons -> if (p(s.hd())) true else exists(s.tl(), p)
            else -> false
        }
    }
}
