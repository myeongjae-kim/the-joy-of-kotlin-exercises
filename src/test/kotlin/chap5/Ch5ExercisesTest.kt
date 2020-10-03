package chap5

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import kotlin.test.assertEquals

class Ch5ExercisesTest {
    sealed class List<A> {
        abstract fun isEmpty(): Boolean

        private object Nil : List<Nothing>() {
            override fun isEmpty(): Boolean = true
            override fun toString(): String = "[NIL]"
        }

        // Cons means Construct.
        private class Cons<A>(
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
                is Nil -> list
                is Cons<A> -> drop(list.tail, n - 1)
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
}
