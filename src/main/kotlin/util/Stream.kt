package util

sealed class Stream<out A>{
    abstract fun isEmpty(): Boolean
    abstract fun head(): Result<A>
    abstract fun tail(): Result<Stream<A>>

    private object Empty: Stream<Nothing>() {
        override fun head(): Result<Nothing> = Result()
        override fun tail(): Result<Nothing> = Result()
        override fun isEmpty(): Boolean = true
    }

    private class Cons<out A>(
            val hd: Lazy<A>,
            val tl: Lazy<Stream<A>>
    ): Stream<A>() {
        override fun head(): Result<A> = Result(hd())
        override fun tail(): Result<Stream<A>> = Result(tl())
        override fun isEmpty(): Boolean = true
    }

    companion object {
        fun <A> cons(
                hd: Lazy<A>,
                tl: Lazy<Stream<A>>
        ): Stream<A> = Cons(hd, tl)

        operator fun <A> invoke(): Stream<A> = Empty

        fun from(i: Int): Stream<Int> = cons(Lazy { i }, Lazy { from (i + 1) } )

        fun <A> repeat(f: () -> A): Stream<A> = cons(Lazy { f() }, Lazy { repeat(f) })
    }
}
