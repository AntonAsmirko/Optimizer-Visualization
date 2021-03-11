import androidx.compose.desktop.Window
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.PlotData
import kotlin.math.max
import kotlin.math.pow

val buttonsText = listOf("Brent", "Dichotomy", "Fibonacci", "Golden Section", "Parabolas")

fun main() = Window {
    MaterialTheme {
        WithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .background(color = Color(0xff795548))
                        .fillMaxHeight()
                        .weight(0.2f)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .background(color = Color(0xff4b2c20))
                            .fillMaxHeight().border(border = BorderStroke(1.dp, color = Color.White))
                    ) {
                        buttonsText.forEach { buttonInBox(it) }
                    }
                }
                Row(
                    modifier = Modifier
                        .background(color = Color(0xff795548))
                        .fillMaxHeight()
                        .weight(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    drawPlot(100, -5.0, 5.0, constraints.maxHeight) {
                        it.pow(2.0)
                    }
                }
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
fun Circle(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp
) {
    Box(
        modifier = modifier.composed {
            preferredSize(size)
                .clip(CircleShape)
                .background(color)
        }
    )
}

@Composable
fun plotPiece(value: Double, fnVal: Double, minVal: Double, maxVal: Double, preferredHeight: Int) {
    val size = maxVal - minVal
    val fnValDiff = fnVal - minVal
    val half = with(AmbientDensity.current) { (preferredHeight / 2.0).dp }
    val prefHDp = with(AmbientDensity.current) { max((preferredHeight * fnValDiff / size), minVal).dp }
    val circleRadius = with(AmbientDensity.current) { (preferredHeight / 200.0).dp }
    Column {
        Column(
            modifier = Modifier
                .weight(0.9f)
                .background(color = Color(0xff795548))
        ) {
            if (prefHDp < half) {
                //Box(modifier = Modifier.height(half).width(circleRadius).background(color = Color.Red))
                Box(modifier = Modifier.height(prefHDp).width(circleRadius).background(color = Color.Yellow))
                Circle(color = Color(0xff32cb00), size = circleRadius)
                Box(modifier = Modifier.height(10.dp /*half - prefHDp*/).width(circleRadius).background(color = Color.Red))
                Box(modifier = Modifier.background(color = Color.Black).width(circleRadius).height(50.dp))
            } else {
                Box(modifier = Modifier.height(half).width(circleRadius).background(color = Color.Yellow))
                Box(modifier = Modifier.background(color = Color.Black).width(circleRadius).height(50.dp))
                Box(modifier = Modifier.height(prefHDp - half).width(circleRadius).background(color = Color.Red))
                Circle(color = Color(0xff32cb00), size = circleRadius)
            }
        }
        Column(
            modifier = Modifier
                .weight(0.1f)
                .background(color = Color(0xff4b2c20))
        ) {

        }
    }
}


@Composable
fun drawPlot(numOfBlobs: Int, l: Double, r: Double, preferredHeight: Int, func: (arg: Double) -> Double) {
    val step = (r - l) / numOfBlobs
    val extremes = findExtremes(func, l, r, step)
    var l = l
    for (i in 0 until numOfBlobs) {
        plotPiece(l, func(l ), extremes.second, extremes.first, preferredHeight)
        l += step
    }
}

@Composable fun plotView(plotData: PlotData){
    Canvas(modifier = Modifier  ,onDraw = {

    })
}

fun Canvas.prepareYAxis(minFnVal: Float, maxFnVal: Float, offset: Float, height: Float, lBound: Float, rBound: Float){
    this.translate(0f, height)
    this.scale(1f, -1f)
    this.translate(lBound, 0f)
    this.scale()
}