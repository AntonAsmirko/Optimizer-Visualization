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
        Pair(it.BRENT, fun(): Optimizer { return Brent() }),
        Pair(it.DICHOTOMY, fun(): Optimizer { return Dichotomy() }),
        Pair(it.GOLDEN_SECTION, fun(): Optimizer { return GoldenSection() }),
        Pair(it.FIBONACCI, fun(): Optimizer { return Fibonacci() }),
        Pair(it.PARABOLAS, fun(): Optimizer { return Parabolas() })
    )
}

val floatFnToDouble: (fn: (Float) -> Float) -> ((Double) -> Double) = { fn ->
    {
        fn(it.toFloat()).toDouble()
    }
}