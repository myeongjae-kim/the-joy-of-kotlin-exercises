package util

import java.lang.RuntimeException

sealed class List<A> {
    abstract fun isEmpty(): Boolean
    abstract fun lengthMemoized(): Int
    abstract fun headSafe(): Result<A>

    internal object Nil : List<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "[NIL]"
        override fun lengthMemoized(): Int = 0
        override fun headSafe(): Result<Nothing> = Result()
    }

    // Cons means Construct.
    internal class Cons<A>(
        val head: A,
        val tail: List<A>
    ) : List<A>() {
        private val length = tail.lengthMemoized() + 1

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        override fun lengthMemoized(): Int = length

        override fun headSafe(): Result<A> = Result(head)

        private tailrec fun toString(acc: String, list: List<A>): String = when (list) {
            is Nil -> acc
            is Cons -> toString("$acc${list.head}, ", list.tail)
        }
    }

    fun cons(elem: A): List<A> = cons(this, elem)
    fun setHead(elem: A): List<A> = setHead(this, elem)
    fun drop(n: Int): List<A> = drop(this, n)
    fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(this, p)
    fun concat(list: List<A>): List<A> = concat(this, list)
    fun reverse(): List<A> = reverse(this)
    fun init(): List<A> = init(this)

    fun <B> foldRight(identity: B, f: (A) -> (B) -> B) = foldRight(this, identity, f)
    fun length(): Int = foldLeft(0) { { _ -> it + 1 } }

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B) = foldLeft(identity, this, f)

    fun <B> foldRightViaFoldLeft(identity: B, f: (A) -> (B) -> B) =
        this.reverse().foldLeft(identity, { b -> { a -> f(a)(b) } })

    fun <B> coFoldRight(identity: B, f: (A) -> (B) -> B): B =
        Companion.coFoldRight(identity, this.reverse(), identity, f)

    fun <B> map(f: (A) -> B) = map(this, f)
    fun filter(p: (A) -> Boolean) = filter(this, p)
    fun <B> flatMap(f: (A) -> List<B>) = flatMap(this, f)
    fun filterViaFlatMap(p: (A) -> Boolean) = filterViaFlatMap(this, p)

    companion object {

        @Suppress("UNCHECKED_CAST")
        operator
        fun <A> invoke(vararg az: A): List<A> =
            az.foldRight(Nil as List<A>, { elem, acc -> Cons(elem, acc) })

        fun <A> cons(list: List<A>, elem: A): List<A> = Cons(elem, list)

        fun <A> setHead(list: List<A>, elem: A): List<A> = when (list) {
            Nil -> throw RuntimeException("cannot setHead of empty list.")
            is Cons -> list.tail.cons(elem)
        }

        tailrec fun <A> drop(list: List<A>, n: Int): List<A> = if (n == 0) list else when (list) {
            Nil -> list
            is Cons<A> -> drop(list.tail, n - 1)
        }

        tailrec fun <A> dropWhile(list: List<A>, p: (A) -> Boolean): List<A> = when (list) {
            Nil -> list
            is Cons -> if (p(list.head)) dropWhile(list.tail, p) else list
        }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = when (list1) {
            Nil -> list2
            is Cons -> concat(list1.tail, list2).cons(list1.head)
        }

        // efficient but not stack-safe
        fun <A> concatViaFoldRight(list1: List<A>, list2: List<A>): List<A> =
            list1.foldRight(list2, { elem -> { acc -> acc.cons(elem) } })

        // stack-safe
        fun <A> concatViaCoFoldRight(list1: List<A>, list2: List<A>): List<A> =
            list1.coFoldRight(list2, { elem -> { acc -> acc.cons(elem) } })

        fun <A> concatViaFoldLeft(list1: List<A>, list2: List<A>): List<A> =
            list1.reverse().foldLeft(list2, { acc -> { elem -> acc.cons(elem) } })

        // my implementation. Not corecursive and not reusing objects...
        fun <A> myInit(list: List<A>): List<A> = when (list) {
            Nil -> list
            is Cons<A> ->
                if (list.tail == Nil)
                    list.tail
                else
                    Cons(list.head, myInit(list.tail))
        }

        fun <A> reverse(list: List<A>): List<A> =
            list.foldLeft(invoke(), { acc -> { elem -> acc.cons(elem) } })

        fun <A> init(list: List<A>): List<A> = list.reverse().drop(1).reverse()

        fun <A, B> foldRight(list: List<A>, identity: B, f: (A) -> (B) -> B): B {
            return when (list) {
                Nil -> identity
                is Cons<A> -> f(list.head)(foldRight(list.tail, identity, f))
            }
        }

        tailrec fun <A, B> foldLeft(acc: B, list: List<A>, f: (B) -> (A) -> B): B = when (list) {
            Nil -> acc
            is Cons<A> -> foldLeft(f(acc)(list.head), list.tail, f)
        }

        private tailrec fun <A, B> coFoldRight(acc: B, list: List<A>, identity: B, f: (A) -> (B) -> B): B = when (list) {
            Nil -> acc
            is Cons<A> -> coFoldRight(f(list.head)(acc), list.tail, identity, f)
        }

        fun <A> flatten(lists: List<List<A>>): List<A> =
            lists.foldLeft(invoke()) { acc -> acc::concat }

        fun <A, B> map(list: List<A>, f: (A) -> B): List<B> = list.coFoldRight(invoke(), { elem -> { acc -> acc.cons(f(elem)) } })

        fun <A> filter(list: List<A>, p: (A) -> Boolean): List<A> = list.coFoldRight(
            invoke(),
            { elem ->
                { acc ->
                    if (p(elem)) acc.cons(elem)
                    else acc
                }
            }
        )

        fun <A, B> flatMap(list: List<A>, f: (A) -> List<B>): List<B> = flatten(list.map(f))

        fun <A> filterViaFlatMap(list: List<A>, p: (A) -> Boolean): List<A> =
            list.flatMap { if (p(it)) List(it) else invoke() }
    }
}