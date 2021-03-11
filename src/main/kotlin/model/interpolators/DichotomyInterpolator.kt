package model.interpolators

import androidx.compose.ui.geometry.Offset

class DichotomyInterpolator {

    fun makePoints(l: Double, r: Double, numPoints: Int ,func: (arg: Double) -> Double): List<Offset>{
        val points = mutableListOf<Offset>()
        val step = (r - l) / numPoints
        var cur = l
        while (cur < r){
            points.add(Offset(cur.toFloat(), func(cur).toFloat()))
            cur += step
        }
        return points
    }
}