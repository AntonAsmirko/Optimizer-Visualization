package model

data class PlotData(
    var lBound: Float,
    var rBound: Float,
    var points: List<Point>,
    var minFnVal: Float,
    var maxFnVal: Float
)

data class Point(var x: Float, var y: Float)