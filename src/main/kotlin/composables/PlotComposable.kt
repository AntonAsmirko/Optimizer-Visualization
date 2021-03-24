package composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import drawLocationMarker
import extensions.drawGrid
import extensions.drawPoints
import extensions.prepareAxis
import firstLab.Optimizer
import model.PlotData

@Composable
fun plotView(
    plotData: PlotData,
    height: Int,
    width: Int,
    cursorPosition: Offset?,
    cursorDrawingPermitted: Boolean,
    themeColors: Colors,
    optimizerResult: Float,
    allOptimizersSteps: ArrayList<ArrayList<Optimizer.Pair<Double>>>,
    currentStep: Int
) {
    val paint by remember {
        mutableStateOf(Paint()
            .apply {
                color = themeColors.surface
            })
    }
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
                cursorPosition?.run {
                    if (cursorDrawingPermitted)
                        drawLocationMarker(
                            path,
                            paint,
                            x / 2,
                            y / 2,
                            Constants.CURSOR_COORDINATES_CIRCLE_RADIUS,
                            scaleX,
                            scaleY,
                            height.toFloat(),
                            plotData.lBound,
                            plotData.minFnVal
                        )
                }
                restore()
            }
        })
}