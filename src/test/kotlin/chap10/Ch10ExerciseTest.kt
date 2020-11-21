package chap10

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
            assertEquals("(T E 2 (T E 1 E))", t2.toString())
            assertEquals("(T (T E 3 E) 2 (T E 1 E))", t3.toString())
            assertEquals("(T (T E 3 E) 2 (T E 1 E))", t4.toString())
            assert(t3 !== t4)
        }
    }
}
