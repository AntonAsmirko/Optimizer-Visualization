package extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint

fun Canvas.drawCurrentStep(
    steps: ArrayList<ArrayList<Double>>,
    currentStep: Int,
    scaleX: Float,
    scaleY: Float
) {
    save()


}

fun Canvas.drawGrid(
    paint: Paint,
    height: Float,
    width: Float,
    scaleX: Float,
    scaleY: Float
) {
    save()
    scale(1 / scaleX, -1 / scaleY)
    val stepX = 30f
    val stepY = 30f
    paint.apply {
        color = Color(0xff1faa00)
        strokeWidth = 1f
    }
    var curHeight = 0f
    while (curHeight < height) {
        drawLine(Offset(0f, 0f + curHeight), Offset(width, 0f + curHeight), paint)
        if (curHeight > 0f)
            drawLine(Offset(0f, 0f + -curHeight), Offset(width, -curHeight), paint)
        curHeight += stepY
    }
    paint.strokeWidth = 1f
    var curWidth = 0f
    while (curWidth < width) {
        drawLine(Offset(curWidth, height), Offset(curWidth, -1 * height), paint)
        curWidth += stepX
    }
    restore()
}

fun Canvas.drawPoints(
    points: List<Offset>,
    radius: Float,
    lBound: Float,
    minFnVal: Float,
    scaleX: Float,
    scaleY: Float,
    paint: Paint
) {
    save()
    scale(1 / scaleX, 1 / scaleY)
    val strokeWidthConst = 5f
    val sorted = points.sortedWith(Comparator { o1, o2 ->
        return@Comparator when {
            o1.x < o2.x -> -1
            o1.x == o2.x -> 0
            else -> 1
        }
    }).toTypedArray()
    var prevOffset: Offset? = null
    paint.apply {
        color = Color(0xff64dd17)
        strokeWidth = strokeWidthConst
    }
    for (i in sorted.indices) {
        val curOffset = Offset((sorted[i].x - lBound) * scaleX, (sorted[i].y - minFnVal) * scaleY)
        this.drawCircle(curOffset, radius, paint)
        if (prevOffset != null) {
            this.drawLine(prevOffset, curOffset, paint)
        }
        prevOffset = curOffset
    }
    restore()
}

fun Canvas.prepareAxis(
    minFnVal: Float,
    maxFnVal: Float,
    height: Float,
    width: Float,
    lBound: Float,
    rBound: Float
): Pair<Float, Float> {
    val scaleX = width / (rBound - lBound)
    val scaleY = height / (maxFnVal - minFnVal)
    translate(0f, height)
    scale(scaleX, -scaleY)
    return Pair(scaleX, scaleY)
}