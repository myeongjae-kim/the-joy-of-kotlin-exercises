package util

sealed class Tree<out A : Comparable<@kotlin.UnsafeVariance A>> {

    abstract fun isEmpty(): Boolean

    operator fun plus(element: @UnsafeVariance A): Tree<A> = when (this) {
        is Empty -> T(Empty, element, Empty)
        is T -> when {
            this.value < element -> T(left + element, this.value, right)
            this.value > element -> T(left, this.value, right + element)
            else -> T(left, element, right)
        }
    }

    internal object Empty : Tree<Nothing> () {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "E"
    }

    internal class T<out A : Comparable<@kotlin.UnsafeVariance A>> (
        internal val left: Tree<A>,
        internal val value: A,
        internal val right: Tree<A>,
    ) : Tree<A>() {
        override fun isEmpty(): Boolean = false

        override fun toString(): String = "(T $left $value $right)"
    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Tree<A> = Empty
    }
}
