package util

class Lazy <out A>(f: () -> A) : () -> A {
    private val value: A by lazy(f)

    override operator fun invoke(): A = value

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

        fun <A> sequence(list: List<Lazy<A>>): Lazy<List<A>> = Lazy {
            list.map { it() }
        }

        fun <A> sequenceResult(list: List<Lazy<A>>): Lazy<Result<List<A>>> = Lazy {
            Result.sequence(list.map { Result.of(it) })
        }
    }
}
