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

        fun cons(elem: A): List<A> = Cons(elem, this)

        fun setHead(elem: A) : List<A> = when(this) {
            Nil -> throw RuntimeException("cannot setHead of empty list.")
            is Cons -> tail.cons(elem)
        }

        fun drop(n: Int): List<A> {

            // my implementation
            @Suppress("UNCHECKED_CAST")
            tailrec fun myDrop(list: List<A>, n: Int): List<A> = when {
                list is Nil -> Nil as List<A>
                n <= 0 -> list
                else -> myDrop((list as Cons<A>).tail, n - 1)
            }

            // book's implementation
            tailrec fun drop(n: Int, list: List<A>): List<A> =
                if (n <= 0) list else when(list) {
                    is Nil -> list
                    is Cons -> drop(n - 1, list.tail)
                }

            return drop(n, this)
        }

        companion object {

            @Suppress("UNCHECKED_CAST")
            operator
            fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>, {elem, acc -> Cons(elem, acc)})
        }
    }

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)
            val newList = list.cons(0)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(newList.toString(), "[0, 1, 2, 3, NIL]")
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)
            val newList = list.setHead(0)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(newList.toString(), "[0, 2, 3, NIL]")
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)
            val newList = list.drop(2)

            assertEquals(list.toString(), "[1, 2, 3, NIL]")
            assertEquals(newList.toString(), "[3, NIL]")
            assertEquals(list.drop(100).toString(), "[NIL]")
        }
    }
}
