import androidx.compose.desktop.Window
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.unit.dp
import model.PlotData
import model.interpolators.DichotomyInterpolator
import kotlin.math.pow

object Constants {
    const val POINT_RADIUS = 0.5f
    const val L_BOUND = -10f
    const val R_BOUND = 10f
    const val NUM_POINTS = 100
}

val buttonsText = listOf("Brent", "Dichotomy", "Fibonacci", "Golden Section", "Parabolas")

val dichotomyInterpolator = DichotomyInterpolator()
val points =
    dichotomyInterpolator.makePoints(Constants.L_BOUND.toDouble(), Constants.R_BOUND.toDouble(), Constants.NUM_POINTS) {
        it.pow(3.0)
    }
val maxFnVal = points.maxByOrNull { it.y }?.y ?: -100f
val minFnVal = points.minByOrNull { it.y }?.y ?: -1000f

val samplePlotData = PlotData(Constants.L_BOUND, Constants.R_BOUND, points, minFnVal, maxFnVal)

fun main() = Window {
    MaterialTheme {
        Row(
            modifier = Modifier
                .background(color = Color(0xff795548))
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .background(color = Color(0xff4b2c20))
                    .fillMaxHeight().border(border = BorderStroke(1.dp, color = Color.White))
            ) {
                buttonsText.forEach { buttonInBox(it) }
            }
            WithConstraints(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
            ) {
                val boxWidth = constraints.maxWidth
                val boxHeight = constraints.maxHeight
                plotView(samplePlotData, boxHeight, boxWidth)
            }
        }
    }
}

@Composable
fun buttonInBox(text: String) {
    val active = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, Color(0xff64dd17)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .pointerMoveFilter(
                    onEnter = {
                        active.value = true
                        false
                    },
                    onExit = {
                        active.value = false
                        false
                    }),
            colors = ButtonConstants.defaultButtonColors(
                backgroundColor = if (!active.value) Color(0xffa98274) else Color(
                    0xff76ff03
                )
            )
        ) {
            Text(text)
        }
    }
}

@Composable
fun plotView(plotData: PlotData, height: Int, width: Int) {
    val paint = remember { Paint() }
    Canvas(modifier = Modifier, onDraw = {
        this.drawContext.canvas.apply {
            save()
            val scale = prepareYAxis(
                plotData.minFnVal,
                plotData.maxFnVal,
                height.toFloat(),
                width.toFloat(),
                plotData.lBound,
                plotData.rBound
            )
            drawGrid(
                paint,
                height.toFloat(),
                width.toFloat(),
                scale.first,
                scale.second
            )
            drawPoints(plotData.points, Constants.POINT_RADIUS, plotData.lBound, plotData.minFnVal, scale.first, paint)
            restore()
        }
    })
}

fun Canvas.drawGrid(paint: Paint, height: Float, width: Float, scaleX: Float, scaleY: Float) {
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
    scale: Float,
    paint: Paint
) {
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
        strokeWidth = strokeWidthConst / scale
    }
    for (i in sorted.indices) {
        val curOffset = Offset(sorted[i].x - lBound, sorted[i].y - minFnVal)
        this.drawCircle(curOffset, radius / scale, paint)
        if (prevOffset != null) {
            this.drawLine(prevOffset, curOffset, paint)
        }
        prevOffset = curOffset
    }
}

fun Canvas.prepareYAxis(
    minFnVal: Float,
    maxFnVal: Float,
    height: Float,
    width: Float,
    lBound: Float,
    rBound: Float
): Pair<Float, Float> {
    val scaleX = width / (rBound - lBound)
    val scaleY = width / (maxFnVal - minFnVal)
    translate(0f, height)
    scale(scaleX, -scaleY)
    return Pair(scaleX, scaleY)
}