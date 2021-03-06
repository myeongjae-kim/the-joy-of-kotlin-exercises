package chap10

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import util.List
import util.Tree
import kotlin.test.assertEquals

class Ch10ExerciseTest {

    @Nested
    inner class Ex01 {

        @Test
        fun solve() {
            val empty = Tree<Int>()
            val t1 = empty + 2
            val t2 = t1 + 1
            val t3 = t2 + 3

            val t4 = t3 + 2

            assertEquals("(T E 2 E)", t1.toString())
            assertEquals("(T (T E 1 E) 2 E)", t2.toString())
            assertEquals("(T (T E 1 E) 2 (T E 3 E))", t3.toString())
            assertEquals("(T (T E 1 E) 2 (T E 3 E))", t4.toString())
            assert(t3 !== t4)
        }
    }

    @Nested
    inner class Ex02 {

        @Test
        fun solve() {
            val tree1 = Tree(List(3, 1, 2))
            val tree2 = Tree(listOf(3, 1, 2))
            val tree3 = Tree(3, 1, 2)

            assertEquals("(T (T E 1 E) 2 (T E 3 E))", tree1.toString())
            assertEquals(tree1.toString(), tree2.toString())
            assertEquals(tree2.toString(), tree3.toString())
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            val tree = Tree(List(3, 1, 2))

            assert(tree.contains(1))
            assert(tree.contains(2))
            assert(tree.contains(3))
            assert(!tree.contains(4))
        }
    }

    @Nested
    inner class Ex04 {

        @Test
        fun solve() {
            val tree1 = Tree(List(3, 1, 2))
            assertEquals(3, tree1.size)
            assertEquals(1, tree1.height)

            val tree2 = Tree(List(2, 1, 3))
            assertEquals(3, tree2.size)
            assertEquals(2, tree2.height)
        }
    }

    @Nested
    inner class Ex05 {

        @Test
        fun solve() {
            val tree1 = Tree(List(3, 1, 2))
            assertEquals("Success(3)", tree1.max().toString())
            assertEquals("Success(1)", tree1.min().toString())

            val tree2 = Tree(List(2, 1, 3))
            assertEquals("Success(3)", tree2.max().toString())
            assertEquals("Success(1)", tree2.min().toString())

            assertEquals("Empty", Tree<Int>().max().toString())
            assertEquals("Empty", Tree<Int>().min().toString())
        }
    }
}
