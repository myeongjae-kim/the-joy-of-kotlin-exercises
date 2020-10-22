package chap5

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.List
import kotlin.test.assertEquals

class Ch5ExercisesTest {

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
            fun sum(list: List<Int>): Int = list.foldLeft(0, { x -> { y -> x + y } })
            fun product(list: List<Double>): Double = list.foldLeft(1.0, { x -> { y -> x * y } })

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
                list.foldRight("") { elem -> { acc -> "$elem, $acc" } },
                list.foldRightViaFoldLeft("") { elem -> { acc -> "$elem, $acc" } }
            )

            assert(
                list.foldRightViaFoldLeft("") { elem -> { acc -> "$elem, $acc" } }
                    != list.foldLeft("") { elem -> { acc -> "$elem, $acc" } }
            )
        }
    }

    @Nested
    inner class Ex13 {

        @Test
        fun solve() {
            val list: List<Int> = List(1, 2, 3)
            val f: (Int) -> (String) -> String = { elem -> { acc -> "$elem, $acc" } }

            assertEquals(
                list.foldRight("", f),
                list.foldRightViaFoldLeft("", f)
            )

            assertEquals(
                list.foldRightViaFoldLeft("", f),
                list.coFoldRight("", f)
            )
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
                List.concatViaFoldLeft(list1, list2).toString()
            )

            assertEquals(
                List.concatViaFoldLeft(list1, list2).toString(),
                List.concatViaFoldRight(list1, list2).toString()
            )

            assertEquals(
                List.concatViaFoldRight(list1, list2).toString(),
                List.concatViaCoFoldRight(list1, list2).toString()
            )
        }
    }

    @Nested
    inner class Ex15 {

        @Test
        fun solve() {
            val lists: List<List<Int>> = List(
                List(1, 2),
                List(3, 4),
                List(5, 6)
            )

            assertEquals(List.flatten(lists).toString(), "[1, 2, 3, 4, 5, 6, NIL]")
        }
    }

    @Nested
    inner class Ex16 {

        @Test
        fun solve() {
            fun triple(list: List<Int>): List<Int> = list.foldRight(List()) { elem -> { acc -> acc.cons(elem * 3) } }

            assertEquals(
                triple(List(1, 2, 3)).toString(),
                "[3, 6, 9, NIL]"
            )
        }
    }

    @Nested
    inner class Ex17 {

        @Test
        fun solve() {
            fun doubleToString(list: List<Double>): List<String> = list.foldRight(List()) { elem -> { acc -> acc.cons(elem.toString()) } }

            assertEquals(
                doubleToString(List(1.0, 2.0, 3.0)).toString(),
                "[1.0, 2.0, 3.0, NIL]"
            )
        }
    }

    @Nested
    inner class Ex18 {

        @Test
        fun solve() {
            assertEquals(
                List(1.0, 2.0, 3.0).map(Double::toString).toString(),
                "[1.0, 2.0, 3.0, NIL]"
            )
        }
    }

    @Nested
    inner class Ex19 {

        @Test
        fun solve() {
            assertEquals(
                List(1, 2, 3).filter { it != 2 }.toString(),
                "[1, 3, NIL]"
            )
        }
    }

    @Nested
    inner class Ex20 {

        @Test
        fun solve() {
            assertEquals(
                List(1, 2, 3).flatMap { i -> List(i, -i) }.toString(),
                "[1, -1, 2, -2, 3, -3, NIL]"
            )
        }
    }

    @Nested
    inner class Ex21 {

        @Test
        fun solve() {
            assertEquals(
                List(1, 2, 3).filterViaFlatMap { it != 2 }.toString(),
                "[1, 3, NIL]"
            )
        }
    }
}
