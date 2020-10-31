package util

class Lazy <A>(f: () -> A) {
    private val value: A by lazy(f)

    operator fun invoke(): A = value
}