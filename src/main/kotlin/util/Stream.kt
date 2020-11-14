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

    abstract fun <B> foldRight(identity: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B

    fun <B> map(f: (A) -> B): Stream<B> = this.foldRight(Lazy { invoke() }) { elem ->
        { acc ->
            cons(Lazy { f(elem) }, acc)
        }
    }

    fun <B> flatMap(f: (A) -> Stream<B>): Stream<B> = this.foldRight(Lazy { invoke() }) { elem ->
        { acc ->
            f(elem).append(acc)
        }
    }

    fun filter(p: (A) -> Boolean): Stream<A> = this.foldRight(Lazy { invoke() }) { elem ->
        { acc ->
            if (p(elem)) {
                cons(Lazy { elem }, acc)
            } else {
                acc()
            }
        }
    }

    fun takeWhileViaFoldRight(p: (A) -> Boolean): Stream<A> {
        return this.foldRight(Lazy { Empty }) { elem: A ->
            { acc: Lazy<Stream<A>> ->
                if (p(elem))
                    cons(Lazy {elem}, acc)
                else
                    Empty
            }
        }
    }

    fun headSafeViaFoldRight(): Result<A> = foldRight(Lazy { Result() }) { elem ->
        { Result(elem) }
    }

    fun append(s: Lazy<Stream<@UnsafeVariance A>>): Stream<A> =
            this.foldRight(s) { elem ->
                { acc ->
                    cons(Lazy {elem}, acc)
                }
            }

    private object Empty: Stream<Nothing>() {
        override fun head(): Result<Nothing> = Result()
        override fun tail(): Result<Nothing> = Result()
        override fun isEmpty(): Boolean = true

        override fun takeAtMost(n: Int): Stream<Nothing> = this
        override fun dropAtMost(n: Int): Stream<Nothing> = this
        override fun toList(): List<out Nothing> = List()

        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this
        override fun dropWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this

        override fun <B> foldRight(identity: Lazy<B>, f: (Nothing) -> (Lazy<B>) -> B): B  = identity()
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

        override fun <B> foldRight(identity: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B =
                f(this.hd())(Lazy { this.tl().foldRight(identity, f) })
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
