import firstLab.*

val methodsButtonsText = Constants.let {
    listOf(
        Pair(it.BRENT, MethodType.BRENT),
        Pair(it.DICHOTOMY, MethodType.DICHOTOMY),
        Pair(it.FIBONACCI, MethodType.FIBONACCI),
        Pair(it.GOLDEN_SECTION, MethodType.GOLDEN_SECTION),
        Pair(it.PARABOLAS, MethodType.PARABOLAS)
    )
}

val getOptimizer = Constants.let {
    mapOf(
        Pair(it.BRENT, fun(l: Logger): Optimizer { return Brent(l) }),
        Pair(it.DICHOTOMY, fun(l: Logger): Optimizer { return Dichotomy(l) }),
        Pair(it.GOLDEN_SECTION, fun(l: Logger): Optimizer { return GoldenSection(l) }),
        Pair(it.FIBONACCI, fun(l: Logger): Optimizer { return Fibonacci(l) }),
        Pair(it.PARABOLAS, fun(l: Logger): Optimizer { return Parabolas(l) })
    )
}

val floatFnToDouble: (fn: (Float) -> Float) -> ((Double) -> Double) = { fn ->
    {
        fn(it.toFloat()).toDouble()
    }
}