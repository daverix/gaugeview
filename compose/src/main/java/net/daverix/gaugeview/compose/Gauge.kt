package net.daverix.gaugeview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@Preview(
    showBackground = true,
    widthDp = 300,
    heightDp = 300
)
@Composable
private fun GaugePreview() {
    Gauge(
        value = 5f,
        startValue = -30,
        endValue = 50,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    )
}

@Composable
fun Gauge(
    value: Float,
    startValue: Int,
    endValue: Int,

    modifier: Modifier = Modifier,

    bigLineLength: Dp = 32.dp,
    mediumLineLength: Dp = 24.dp,
    smallLineLength: Dp = 16.dp,

    smallLineEvery: Int = 1,
    mediumLineEvery: Int = 5,
    bigLineEvery: Int = 10,

    showNumberEvery: Int = 10,
    degrees: Int = 270,
    gaugeRotation: Float = -90f,

    numberSize: TextUnit = 24.sp,
    smallLineStrokeWidth: Dp = 2.dp,
    mediumLineStrokeWidth: Dp = 3.dp,
    bigLineStrokeWidth: Dp = 4.dp,

    pointerColor: Color = Color.Red,

    numberColor: Color = Color.Black,
    smallLineColor: Color = Color.DarkGray,
    mediumLineColor: Color = Color.DarkGray,
    bigLineColor: Color = Color.DarkGray,

    negativeNumberColor: Color = Color.Blue,
    negativeSmallLineColor: Color = Color.Blue,
    negativeMediumLineColor: Color = Color.Blue,
    negativeBigLineColor: Color = Color.Blue
) {
    val values = (endValue - startValue) / 2
    val degreesPerStep = (degrees / 2f) / values

    BoxWithConstraints(modifier = modifier) {
        DrawLines(
            degreesPerStep = degreesPerStep,
            startValue = startValue,
            endValue = endValue,
            smallLineEvery = smallLineEvery,
            gaugeRotation = gaugeRotation,
            degrees = degrees,
            bigLineEvery = bigLineEvery,
            mediumLineEvery = mediumLineEvery,
            negativeBigLineColor = negativeBigLineColor,
            bigLineColor = bigLineColor,
            negativeMediumLineColor = negativeMediumLineColor,
            mediumLineColor = mediumLineColor,
            negativeSmallLineColor = negativeSmallLineColor,
            smallLineColor = smallLineColor,
            bigLineLength = bigLineLength,
            mediumLineLength = mediumLineLength,
            smallLineLength = smallLineLength,
            bigLineStrokeWidth = bigLineStrokeWidth,
            mediumLineStrokeWidth = mediumLineStrokeWidth,
            smallLineStrokeWidth = smallLineStrokeWidth
        )

        for (currentValue in startValue..endValue step showNumberEvery) {
            BasicText(
                text = currentValue.absoluteValue.toString(),
                style = TextStyle(
                    color = if (currentValue <= 0) negativeNumberColor else numberColor,
                    fontSize = numberSize
                ),
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val angle = getAngleFromValue(
                        value = currentValue.toFloat(),
                        degreesPerStep = degreesPerStep,
                        degrees = degrees,
                        startValue = startValue
                    )
                    val bigLineLengthInPx = bigLineLength.toPx()
                    val widthInPx = maxWidth.toPx()
                    val heightInPx = maxHeight.toPx()
                    val halfWidth = widthInPx / 2f
                    val halfHeight = heightInPx / 2f
                    val a = gaugeRotation + angle
                    val x = halfWidth - placeable.width / 2f - sin(a) * (halfWidth - bigLineLengthInPx*2)
                    val y = halfHeight - placeable.height / 2f - cos(a) * (halfHeight - bigLineLengthInPx*2)

                    layout(placeable.width, placeable.height) {
                        placeable.place(x.toInt(), y.toInt())
                    }
                }
            )
        }

        DrawPointer(
            degreesPerStep = degreesPerStep,
            mediumLineLength = mediumLineLength,
            value = value,
            pointerColor = pointerColor,
            startValue = startValue,
            degrees = degrees
        )
    }
}

@Composable
private fun DrawLines(
    degreesPerStep: Float,
    startValue: Int,
    endValue: Int,
    smallLineEvery: Int,
    gaugeRotation: Float,
    degrees: Int,
    bigLineEvery: Int,
    mediumLineEvery: Int,
    negativeBigLineColor: Color,
    bigLineColor: Color,
    negativeMediumLineColor: Color,
    mediumLineColor: Color,
    negativeSmallLineColor: Color,
    smallLineColor: Color,
    bigLineLength: Dp,
    mediumLineLength: Dp,
    smallLineLength: Dp,
    bigLineStrokeWidth: Dp,
    mediumLineStrokeWidth: Dp,
    smallLineStrokeWidth: Dp
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (value in startValue..endValue step smallLineEvery) {
            val angle = getAngleFromValue(
                value = value.toFloat(),
                degreesPerStep = degreesPerStep,
                degrees = degrees,
                startValue = startValue
            )

            val lineColor = when {
                value % bigLineEvery == 0 && value <= 0 -> negativeBigLineColor
                value % bigLineEvery == 0 -> bigLineColor
                value % mediumLineEvery == 0 && value <= 0 -> negativeMediumLineColor
                value % mediumLineEvery == 0 -> mediumLineColor
                value <= 0 -> negativeSmallLineColor
                else -> smallLineColor
            }
            val lineLength = when {
                value % bigLineEvery == 0 -> bigLineLength.toPx()
                value % mediumLineEvery == 0 -> mediumLineLength.toPx()
                else -> smallLineLength.toPx()
            }
            val strokeWidth = when {
                value % bigLineEvery == 0 -> bigLineStrokeWidth.toPx()
                value % mediumLineEvery == 0 -> mediumLineStrokeWidth.toPx()
                else -> smallLineStrokeWidth.toPx()
            }
            val pivot = center

            withTransform({
                rotate(clampDegrees(gaugeRotation + angle), pivot)
            }) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width - lineLength, pivot.y),
                    end = Offset(size.width, pivot.y),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Composable
private fun DrawPointer(
    degreesPerStep: Float,
    mediumLineLength: Dp,
    value: Float,
    pointerColor: Color,
    degrees: Int,
    startValue: Int
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val pointerRadiusCenter = 16.dp.toPx()
        val pointerRadiusTop = 2.dp.toPx()
        val pointerTop = size.height / 2 - mediumLineLength.toPx() - 4.dp.toPx()
        val pointerRadiusBottom = 8.dp.toPx()
        val pointerBottom = size.height / 4

        val pointerMiddle = Path().apply {
            addOval(
                Rect(
                    left = -pointerRadiusCenter,
                    top = -pointerRadiusCenter,
                    right = pointerRadiusCenter,
                    bottom = pointerRadiusCenter
                )
            )
        }
        val pointer = Path().apply {
            moveTo(-pointerRadiusTop, -pointerTop)
            lineTo(0f, -pointerTop - pointerRadiusTop)
            lineTo(pointerRadiusTop, -pointerTop)
            lineTo(pointerRadiusBottom, pointerBottom)
            lineTo(-pointerRadiusBottom, pointerBottom)
            lineTo(-pointerRadiusTop, -pointerTop)
        }
        val fullPointer = Path().apply {
            op(pointerMiddle, pointer, PathOperation.Union)
        }

        val angle = getAngleFromValue(
            value = value,
            degreesPerStep = degreesPerStep,
            degrees = degrees,
            startValue = startValue
        )

        val pivot = center
        val middleDotSize = 16.dp.toPx()

        withTransform({
            rotate(angle, pivot)
            translate(pivot.x, pivot.y)
        }) {
            drawPath(fullPointer, color = pointerColor)

            drawOval(
                color = Color.White,
                topLeft = Offset(-middleDotSize / 2f, -middleDotSize / 2f),
                size = Size(middleDotSize, middleDotSize)
            )
        }
    }
}

private fun getAngleFromValue(
    value: Float,
    degreesPerStep: Float,
    degrees: Int,
    startValue: Int
): Float {
    return -(degrees / 2f) + degreesPerStep * (value - startValue)
}

private fun clampDegrees(degrees: Float) = when {
    degrees < 0 -> degrees + 360
    degrees > 360 -> degrees - 360
    else -> degrees
}

