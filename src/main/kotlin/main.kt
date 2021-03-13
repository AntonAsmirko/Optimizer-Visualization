import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.unit.dp
import model.PlotData
import model.interpolators.FunctionInterpolator
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.*

object Constants {
    const val POINT_RADIUS = 0.005f
    const val TITLE = "Optimizer Visualizer"
    const val NONE_FUNC = "NONE"
    const val BACK_BUTTON = "BACK"
    const val SUBMIT = "ok"
}

enum class LeftViewType {
    METHOD,
    FUNCTION,
    FUNCTION_INPUT
}

enum class MethodType {
    BRENT,
    DICHOTOMY,
    FIBONACCI,
    GOLDEN_SECTION,
    PARABOLAS
}

val methodsButtonsText = listOf(
    Pair("Brent", MethodType.BRENT),
    Pair("Dichotomy", MethodType.DICHOTOMY),
    Pair("Fibonacci", MethodType.FIBONACCI),
    Pair("Golden Section", MethodType.GOLDEN_SECTION),
    Pair("Parabolas", MethodType.PARABOLAS)
)

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

val appImg: BufferedImage = ImageIO.read(File("./img/appIcon.png"))

fun main() = Window(title = Constants.TITLE, icon = appImg) {
    MaterialTheme {
        var leftViewType by remember { mutableStateOf(LeftViewType.METHOD) }
        var func by remember { mutableStateOf(Constants.NONE_FUNC) }
        var numBlobs by remember { mutableStateOf("") }
        var lBound by remember { mutableStateOf("") }
        var rBound by remember { mutableStateOf("") }
        val functionInterpolator by remember { mutableStateOf(FunctionInterpolator()) }
        var functionDrawingPermitted by remember { mutableStateOf(false) }
        var message: String? = null
        var points: List<Offset>?
        Row(
            modifier = Modifier
                .background(color = Color(0xff795548))
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xff4b2c20))
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(0.2f)
                    .border(border = BorderStroke(1.dp, color = Color.White))
                    .verticalScroll(rememberScrollState())
            ) {
                when (leftViewType) {
                    LeftViewType.METHOD -> {
                        methodsButtonsText.forEach {
                            buttonInBox(it.first) {
                                leftViewType = it
                            }
                        }
                    }
                    LeftViewType.FUNCTION -> {
                        buttonInBox(Constants.BACK_BUTTON) {
                            leftViewType = LeftViewType.METHOD
                            func = Constants.NONE_FUNC
                        }
                        functionsButtonsText.forEach { pair ->
                            buttonInBox(pair.key) {
                                func = pair.key
                                leftViewType = LeftViewType.FUNCTION_INPUT
                            }
                        }
                    }
                    LeftViewType.FUNCTION_INPUT -> {
                        buttonInBox(Constants.BACK_BUTTON) {
                            leftViewType = LeftViewType.FUNCTION
                            func = Constants.NONE_FUNC
                            functionDrawingPermitted = false
                            numBlobs = ""
                            lBound = ""
                            rBound = ""
                        }
                        fieldSpacer()
                        Text(text = func)
                        fieldSpacer()
                        OutlinedTextField(
                            value = "",
                            inactiveColor = Color(0xff64dd17),
                            activeColor = Color(0xff1faa00),
                            onValueChange = {
                                lBound += it
                            },

                            label = { Text(lBound) })
                        fieldSpacer()
                        OutlinedTextField(
                            value = "",
                            inactiveColor = Color(0xff64dd17),
                            activeColor = Color(0xff1faa00),
                            onValueChange = {
                                rBound += it
                            },
                            label = { Text(rBound) })
                        fieldSpacer()
                        OutlinedTextField(
                            value = "",
                            inactiveColor = Color(0xff64dd17),
                            activeColor = Color(0xff1faa00),
                            onValueChange = {
                                numBlobs += it
                            },
                            label = { Text(numBlobs) }
                        )
                        fieldSpacer()
                        buttonInBox(Constants.SUBMIT) {
                            functionDrawingPermitted = validateInput(lBound, rBound, numBlobs)
                        }
                    }
                }
            }
            if (func != Constants.NONE_FUNC && functionDrawingPermitted) {
                var cursorPosition by remember { mutableStateOf<Offset?>(null) }
                WithConstraints(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .pointerMoveFilter(onMove = {
                            cursorPosition = it
                            true
                        })

                ) {
                    val boxWidth = constraints.maxWidth
                    val boxHeight = constraints.maxHeight
                    val res =
                        functionInterpolator.makePoints(
                            lBound.toFloat(),
                            rBound.toFloat(),
                            numBlobs.toInt(),
                            functionsButtonsText[func]!!
                        )
                    message = res.first
                    points = res.second
                    if (message == FunctionInterpolator.MESSAGE_OK) {
                        val minFnVal: Float = points!!.minByOrNull { it.y }?.y ?: -100f
                        val maxFnVal = points!!.maxByOrNull { it.y }?.y ?: 100f
                        val samplePlotData =
                            PlotData(lBound.toFloat(), rBound.toFloat(), points!!, minFnVal, maxFnVal)
                        plotView(samplePlotData, boxHeight, boxWidth, cursorPosition)
                    } else {
                        textCentred(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .weight(0.8f),
                            message!!
                        )
                    }
                }
            } else {
                textCentred(
                    Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.8f),
                    "Something will be drawn here after you chose method and function"
                )
            }
        }
    }
}

@Composable
fun fieldSpacer() {
    Spacer(modifier = Modifier.padding(7.dp))
}

fun validateInput(lBound: String, rBound: String, numOfBlobs: String): Boolean {
    return true
}

@Composable
fun textCentred(
    modifier: Modifier,
    message: String
) {
    Column(modifier) {
        Text(
            modifier = Modifier
                .padding(30.dp),
            text = message
        )
    }
}

@Composable
fun buttonInBox(
    text: String,
    onChange: (type: LeftViewType) -> Unit
) {
    val active = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { onChange(LeftViewType.FUNCTION) },
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
fun plotView(
    plotData: PlotData,
    height: Int,
    width: Int,
    cursorPosition: Offset?
) {
    val paint by remember { mutableStateOf(Paint()) }
    val path by remember { mutableStateOf(Path()) }
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        onDraw = {
        this.drawContext.canvas.apply {
            save()
            val (scaleX, scaleY) = prepareAxis(
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
                scaleX,
                scaleY
            )
            drawPoints(
                plotData.points,
                Constants.POINT_RADIUS,
                plotData.lBound,
                plotData.minFnVal,
                scaleX,
                scaleY,
                paint
            )
            cursorPosition?.let {
                drawLocationMarker(
                    path,
                    paint,
                    it.x / 2,
                    it.y / 2,
                    50f,
                    scaleX,
                    scaleY,
                    height.toFloat()
                )
            }
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

fun Float.closestPowOfTwo(): Float = 2f.pow(round(log2(this)))