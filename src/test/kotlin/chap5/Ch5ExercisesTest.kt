package chap5

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import kotlin.test.assertEquals

class Ch5ExercisesTest {
    internal sealed class List<A> {
        abstract fun isEmpty(): Boolean

        internal object Nil : List<Nothing>() {
            override fun isEmpty(): Boolean = true
            override fun toString(): String = "[NIL]"
        }

        // Cons means Construct.
        internal class Cons<A>(
            val head: A,
            val tail: List<A>) : List<A>() {

            override fun isEmpty(): Boolean = false

            override fun toString(): String = "[${toString("", this)}NIL]"

            private tailrec fun toString(acc: String, list: List<A>): String = when(list) {
                is Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
        }

        fun cons(elem: A): List<A> = cons(this, elem)
        fun setHead(elem: A) : List<A> = setHead(this, elem)
        fun drop(n: Int): List<A> = drop(this, n)
        fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(this, p)
        fun concat(list: List<A>): List<A> = concat(this, list)
        fun reverse(): List<A> = reverse(this)
        fun init(): List<A> = init(this)

        fun head(): A = head(this)
        fun tail(): List<A> = tail(this)

        companion object {

            @Suppress("UNCHECKED_CAST")
            operator
            fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>, {elem, acc -> Cons(elem, acc)})

            fun <A> cons(list: List<A>, elem: A): List<A> = Cons(elem, list)

            fun <A> setHead(list: List<A>, elem: A) : List<A> = when(list) {
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

            fun <A> concat(list1: List<A>, list2: List<A>) : List<A> = when (list1) {
                Nil -> list2
                is Cons -> concat(list1.tail, list2).cons(list1.head)
            }

            // my implementation. Not corecursive...
            fun <A> myInit(list: List<A>) : List<A> = when(list) {
                Nil -> list
                is Cons<A> ->
                    if (list.tail == Nil)
                        list.tail
                    else
                        Cons(list.head, myInit(list.tail))
            }

            fun <A> reverse(list: List<A>): List<A> {
                tailrec fun <A> reverse(acc: List<A>, list: List<A>): List<A> = when(list) {
                    Nil -> acc
                    is Cons<A> -> reverse(acc.cons(list.head), list.tail)
                }

                @Suppress("UNCHECKED_CAST")
                return reverse(invoke(), list)
            }

            fun <A> init(list: List<A>): List<A> = list.reverse().drop(1).reverse()

            fun <A> head(list: List<A>): A = when(list) {
                Nil -> throw IllegalArgumentException("No head for empty list.")
                is Cons<A> -> list.head
            }

            fun <A> tail(list: List<A>): List<A> = when(list) {
                Nil -> list
                is Cons<A> -> list.tail
            }
        }
    }

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(list.cons(0).toString(), "[0, 1, 2, 3, NIL]")
            assertEquals(List.cons(list, 0).toString(), "[0, 1, 2, 3, NIL]")
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(list.setHead(0).toString(), "[0, 2, 3, NIL]")
            assertEquals(List.setHead(list, 0).toString(), "[0, 2, 3, NIL]")
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(list.drop(2).toString(), "[3, NIL]")
            assertEquals(List.drop(list, 2).toString(), "[3, NIL]")

            assertEquals(list.drop(100).toString(), "[NIL]")
            assertEquals(List.drop(list, 100).toString(), "[NIL]")
        }
    }

    @Nested
    inner class Ex04 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(list.dropWhile { it != 3 }.toString(), "[3, NIL]")
            assertEquals(List.dropWhile(list) { it != 3 }.toString(), "[3, NIL]")

            assertEquals(list.dropWhile { it != 100 }.toString(), "[NIL]")
            assertEquals(List.dropWhile(list) { it != 100 }.toString(), "[NIL]")
        }
    }

    @Nested
    inner class Concat {

        @Test
        fun solve() {
            val list1: List<Int> = List(1, 2, 3)
            val list2: List<Int> = List(4, 5, 6)

            assertEquals(list1.concat(list2).toString(), "[1, 2, 3, 4, 5, 6, NIL]")
            assertEquals(List.concat(list1, list2).toString(), "[1, 2, 3, 4, 5, 6, NIL]")
        }
    }

    @Nested
    inner class Ex05 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.reverse().toString(), "[3, 2, 1, NIL]")
            assertEquals(list.init().toString(), "[1, 2, NIL]")
            assertEquals(List.myInit(list).toString(), "[1, 2, NIL]")
        }
    }

    @Nested
    inner class Ex06 {
        private fun sum(ints: List<out Int>): Int {
            tailrec fun sum(acc: Int, ints: List<out Int>): Int = when (ints) {
                List.Nil -> acc
                is List.Cons -> sum(acc + ints.head, ints.tail)
            }

            return sum(0, ints)
        }

        @Test
        fun solve() {
            val list = List(1, 2, 3)

            assertEquals(sum(list), 6)
        }
    }
}
