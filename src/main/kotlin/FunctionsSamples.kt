import kotlin.math.*

val functionsButtonsText = mapOf(
    Pair("y = tan(x)", fun(x: Float): Float { return tan(x) }),
    Pair("y = sin(x)", fun(x: Float): Float { return sin(x) }),
    Pair("y = log2(x)", fun(x: Float): Float { return log2(x) }),
    Pair("y = x^2 * cos(x)", fun(x: Float): Float { return x.pow(2f) * sin(x) }),
    Pair("x^2 + e^(-0.35 * x)", fun(x: Float): Float { return x.pow(2f) + exp(-0.35f * x) }),
    Pair("x^4 - 1.5 * atan(x)", fun(x: Float): Float { return x.pow(4f) - 1.5f * atan(x) }),
    Pair("x * sin(x) + 2 * cos(x)", fun(x: Float): Float { return x * sin(x) + 2f * cos(x) }),
    Pair(
        "-5 * x^5 + 4 * x^4 - 12 * x^3 + 11 * x^2 - 2 * x + 1",
        fun(x: Float): Float {
            return -5f * x.pow(5f) + 4f * x.pow(4f) - 12 * x.pow(3f) + 11f * x.pow(2f) - 2f * x + 1f
        }),
    Pair("log10(x - 2)^2 + log10(10 - x)^2 - x^2",
        fun(x: Float): Float { return log10(x - 2f).pow(2f) + log10(10f - x).pow(2f) - x.pow(.2f) })
)