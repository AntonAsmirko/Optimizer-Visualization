@file:Suppress("NAME_SHADOWING")

import androidx.compose.material.rememberBottomDrawerState
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
    scale(1f, 1f)
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
//            arcTo(
//                Rect(
//                    mData[6],
//                    mData[1] - 2f * circleRadius,
//                    mData[2],
//                    mData[1],
//                ),
//                180f,
//                -90f,
//                false
//            )
            close()
        },
        paint
    )
    drawCircle(Offset(originX, originY), 5f, paint)
    restore()
}