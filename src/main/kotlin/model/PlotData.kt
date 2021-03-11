package model

import androidx.compose.ui.geometry.Offset

data class PlotData(
    var lBound: Float,
    var rBound: Float,
    var points: List<Offset>,
    var minFnVal: Float,
    var maxFnVal: Float
)