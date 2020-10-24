package util

fun <A, S> unfold(s: S, getNext: (S) -> Option<Pair<A, S>>): List<A> {
    tailrec fun unfold(acc: List<A>, s: S): List<A> =
            when (val next = getNext(s)) {
                Option.None -> acc
                is Option.Some -> unfold(acc.cons(next.value.first), next.value.second)
            }

    return unfold(List(), s).reverse()
}

fun range(fromInclusive: Int, toExclusive: Int): List<Int> =
        unfold(fromInclusive) {
            if (it < toExclusive)
                Option(Pair(it, it + 1))
            else
                Option()
        }