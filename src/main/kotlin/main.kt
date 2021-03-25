import Constants.BORDER_WIDTH
import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import composables.buttonInBox
import composables.fieldSpacer
import composables.plotView
import composables.textCentred
import firstLab.*
import model.PlotData
import model.interpolators.FunctionInterpolator
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val appImg: BufferedImage = ImageIO.read(File("./img/appIcon.png"))

val darkColors = darkColors(
    primary = Color(0xff37474f),
    primaryVariant = Color(0xff62727b),
    secondary = Color(0xffb2ff59),
    background = Color(0xff102027),
    surface = Color(0xff62727b),
    onPrimary = Color.White,
    onSecondary = Color.Black
)

fun main() = Window(
    title = Constants.TITLE,
    icon = appImg,
) {
    MaterialTheme(
        colors = darkColors
    ) {
        var leftViewType by remember { mutableStateOf(LeftViewType.METHOD) }
        var func by remember { mutableStateOf(Constants.NONE_FUNC) }
        var numBlobs by remember { mutableStateOf("") }
        var lBound by remember { mutableStateOf("") }
        var rBound by remember { mutableStateOf("") }
        val functionInterpolator by remember { mutableStateOf(FunctionInterpolator()) }
        var functionDrawingPermitted by remember { mutableStateOf(false) }
        var stepSize by remember { mutableStateOf("") }
        var currentOptimizerStep by remember { mutableStateOf(0) }
        var message: String? = null
        var points: List<Offset>?
        var optimizer by remember { mutableStateOf<Optimizer?>(null) }

        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.background)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(0.2f)
                    .border(
                        border = BorderStroke(
                            BORDER_WIDTH,
                            color = MaterialTheme.colors.primary
                        )
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                when (leftViewType) {
                    LeftViewType.METHOD -> {
                        methodsButtonsText.forEach { p ->
                            buttonInBox(
                                p.first,
                                MaterialTheme.colors
                            ) {
                                leftViewType = it
                                //TODO delete useless logger
                                optimizer = getOptimizer[p.first]?.invoke(Logger())
                            }
                        }
                    }
                    LeftViewType.FUNCTION -> {
                        buttonInBox(
                            Constants.BACK_BUTTON,
                            MaterialTheme.colors
                        ) {
                            leftViewType = LeftViewType.METHOD
                            func = Constants.NONE_FUNC
                        }
                        functionsButtonsText.forEach { pair ->
                            buttonInBox(
                                pair.key,
                                MaterialTheme.colors
                            ) {
                                func = pair.key
                                leftViewType = LeftViewType.FUNCTION_INPUT
                            }
                        }
                    }
                    LeftViewType.FUNCTION_INPUT -> {
                        buttonInBox(
                            Constants.BACK_BUTTON,
                            MaterialTheme.colors
                        ) {
                            leftViewType = LeftViewType.FUNCTION
                            func = Constants.NONE_FUNC
                            functionDrawingPermitted = false
                            numBlobs = ""
                            lBound = ""
                            rBound = ""
                        }
                        fieldSpacer()
                        Text(
                            modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                            text = func,
                            style = TextStyle(color = MaterialTheme.colors.onSurface)
                        )
                        fieldSpacer()
                        OutlinedTextField(
                            value = lBound,
                            inactiveColor = MaterialTheme.colors.secondary,
                            activeColor = MaterialTheme.colors.surface,
                            onValueChange = {
                                lBound = it
                            },
                            label = { Text("enter left bound of the function") },
                            textStyle = TextStyle(color = MaterialTheme.colors.onSurface)
                        )
                        fieldSpacer()
                        OutlinedTextField(
                            value = rBound,
                            inactiveColor = MaterialTheme.colors.secondary,
                            activeColor = MaterialTheme.colors.surface,
                            onValueChange = {
                                rBound = it
                            },
                            label = { Text("enter right bound of function") },
                            textStyle = TextStyle(color = MaterialTheme.colors.onSurface)
                        )
                        fieldSpacer()
                        OutlinedTextField(
                            value = numBlobs,
                            inactiveColor = MaterialTheme.colors.secondary,
                            activeColor = MaterialTheme.colors.surface,
                            onValueChange = {
                                numBlobs = it
                            },
                            label = { Text("enter number of blobs to be taken") },
                            textStyle = TextStyle(color = MaterialTheme.colors.onSurface)
                        )
                        fieldSpacer()
                        buttonInBox(
                            Constants.SUBMIT,
                            MaterialTheme.colors
                        ) {
                            functionDrawingPermitted = validateInput(lBound)
                                    && validateInput(rBound)
                                    && validateInput(numBlobs)
                                    && lBound.toFloat() < rBound.toFloat()
                        }
                        if (functionDrawingPermitted) {
                            fieldSpacer()
                            Text(text = "Step (num iterations)")
                            fieldSpacer()
                            OutlinedTextField(
                                value = stepSize,
                                inactiveColor = MaterialTheme.colors.secondary,
                                activeColor = MaterialTheme.colors.surface,
                                onValueChange = {
                                    stepSize = it
                                },
                                label = { Text("enter step size") },
                                textStyle = TextStyle(color = MaterialTheme.colors.onSurface)
                            )
                            fieldSpacer()
                            buttonInBox(
                                Constants.NEXT_ITERATION,
                                MaterialTheme.colors
                            ) {
                                functionDrawingPermitted = validateInput(stepSize)
                                if (functionDrawingPermitted) {
                                    currentOptimizerStep += stepSize.toInt()
                                }
                            }
                            fieldSpacer()
                            buttonInBox(
                                Constants.PREV_ITERATION,
                                MaterialTheme.colors
                            ) {
                                functionDrawingPermitted = validateInput(stepSize)
                                if (functionDrawingPermitted) {
                                    currentOptimizerStep -= stepSize.toInt()
                                }
                            }
                        }
                    }
                }
            }
            if (func != Constants.NONE_FUNC && functionDrawingPermitted) {
                var cursorPosition by remember { mutableStateOf<Offset?>(null) }
                var clickPermitted by remember { mutableStateOf(false) }
                WithConstraints(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .pointerMoveFilter(onMove = {
                            cursorPosition = it
                            true
                        }, onExit = {
                            cursorPosition = null
                            true
                        }).clickable {
                            clickPermitted = !clickPermitted
                        }

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
                        optimizer?.run {
                            val optimizerResult = optimize(
                                samplePlotData.lBound.toDouble(),
                                samplePlotData.rBound.toDouble(),
                                Constants.TMP_EPS,
                                floatFnToDouble(functionsButtonsText[func]!!)
                            )
                            plotView(
                                samplePlotData,
                                boxHeight,
                                boxWidth,
                                cursorPosition,
                                clickPermitted,
                                MaterialTheme.colors,
                                array
                                    .map {
                                        it.map { p ->
                                            val xFlt = p.first.toFloat()
                                            Optimizer.Pair<Float>(
                                                p.first.toFloat(),
                                                functionsButtonsText[func]!!.invoke(xFlt)
                                            )
                                        }
                                    },
                                currentOptimizerStep
                            )
                        } ?: textCentred(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .weight(0.8f),
                            Constants.MESSAGE_TROUBLES_WITH_OPTIMIZER
                        )
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
                    Constants.HINT_CHOOSE_FUNCTION
                )
            }
        }
    }
}

private fun validateInput(num: String): Boolean {
    return true
}