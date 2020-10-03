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

        fun <B> foldRight(identity: B, f: (A) -> (B) -> B) = foldRight(this, identity, f)
        fun length(): Int = foldLeft(0) {  { _ -> it + 1} }

        fun <B> foldLeft(identity: B, f: (B) -> (A) -> B) = foldLeft(identity, this, f)

        fun <B> foldRightViaFoldLeft(identity: B, f: (A) -> (B) -> B) =
            this.reverse().foldLeft(identity, { b -> { a -> f(a)(b)}})

        fun <B> coFoldRight(identity: B, f: (A) -> (B) -> B): B =
            Companion.coFoldRight(identity, this.reverse(), identity, f)

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

            // efficient but not stack-safe
            fun <A> concatViaFoldRight(list1: List<A>, list2: List<A>) : List<A> =
                list1.foldRight(list2, {elem -> { acc -> acc.cons(elem) }})

            // stack-safe
            fun <A> concatViaCoFoldRight(list1: List<A>, list2: List<A>) : List<A> =
                list1.coFoldRight(list2, {elem -> { acc -> acc.cons(elem) }})

            fun <A> concatViaFoldLeft(list1: List<A>, list2: List<A>) : List<A> =
                list1.reverse().foldLeft(list2, { acc -> { elem -> acc.cons(elem) }})

            // my implementation. Not corecursive...
            fun <A> myInit(list: List<A>) : List<A> = when(list) {
                Nil -> list
                is Cons<A> ->
                    if (list.tail == Nil)
                        list.tail
                    else
                        Cons(list.head, myInit(list.tail))
            }

            fun <A> reverse(list: List<A>): List<A> =
                list.foldLeft(invoke(), { acc -> { elem -> acc.cons(elem) }})

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

    @Nested
    inner class Ex07 {
        private fun product(doubles: List<out Double>): Double {
            tailrec fun product(acc: Double, ints: List<out Double>): Double = when (ints) {
                List.Nil -> acc
                is List.Cons -> product(acc * ints.head, ints.tail)
            }

            return product(1.0, doubles)
        }

        @Test
        fun solve() {
            val list = List(1.0, 2.0, 3.0)

            assertEquals(product(list).toString(), "6.0")
        }
    }

    @Nested
    inner class Ex08 {

        @Test
        fun solve() {
            assertEquals(List("a", "b", "c").length(), 3)
        }
    }

    @Nested
    inner class Ex10 {

        @Test
        fun solve() {
            fun sum(list: List<Int>): Int = list.foldLeft(0, {x -> {y -> x + y}})
            fun product(list: List<Double>): Double = list.foldLeft(1.0, {x -> {y -> x * y}})

            assertEquals(sum(List(1, 2, 3)), 6)
            assertEquals(product(List(1.0, 2.0, 3.0)), 6.0)
            assertEquals(List(1, 2, 3).length(), 3)
        }
    }

    @Nested
    inner class Ex11 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(list.reverse().toString(), "[3, 2, 1, NIL]")
            assertEquals(list.init().toString(), "[1, 2, NIL]")
        }
    }


    @Nested
    inner class Ex12 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)

            assertEquals(
                list.foldRight("") {elem -> { acc -> "$elem, $acc" }},
                list.foldRightViaFoldLeft("") {elem -> { acc -> "$elem, $acc" }})

            assert(
                list.foldRightViaFoldLeft("") {elem -> { acc -> "$elem, $acc" }}
                        != list.foldLeft("") {elem -> { acc -> "$elem, $acc" }})
        }
    }

    @Nested
    inner class Ex13 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)
            val f: (Int) -> (String) -> String = { elem -> { acc -> "$elem, $acc" }}

            assertEquals(
                list.foldRight("", f),
                list.foldRightViaFoldLeft("", f))

            assertEquals(
                list.foldRightViaFoldLeft("", f),
                list.coFoldRight("", f))
        }
    }

    @Nested
    inner class Ex14 {

        @Test
        fun solve() {
            val list1: List<Int> = List(1, 2, 3)
            val list2: List<Int> = List(4, 5, 6)

            assertEquals(
                list1.concat(list2).toString(),
                List.concatViaFoldLeft(list1, list2).toString())

            assertEquals(
                List.concatViaFoldLeft(list1, list2).toString(),
                List.concatViaFoldRight(list1, list2).toString())

            assertEquals(
                List.concatViaFoldRight(list1, list2).toString(),
                List.concatViaCoFoldRight(list1, list2).toString())
        }
    }

    @Nested
    inner class Ex15 {

        @Test
        fun solve() {
            val lists: List<List<Int>> = List(
                List(1,2),
                List(3,4),
                List(5,6)
            )

            assertEquals(List.flatten(lists).toString(), "[1, 2, 3, 4, 5, 6, NIL]")
        }
    }

    @Nested
    inner class Ex16 {

        @Test
        fun solve() {
            fun triple(list: List<Int>): List<Int> = list.foldRight(List()) {elem -> {acc -> acc.cons(elem * 3)}}

            assertEquals(
                triple(List(1, 2, 3)).toString(),
                "[3, 6, 9, NIL]")
        }
    }

    @Nested
    inner class Ex17 {

        @Test
        fun solve() {
            fun doubleToString(list: List<Double>): List<String> = list.foldRight(List()) {elem -> {acc -> acc.cons(elem.toString())}}

            assertEquals(
                doubleToString(List(1.0, 2.0, 3.0)).toString(),
                "[1.0, 2.0, 3.0, NIL]")
        }
    }
}
