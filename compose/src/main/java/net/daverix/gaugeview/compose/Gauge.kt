package net.daverix.gaugeview.compose

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.daverix.gaugeview.GaugeView

@Composable
fun Gauge(
    value: Float,
    startValue: Int,
    endValue: Int,

    modifier: Modifier = Modifier,

    bigLineLength: Float = 64f,
    mediumLineLength: Float = 48f,
    smallLineLength: Float = 32f,

    smallLineEvery: Int = 1,
    mediumLineEvery: Int = 5,
    bigLineEvery: Int = 10,

    showNumberEvery: Int = 10,
    degrees: Int = 270,
    gaugeRotation: Float = -90f,

    numberSize: Float = 48f,
    smallLineStrokeWidth: Float = 4f,
    mediumLineStrokeWidth: Float = 6f,
    bigLineStrokeWidth: Float = 8f,

    pointerColor: Int = Color.RED,

    numberColor: Int = Color.BLACK,
    smallLineColor: Int = Color.DKGRAY,
    mediumLineColor: Int = Color.DKGRAY,
    bigLineColor: Int = Color.DKGRAY,

    negativeNumberColor: Int = Color.BLUE,
    negativeSmallLineColor: Int = Color.BLUE,
    negativeMediumLineColor: Int = Color.BLUE,
    negativeBigLineColor: Int = Color.BLUE
) {
    AndroidView(
        factory = { GaugeView(it) },
        update = {
            it.startValue = startValue
            it.endValue = endValue

            it.bigLineLength = bigLineLength
            it.mediumLineLength = mediumLineLength
            it.smallLineLength = smallLineLength

            it.smallLineEvery = smallLineEvery
            it.mediumLineEvery = mediumLineEvery
            it.bigLineEvery = bigLineEvery

            it.showNumberEvery = showNumberEvery
            it.degrees = degrees
            it.gaugeRotation = gaugeRotation

            it.numberSize = numberSize
            it.smallLineStrokeWidth = smallLineStrokeWidth
            it.mediumLineStrokeWidth = mediumLineStrokeWidth
            it.bigLineStrokeWidth = bigLineStrokeWidth

            it.pointerColor = pointerColor

            it.numberColor = numberColor
            it.smallLineColor = smallLineColor
            it.mediumLineColor = mediumLineColor
            it.bigLineColor = bigLineColor

            it.negativeNumberColor = negativeNumberColor
            it.negativeSmallLineColor = negativeSmallLineColor
            it.negativeMediumLineColor = negativeMediumLineColor
            it.negativeBigLineColor = negativeBigLineColor
            it.value = value
        },
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    widthDp = 300,
    heightDp = 300
)
@Composable
fun GaugePreview() {
    Gauge(
        value = 5f,
        startValue = -30,
        endValue = 50,
        modifier = Modifier.fillMaxSize()
            .padding(all = 16.dp)
    )
}
