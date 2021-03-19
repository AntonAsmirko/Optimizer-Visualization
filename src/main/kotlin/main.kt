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
import composables.buttonInBox
import composables.fieldSpacer
import composables.plotView
import composables.textCentred
import extensions.drawGrid
import extensions.drawPoints
import extensions.prepareAxis
import firstLab.*
import model.PlotData
import model.interpolators.FunctionInterpolator
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.*

val appImg: BufferedImage = ImageIO.read(File("./img/appIcon.png"))

fun main() = Window(
    title = Constants.TITLE,
    icon = appImg
) {
    MaterialTheme {
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
                        methodsButtonsText.forEach { p ->
                            buttonInBox(p.first) {
                                leftViewType = it
                                optimizer = getOptimizer[p.first]?.invoke(Logger())
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
                                value = "",
                                inactiveColor = Color(0xff64dd17),
                                activeColor = Color(0xff1faa00),
                                onValueChange = {
                                    stepSize += it
                                },
                                label = { Text(text = stepSize) }
                            )
                            fieldSpacer()
                            buttonInBox(Constants.NEXT_ITERATION) {
                                functionDrawingPermitted = validateInput(stepSize)
                                currentOptimizerStep += stepSize.toInt()
                            }
                            fieldSpacer()
                            buttonInBox(Constants.PREV_ITERATION) {
                                functionDrawingPermitted = validateInput(stepSize)
                                currentOptimizerStep -= stepSize.toInt()
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
                                optimizerResult.toFloat(),
                                array,
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