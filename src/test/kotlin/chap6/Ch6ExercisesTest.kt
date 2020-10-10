package chap6

import chap5.Ch5ExercisesTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.collections.List as ListStandard
import chap5.Ch5ExercisesTest.List

class Ch6ExercisesTest {

    sealed class Option<out A> {

        abstract fun isEmpty(): Boolean

        abstract fun <B> map(f: (A) -> B): Option<B>

        fun getOrElse(default: @UnsafeVariance A): A = when(this) {
            None -> default
            is Some -> this.value
        }

        fun getOrElse(default: () -> @UnsafeVariance A): A = when(this) {
            None -> default()
            is Some -> this.value
        }

        fun <B> flatMap(f: (A) -> Option<B>): Option<B> = this.map(f).getOrElse(None)

        fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> = map { this }.getOrElse(default)

        fun filter(p: (A) -> Boolean): Option<A> = flatMap { if (p(it)) this else None }

        internal object None: Option<Nothing>() {

            override fun isEmpty(): Boolean = true

            override fun toString(): String = "None"

            override fun equals(other: Any?): Boolean = other === None

            override fun hashCode(): Int = 0

            override fun <B> map(f: (Nothing) -> B): Option<B> = None
        }

        internal data class Some<out A>(internal val value: A): Option<A>() {

            override fun isEmpty() = false

            override fun <B> map(f: (A) -> B): Option<B> = Some(f(this.value))
        }

        companion object {

            operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
                null -> None
                else -> Some(a)
            }
        }
    }

    @Nested
    inner class Ex01 {
        @Test
        fun solve() {
            assertEquals(Option(1).getOrElse(0), 1)
            assertEquals(Option<Int>().getOrElse(0), 0)
        }
    }

    fun <T: Comparable<T>> max(list: ListStandard<T>): Option<T> = Option(list.maxByOrNull { it })

    fun getDefault(): Int = throw RuntimeException()

    @Nested
    inner class Ex02 {
        @Test
        fun solve() {
            val max1 = max(listOf(3, 5, 7, 2, 1)).getOrElse(::getDefault)
            assertEquals(max1, 7)

            val exception = assertThrows<RuntimeException> { max(listOf<Int>()).getOrElse(::getDefault) }
            assertEquals(exception.javaClass, RuntimeException::class.java)
        }
    }

    @Nested
    inner class Ex03 {

        @Test
        fun solve() {
            assertEquals(Option<Double>().map(Double::toString), Option())
            assertEquals(Option(1.0).map(Double::toString).getOrElse { throw RuntimeException() }, "1.0")
        }
    }

    @Nested
    inner class Ex04 {

        @Test
        fun solve() {
            val f: (Double) -> Option<String> = { it -> Option(it.toString()) }

            assertEquals(Option(1.0).flatMap(f).getOrElse(""), "1.0")
            assertEquals(Option<Double>().flatMap(f).getOrElse(""), "")
        }
    }

    @Nested
    inner class Ex05 {

        @Test
        fun solve() {
            assertEquals(Option(1.0).orElse { Option(0.0) }.getOrElse(-1.0).toString(), "1.0")
            assertEquals(Option<Double>().orElse { Option(0.0) }.getOrElse(-1.0).toString(), "0.0")
        }
    }

    @Nested
    inner class Ex06 {

        @Test
        fun solve() {
            assertEquals(Option(1).filter { it == 1 }.getOrElse(0), 1)
            assertEquals(Option(1).filter { it != 1 }.getOrElse(0), 0)
        }
    }

    @Nested
    inner class Ex07 {
        // my implementation
        // ListStandard<T> is alias of kotlin standard List<T>.
        val myVariance: (ListStandard<Double>) -> Option<Double> = {
            Option(it)
                    .filter { l -> l.isNotEmpty() }
                    .map { l -> l.sum() / l.size }
                    .map { m -> it.fold(0.0, {acc, x -> (x - m).pow(2.0) + acc}) / it.size }
        }

        // book's implementation
        val mean: (ListStandard<Double>) -> Option<Double> = { list ->
            when {
                list.isEmpty() -> Option()
                else -> Option(list.sum() / list.size)
            }
        }

        val variance: (ListStandard<Double>) -> Option<Double> = { list ->
            mean(list).flatMap { m ->
                mean(list.map { x -> (x - m).pow(2) })
            }
        }

        @Test
        fun solve() {
            assertEquals(myVariance(listOf(1.0, 2.0, 3.0)), variance(listOf(1.0, 2.0, 3.0)))
        }
    }

    fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = {
        try {
            it.map(f)
        } catch (e: Exception) {
            Option()
        }
    }

    @Nested
    inner class Ex08 {

        @Test
        fun solve() {
            val f: (Int) -> String = { it.toString() }
            val lifted = lift(f)

            val a = Option(1)
            val b = lifted(a)

            assertEquals(b.getOrElse(""), "1")
        }
    }

    @Nested
    inner class Ex09 {

        @Test
        fun solve() {
            val f: (Int) -> String = { throw RuntimeException() }
            val lifted = lift(f)

            val a = Option(1)
            val b = lifted(a)

            assertEquals(b.getOrElse(""), "")
        }
    }

    fun <A, B, C> myMap2(a: Option<A>, b: Option<B>, f: (A) -> (B) -> C): Option<C> =
            a.map(f).flatMap { b.map(it) }

    // 매개변수들을 closure로 몽땅 잡은 다음에 가장 안쪽에 있는 함수에서 f를 콜한다.
    // 함수형 프로그래밍의 전형적인 패턴.
    // 이런식으로 작성하면 map3, map4 등도 쉽게 작성 가능함
    fun <A, B, C> map2(oa: Option<A>, ob: Option<B>, f: (A) -> (B) -> C): Option<C> =
            oa.flatMap { a -> ob.map { b -> f(a)(b) } }

    fun <A, B, C, D> map3(
            oa: Option<A>,
            ob: Option<B>,
            oc: Option<C>,
            f: (A) -> (B) -> (C) -> D
    ): Option<D> = oa.flatMap { a -> ob.flatMap { b -> oc.map { c -> f(a)(b)(c) } } }

    fun <A, B, C, D, E> map4(
            oa: Option<A>,
            ob: Option<B>,
            oc: Option<C>,
            od: Option<D>,
            f: (A) -> (B) -> (C) -> (D) -> E
    ): Option<E> = oa.flatMap { a -> ob.flatMap { b -> oc.flatMap { c -> od.map { d -> f(a)(b)(c)(d) } } } }

    @Nested
    inner class Ex10 {

        @Test
        fun solve() {
            val a: Option<Int> = Option(1)
            val b: Option<Double> = Option(2.0)
            val f: (Int) -> (Double) -> String = {x -> {y -> (x + y).toString() }}

            assertEquals(map2(a, b, f).getOrElse(""), "3.0")
        }
    }

    fun <A> sequence(list: List<Option<A>>): Option<List<A>> = traverse(list) { it }

    @Nested
    inner class Ex11 {

        @Test
        fun solve() {
            val list1 = List(Option(1), Option(2), Option(3))
            val list2 = List(Option(1), Option(), Option(3))

            assertEquals(sequence(list1).getOrElse { throw RuntimeException() }.toString(), "[1, 2, 3, NIL]")
            assertEquals(sequence(list2), Option())
        }
    }

    // None의 map을 호출하면 map의 매개변수에 어떤 함수가 들어오든지 상관없이 함수를 호출하지 않고 None객체가 다시 나온다.
    // 따라서 아래 매개변수 list에 하나라도 None객체가 있으면 결과가 None이 된다.
    fun <A, B> traverse(list: List<A>, f: (A) -> Option<B>): Option<List<B>> =
            list.foldRight(Option(List())) { x ->
                { y: Option<List<B>> ->
                    map2(f(x), y) { a ->
                        { b: List<B> -> b.cons(a) }
                    }
                }
            }

    // 돌겠네.. Ex11, Ex12 어려움.
    @Nested
    inner class Ex12 {

        @Test
        fun solve() {
            val list1 = List(Option(1), Option(2), Option(3))
            val list2 = List(Option(1), Option(), Option(3))

            val f: (Option<Int>) -> Option<Int> = { elem -> elem.map { it + 10 } }

            assertEquals(traverse(list1, f).getOrElse { throw RuntimeException() }.toString(), "[11, 12, 13, NIL]")
            assertEquals(traverse(list2, f), Option())
        }
    }

}
