package model.interpolators

import androidx.compose.ui.geometry.Offset
import java.lang.IllegalStateException

class FunctionInterpolator {

    companion object {
        const val MESSAGE_OK = "OK"
        const val MESSAGE_NAN = "Given function has break points at the given segment"
    }

    fun makePoints(
        l: Float,
        r: Float,
        numPoints: Int,
        func: (arg: Float) -> Float
    ): Pair<String, List<Offset>> {
        val points = mutableListOf<Offset>()
        val step = (r - l) / numPoints
        var cur = l
        while (cur < r) {
            val point = Offset(cur, func(cur))
            if (point.y.isNaN()) {
                return Pair(MESSAGE_NAN, points)
            }
            points.add(point)
            cur += step
        }
        return Pair(MESSAGE_OK, points)
    }
}