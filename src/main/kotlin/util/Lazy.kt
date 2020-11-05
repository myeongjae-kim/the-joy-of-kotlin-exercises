package util

class Lazy <A>(f: () -> A) {
    private val value: A by lazy(f)

    operator fun invoke(): A = value

    companion object {
        fun <A, B, C> lift2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> =
                { a ->
                    { b ->
                        Lazy { f(a())(b()) }
                    }
                }
    }
}


