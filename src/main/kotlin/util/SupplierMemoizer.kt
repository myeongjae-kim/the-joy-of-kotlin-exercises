package util

class SupplierMemoizer<T> private constructor() {
    private var cache: T? = null

    private fun doMemoize(function: () -> T): () -> T = {
        if (cache == null) {
            cache = function()
        }
        cache!!
    }

    companion object {
        fun <T> memoize(function: () -> T): () -> T = SupplierMemoizer<T>().doMemoize(function)
    }
}
