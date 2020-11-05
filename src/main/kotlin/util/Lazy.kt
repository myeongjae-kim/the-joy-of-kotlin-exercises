package util

class Lazy <A>(f: () -> A) {
    private val value: A by lazy(f)

    operator fun invoke(): A = value

    fun <B> map(f: (A) -> B): Lazy<B> = map(this, f)

    fun <B> flatMap(f: (A) -> Lazy<B>): Lazy<B> = flatMap(this, f)

    companion object {
        fun <A, B, C> lift2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> =
                { a ->
                    { b ->
                        Lazy { f(a())(b()) }
                    }
                }

        fun <A, B> map(a: Lazy<A>, f: (A) -> B): Lazy<B> = Lazy {
            f(a())
        }

        fun <A, B> flatMap(a: Lazy<A>, f: (A) -> Lazy<B>): Lazy<B> = a.map(f)()
    }
}


