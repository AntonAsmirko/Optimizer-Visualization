@file:Suppress("NAME_SHADOWING")

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path

fun Canvas.drawLocationMarker(
    path: Path,
    paint: Paint,
    originX: Float,
    originY: Float,
    circleRadius: Float,
    scaleX: Float,
    scaleY: Float,
    height: Float
) {
    save()
    scale(1 / scaleX, -1 / scaleY)
    translate(0f, -height)

    val c = 0.551915024494f
    val mDifference = circleRadius * c

    val mData = arrayOf(
        originX,
        originY + circleRadius,

        originX + circleRadius,
        originY,

        originX,
        originY - circleRadius * 3,

        originX - circleRadius,
        originY
    )

    val mCtrl = arrayOf(
        mData[0] + mDifference,
        mData[1],

        mData[2],
        mData[3] + mDifference,

        mData[2],
        mData[3] - mDifference,

        mData[4] + mDifference,
        mData[5],

        mData[4] - mDifference,
        mData[5],

        mData[6],
        mData[7] - mDifference,

        mData[6],
        mData[7] + mDifference,

        mData[0] - mDifference,
        mData[1]
    )
    translate(originX, originY)
    scale(1f, 1f)
    path.reset()
    drawPath(
        path.apply {
            moveTo(mData[0], mData[1])
            lineTo((mData[2] - mData[6]) / 2f + mData[6], mData[1])
            arcTo(
                Rect(
                    mData[6],
                    mData[1] - 2f * circleRadius,
                    mData[2],
                    mData[1]
                ),
                90f,
                -90f,
                false
            )
            cubicTo(
                mCtrl[4],
                mCtrl[5],
                mCtrl[6],
                mCtrl[7],
                mData[4],
                mData[5]
            )
            cubicTo(
                mCtrl[8],
                mCtrl[9],
                mCtrl[10],
                mCtrl[11],
                mData[6],
                mData[7]
            )
            arcTo(
                Rect(
                    mData[6],
                    mData[1] - 2f * circleRadius,
                    mData[2],
                    mData[1],
                ),
                180f,
                -90f,
                false
            )
            close()
        },
        paint
    )
    restore()
}