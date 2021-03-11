package model.interpolators

class DichotomyInterpolator {

    fun findExtremes(func: (arg: Double) -> Double, l: Double, r: Double, stepVal: Double): Pair<Double, Double> {
        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE
        var l = l
        while (l < r) {
            val fnL = func(l)
            if (fnL > max) {
                max = fnL
            }
            if (fnL < min) {
                min = fnL
            }
            l += stepVal
        }
        return Pair(max, min)
    }
}