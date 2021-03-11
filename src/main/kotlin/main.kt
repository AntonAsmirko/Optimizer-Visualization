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
    const val POINT_RADIUS = 4f
    const val L_BOUND = -50f
    const val R_BOUND = 0f
    const val NUM_POINTS = 100
}

val buttonsText = listOf("Brent", "Dichotomy", "Fibonacci", "Golden Section", "Parabolas")
val dichotomyInterpolator = DichotomyInterpolator()
val points =
    dichotomyInterpolator.makePoints(Constants.L_BOUND.toDouble(), Constants.R_BOUND.toDouble(), Constants.NUM_POINTS) {
        it.pow(2.0) * 0.05
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
    var active = remember { mutableStateOf(false) }
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
    val paint = remember {
        Paint()
            .apply {
                color = Color.Red
                strokeWidth = 1f
            }
    }
    Canvas(modifier = Modifier, onDraw = {
        this.drawContext.canvas.apply {
            prepareYAxis(
                plotData.minFnVal,
                plotData.maxFnVal,
                height.toFloat(),
                width.toFloat(),
                plotData.lBound,
                plotData.rBound
            )
            drawGrid(paint, height.toFloat(), width.toFloat())
            drawPoints(plotData.points, Constants.POINT_RADIUS, paint)
            scale(1.5f,1.5f)
        }
    })
}

fun Canvas.drawGrid(paint: Paint, height: Float, width: Float){
    val step = 50f
    paint.apply {
        color = Color(0xff1faa00)
        strokeWidth = 1f
    }
    var curHeight = height / 2
    while (curHeight > -1 * height / 2) {
        drawLine(Offset(0f, 0f + curHeight), Offset(width / 2f, 0f + curHeight), paint)
        drawLine(Offset(0f, 0f + curHeight), Offset(-1 * width / 2f, 0f + curHeight), paint)
        curHeight -= step
    }
    var curWidth = 0f
    while (curWidth < width / 2){
        drawLine(Offset(curWidth, height / 2), Offset(curWidth, -1 * height / 2), paint)
        drawLine(Offset(-curWidth, height / 2), Offset(-curWidth, -1 * height / 2), paint)
        curWidth += step
    }
}

fun Canvas.drawPoints(
    points: List<Offset>,
    radius: Float,
    paint: Paint
) {
    val sorted = points.sortedWith(Comparator { o1, o2 ->
        return@Comparator when {
            o1.x < o2.x -> -1
            o1.x == o2.x -> 0
            else -> 1
        }
    }).toTypedArray()
    var prevOffset: Offset? = null
    for (i in sorted.indices) {
        val curOffset = Offset(sorted[i].x, sorted[i].y)
        this.drawCircle(curOffset, radius, paint.apply {
            color = Color(0xff64dd17)
            strokeWidth = 7f
        })
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
) {
    this.translate(width / 2f - lBound, height / 2f)
    this.scale(1f, -1f)
    //this.translate(lBound, 0f)
    //this.scale((rBound - lBound) / width, (maxFnVal - minFnVal) / height)
}