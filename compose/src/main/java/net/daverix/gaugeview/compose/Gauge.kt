package net.daverix.gaugeview.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 400
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

@Immutable
data class LineStyle(
    val length: Dp,
    val strokeWidth: Dp,
    val color: Color
)

@Composable
fun Gauge(
    value: Float,
    startValue: Int,
    endValue: Int,

    modifier: Modifier = Modifier,

    smallLineEvery: Int = 1,
    mediumLineEvery: Int = 5,
    bigLineEvery: Int = 10,
    showNumberEvery: Int = 10,

    bigLineStyle: LineStyle = LineStyle(
        length = 32.dp,
        strokeWidth = 4.dp,
        color = Color.DarkGray
    ),
    negativeBigLineStyle: LineStyle = bigLineStyle.copy(
        color = Color.Blue
    ),

    mediumLineStyle: LineStyle = LineStyle(
        length = 24.dp,
        strokeWidth = 3.dp,
        color = Color.DarkGray
    ),
    negativeMediumLineStyle: LineStyle = mediumLineStyle.copy(
        color = Color.Blue
    ),

    smallLineStyle: LineStyle = LineStyle(
        length = 16.dp,
        strokeWidth = 2.dp,
        color = Color.DarkGray
    ),
    negativeSmallLineStyle: LineStyle = smallLineStyle.copy(
        color = Color.Blue
    ),

    degrees: Int = 270,
    gaugeRotation: Float = -90f,

    numberSize: TextUnit = 24.sp,
    numberPadding: Dp = bigLineStyle.length / 4f,

    pointerColor: Color = Color.Red,

    numberColor: Color = Color.Black,

    negativeNumberColor: Color = Color.Blue,

    displayIndicators: Boolean = startValue < 0,
    negativeIndicatorColor: Color = Color.Black,
    positiveIndicatorColor: Color = Color.Black,
    indicatorSize: TextUnit = numberSize,
    indicatorPadding: Dp = 4.dp
) {
    val values = (endValue - startValue) / 2
    val degreesPerStep = (degrees / 2f) / values

    BoxWithConstraints(modifier = modifier) {
        DrawLines(
            degreesPerStep = degreesPerStep,
            startValue = startValue,
            endValue = endValue,
            smallLineEvery = smallLineEvery,
            mediumLineEvery = mediumLineEvery,
            bigLineEvery = bigLineEvery,
            smallLineStyle = smallLineStyle,
            negativeSmallLineStyle = negativeSmallLineStyle,
            mediumLineStyle = mediumLineStyle,
            negativeMediumLineStyle = negativeMediumLineStyle,
            bigLineStyle = bigLineStyle,
            negativeBigLineStyle = negativeBigLineStyle,
            gaugeRotation = gaugeRotation,
            degrees = degrees,
        )

        DrawNumbers(
            startValue = startValue,
            endValue = endValue,
            showNumberEvery = showNumberEvery,
            negativeNumberColor = negativeNumberColor,
            numberColor = numberColor,
            numberSize = numberSize,
            numberPadding = numberPadding,
            degreesPerStep = degreesPerStep,
            degrees = degrees,
            bigLineLength = bigLineStyle.length,
            gaugeRotation = gaugeRotation,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            displayIndicators = displayIndicators,
            negativeIndicatorColor = negativeIndicatorColor,
            positiveIndicatorColor = positiveIndicatorColor,
            indicatorSize = indicatorSize,
            indicatorPadding = indicatorPadding
        )

        DrawPointer(
            degreesPerStep = degreesPerStep,
            mediumLineLength = mediumLineStyle.length,
            value = value,
            pointerColor = pointerColor,
            startValue = startValue,
            degrees = degrees
        )
    }
}

@Composable
private fun DrawNumbers(
    startValue: Int,
    endValue: Int,
    showNumberEvery: Int,
    negativeNumberColor: Color,
    numberColor: Color,
    numberSize: TextUnit,
    degreesPerStep: Float,
    degrees: Int,
    bigLineLength: Dp,
    gaugeRotation: Float,
    numberPadding: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
    displayIndicators: Boolean,
    negativeIndicatorColor: Color,
    positiveIndicatorColor: Color,
    indicatorSize: TextUnit,
    indicatorPadding: Dp
) {
    for (currentValue in startValue..endValue step showNumberEvery) {
        val angle = getAngleFromValue(
            value = currentValue.toFloat(),
            degreesPerStep = degreesPerStep,
            degrees = degrees,
            startValue = startValue
        )

        BasicText(
            text = currentValue.absoluteValue.toString(),
            style = TextStyle(
                color = if (currentValue <= 0) negativeNumberColor else numberColor,
                fontSize = numberSize
            ),
            modifier = Modifier.layoutTextAtAngle(
                angle = angle,
                padding = bigLineLength + numberPadding,
                gaugeRotation = gaugeRotation,
                maxWidth = maxWidth,
                maxHeight = maxHeight
            )
        )
    }

    if(displayIndicators) {
        BasicText(
            text = "-",
            style = TextStyle(
                color = negativeIndicatorColor,
                fontSize = indicatorSize
            ),
            modifier = Modifier.layoutTextAtAngle(
                angle = getAngleFromValue(
                    value = startValue.toFloat() - showNumberEvery/2f,
                    degreesPerStep = degreesPerStep,
                    degrees = degrees,
                    startValue = startValue
                ),
                padding = indicatorPadding,
                gaugeRotation = gaugeRotation,
                maxWidth = maxWidth,
                maxHeight = maxHeight
            )
        )

        BasicText(
            text = "+",
            style = TextStyle(
                color = positiveIndicatorColor,
                fontSize = indicatorSize
            ),
            modifier = Modifier.layoutTextAtAngle(
                angle = getAngleFromValue(
                    value = endValue.toFloat() + showNumberEvery/2f,
                    degreesPerStep = degreesPerStep,
                    degrees = degrees,
                    startValue = startValue
                ),
                padding = indicatorPadding,
                gaugeRotation = gaugeRotation,
                maxWidth = maxWidth,
                maxHeight = maxHeight
            )
        )
    }
}

private fun Modifier.layoutTextAtAngle(
    angle: Float,
    gaugeRotation: Float,
    padding: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val a = (angle - gaugeRotation) * Math.PI / 180
    val radius = maxOf(maxWidth, maxHeight).toPx() / 2f -
            padding.toPx() -
            placeable.measuredHeight / 2f

    val x = maxWidth.toPx() / 2f - placeable.measuredWidth / 2f - cos(a) * radius
    val y = maxHeight.toPx() / 2f - placeable.measuredHeight / 2f - sin(a) * radius

    layout(placeable.width, placeable.height) {
        placeable.place(x.toInt(), y.toInt())
    }
}

@Composable
private fun DrawLines(
    degreesPerStep: Float,
    startValue: Int,
    endValue: Int,
    smallLineEvery: Int,
    mediumLineEvery: Int,
    bigLineEvery: Int,
    gaugeRotation: Float,
    degrees: Int,
    smallLineStyle: LineStyle,
    negativeSmallLineStyle: LineStyle,
    mediumLineStyle: LineStyle,
    negativeMediumLineStyle: LineStyle,
    bigLineStyle: LineStyle,
    negativeBigLineStyle: LineStyle
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for (value in startValue..endValue step smallLineEvery) {
            val angle = getAngleFromValue(
                value = value.toFloat(),
                degreesPerStep = degreesPerStep,
                degrees = degrees,
                startValue = startValue
            )

            val lineStyle = when {
                value % bigLineEvery == 0 && value <= 0 -> negativeBigLineStyle
                value % bigLineEvery == 0 -> bigLineStyle
                value % mediumLineEvery == 0 && value <= 0 -> negativeMediumLineStyle
                value % mediumLineEvery == 0 -> mediumLineStyle
                value <= 0 -> negativeSmallLineStyle
                else -> smallLineStyle
            }
            val pivot = center

            withTransform({
                rotate(clampDegrees(gaugeRotation + angle), pivot)
            }) {
                drawLine(
                    color = lineStyle.color,
                    start = Offset(size.width - lineStyle.length.toPx(), pivot.y),
                    end = Offset(size.width, pivot.y),
                    strokeWidth = lineStyle.strokeWidth.toPx()
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
        val pointerHeight = maxOf(size.height, size.width)
        val pointerRadiusCenter = 16.dp.toPx()
        val pointerRadiusTop = 2.dp.toPx()
        val pointerTop = pointerHeight / 2 - mediumLineLength.toPx() - 4.dp.toPx()
        val pointerRadiusBottom = 8.dp.toPx()
        val pointerBottom = pointerHeight / 4

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

