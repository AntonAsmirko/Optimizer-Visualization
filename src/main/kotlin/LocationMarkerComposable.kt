@file:Suppress("NAME_SHADOWING")

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import org.jetbrains.skija.*

val font = Font(Typeface.makeDefault(), 24f)

fun Canvas.drawLocationMarker(
    path: Path,
    paint: Paint,
    originX: Float,
    originY: Float,
    circleRadius: Float,
    scaleX: Float,
    scaleY: Float,
    height: Float,
    lBound: Float,
    minValue: Float
) {
    save()
    scale(1 / scaleX, -1 / scaleY)
    translate(0f, -height)

    val c = 0.551915024494f
    val mDifference = circleRadius * c

    val mData = arrayOf(
        originX,
        originY,

        originX + circleRadius,
        originY - circleRadius * 2,

        originX,
        originY - circleRadius * 3,

        originX - circleRadius,
        originY - circleRadius * 2
    )

    val mCtrl = arrayOf(
        mData[0] + mDifference,
        mData[1],

        mData[2],
        mData[3] + mDifference,

        mData[0] - mDifference,
        mData[1],
        mData[6],
        mData[7] + mDifference
    )
    translate(originX, originY)
    path.reset()
    drawPath(
        path.apply {
            val arcRect = Rect(
                mData[6],
                mData[5],
                mData[2],
                mData[3] + circleRadius
            )
            moveTo(mData[0], mData[1])
            cubicTo(
                mCtrl[0],
                mCtrl[1],
                mCtrl[2],
                mCtrl[3],
                mData[2],
                mData[3]
            )
            arcTo(
                arcRect,
                0f,
                -90f,
                false
            )
            arcTo(
                arcRect,
                -90f,
                -90f,
                false
            )
            cubicTo(
                mCtrl[6],
                mCtrl[7],
                mCtrl[4],
                mCtrl[5],
                mData[0],
                mData[1]
            )
            close()
        },
        paint
    )

    nativeCanvas
        .drawText(
            originX,
            originY,
            circleRadius,
            (originX / scaleX + lBound / 2) * 2,
            0f
        )
    nativeCanvas
        .drawText(
            originX,
            originY,
            circleRadius,
            (originY / scaleY + minValue / 2) * 2,
            20f
        )

    path.reset()
    restore()
}

fun NativeCanvas.drawText(
    originX: Float,
    originY: Float,
    circleRadius: Float,
    translatedVal: Float,
    yOffset: Float
) {
    val startX = -circleRadius / 2
    val startY = -circleRadius * 2
    var curPos = startX
    val textItems = font
        .getStringGlyphs(translatedVal.toString())
        .map {
            val res = Pair(it, Point(curPos, startY))
            curPos += font.size * 2 / 3
            res
        }
    drawTextBlob(
        TextBlob.makeFromPos(
            textItems
                .map { it.first }
                .toShortArray(),
            textItems
                .map { it.second }
                .toTypedArray(),
            font
        ),
        originX,
        originY + yOffset,
        font,
        org.jetbrains.skija.Paint().apply {
            color = 0xff000000.toInt()
        }
    )
}