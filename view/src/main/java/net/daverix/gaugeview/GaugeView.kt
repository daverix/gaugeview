/*
    Copyright 2018 David Laurell

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package net.daverix.gaugeview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import kotlin.math.absoluteValue

class GaugeView : View {
    private val linePaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
    }
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private var pointerPath: Path? = null
    private var pointerCenterPath: Path? = null
    private var innerWidth: Float = 0f
    private var innerHeight: Float = 0f
    private var currentWidth: Int = 0
    private var currentHeight: Int = 0

    var value: Float = 0f
        set(input) {
            if(input > endValue)
                throw IllegalArgumentException("value $input greater than max value $endValue")

            if(input < startValue)
                throw IllegalArgumentException("value $input lesser than max value $startValue")

            field = input
            invalidate()
        }

    var bigLineLength: Float = 64f
    var mediumLineLength: Float = 48f
    var smallLineLength: Float = 32f

    var smallLineEvery: Int = 1
    var mediumLineEvery: Int = 5
    var bigLineEvery: Int = 10

    var startValue: Int = -30
    var endValue: Int = 50
    var showNumberEvery: Int = 10
    var degrees: Int = 270
    var gaugeRotation: Float = -90f

    var numberSize: Float = 48f
    var smallLineStrokeWidth: Float = 4f
    var mediumLineStrokeWidth: Float = 6f
    var bigLineStrokeWidth: Float = 8f

    var pointerColor: Int = Color.RED

    var numberColor: Int = Color.BLACK
    var smallLineColor: Int = Color.DKGRAY
    var mediumLineColor: Int = Color.DKGRAY
    var bigLineColor: Int = Color.DKGRAY

    var negativeNumberColor: Int = Color.BLUE
    var negativeSmallLineColor: Int = Color.BLUE
    var negativeMediumLineColor: Int = Color.BLUE
    var negativeBigLineColor: Int = Color.BLUE

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr) {
        val arr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.GaugeView,
                defStyleAttr,
                defStyleRes)
        try {
            arr.apply {
                pointerColor = getInt(R.styleable.GaugeView_pointerColor, pointerColor)
                startValue = getInt(R.styleable.GaugeView_startValue, startValue)
                endValue = getInt(R.styleable.GaugeView_endValue, endValue)
                value = getFloat(R.styleable.GaugeView_value, value)
                showNumberEvery = getInt(R.styleable.GaugeView_showNumberEvery, showNumberEvery)
                degrees = getInt(R.styleable.GaugeView_arc, degrees)
                gaugeRotation = getFloat(R.styleable.GaugeView_rotation, gaugeRotation)

                smallLineLength = getDimension(R.styleable.GaugeView_smallLineLength, smallLineLength)
                mediumLineLength = getDimension(R.styleable.GaugeView_mediumLineLength, mediumLineLength)
                bigLineLength = getDimension(R.styleable.GaugeView_mediumLineLength, bigLineLength)

                smallLineEvery = getInt(R.styleable.GaugeView_smallLineEvery, smallLineEvery)
                mediumLineEvery = getInt(R.styleable.GaugeView_mediumLineEvery, mediumLineEvery)
                bigLineEvery = getInt(R.styleable.GaugeView_bigLineEvery, bigLineEvery)

                numberColor = getColor(R.styleable.GaugeView_numberColor, numberColor)
                negativeNumberColor = getColor(R.styleable.GaugeView_negativeNumberColor, negativeNumberColor)
                numberSize = getDimension(R.styleable.GaugeView_numberSize, numberSize)

                smallLineColor = getColor(R.styleable.GaugeView_smallLineColor, smallLineColor)
                negativeSmallLineColor = getColor(R.styleable.GaugeView_negativeSmallLineColor, negativeSmallLineColor)
                smallLineStrokeWidth = getDimension(R.styleable.GaugeView_smallLineStrokeWidth, smallLineStrokeWidth)

                mediumLineColor = getColor(R.styleable.GaugeView_mediumLineColor, mediumLineColor)
                negativeMediumLineColor = getColor(R.styleable.GaugeView_negativeMediumLineColor, negativeMediumLineColor)
                mediumLineStrokeWidth = getDimension(R.styleable.GaugeView_mediumLineStrokeWidth, mediumLineStrokeWidth)

                bigLineColor = getColor(R.styleable.GaugeView_bigLineColor, bigLineColor)
                negativeBigLineColor = getColor(R.styleable.GaugeView_negativeBigLineColor, negativeBigLineColor)
                bigLineStrokeWidth = getDimension(R.styleable.GaugeView_bigLineStrokeWidth, bigLineStrokeWidth)
            }
        } finally {
            arr.recycle()
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        currentWidth = width
        currentHeight = height

        innerWidth = currentWidth.toFloat() - paddingLeft - paddingRight
        innerHeight = currentHeight.toFloat() - paddingTop - paddingBottom
        pivotX = paddingLeft + innerWidth / 2
        pivotY = paddingTop + innerHeight / 2

        val pointerTop = innerHeight / 2 - mediumLineLength
        val pointerBottom = innerHeight / 4
        val pointerRadiusTop = 4f
        val pointerRadiusBottom = 16f
        val pointerRadiusCenter = 32f

        pointerPath = createPointerPath(pointerRadiusCenter,
                pointerRadiusTop,
                pointerTop,
                pointerRadiusBottom,
                pointerBottom)
        pointerCenterPath = createPointerCenterPath(pointerRadiusBottom)
    }

    private fun createPointerCenterPath(pointerRadiusBottom: Float) = Path().apply {
        addOval(RectF(-pointerRadiusBottom,
                -pointerRadiusBottom,
                pointerRadiusBottom,
                pointerRadiusBottom), Path.Direction.CW)
    }

    private fun createPointerPath(pointerRadiusCenter: Float,
                                  pointerRadiusTop: Float,
                                  pointerTop: Float,
                                  pointerRadiusBottom: Float,
                                  pointerBottom: Float) = Path().apply {
        addOval(RectF(-pointerRadiusCenter,
                -pointerRadiusCenter,
                pointerRadiusCenter,
                pointerRadiusCenter), Path.Direction.CW)

        moveTo(-pointerRadiusTop, -pointerTop)
        lineTo(0f, -pointerTop - pointerRadiusTop)
        lineTo(pointerRadiusTop, -pointerTop)
        lineTo(pointerRadiusBottom, pointerBottom)
        lineTo(-pointerRadiusBottom, pointerBottom)
        lineTo(-pointerRadiusTop, -pointerTop)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val width = View.resolveSizeAndState(minWidth, widthMeasureSpec, 1)

        val minHeight = View.MeasureSpec.getSize(width) - paddingBottom + paddingTop
        val height = View.resolveSizeAndState(View.MeasureSpec.getSize(minHeight), heightMeasureSpec, 0)
        val size = minOf(width, height)

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        if(canvas == null)
            throw IllegalArgumentException("canvas is null")

        val values = (endValue - startValue) / 2
        val degreesPerStep = (degrees / 2f) / values
        val lineEndX = pivotX + innerWidth / 2

        drawLines(canvas, lineEndX, degreesPerStep)
        drawNumbers(canvas, lineEndX, degreesPerStep)
        drawPointer(canvas, degreesPerStep)

        if (startValue < 0) {
            drawIndicators(degreesPerStep, canvas, lineEndX)
        }
    }

    private fun drawPointer(canvas: Canvas, degreesPerStep: Float) {
        val angle = getAngleFromValue(value, degreesPerStep)

        canvas.withSave {
            rotate(angle, pivotX, pivotY)
            translate(pivotX, pivotY)

            pointerPaint.color = pointerColor
            drawPath(pointerPath!!, pointerPaint)

            pointerPaint.color = Color.WHITE
            drawPath(pointerCenterPath!!, pointerPaint)
        }
    }

    private fun drawNumbers(canvas: Canvas, lineEndX: Float, degreesPerStep: Float) {
        numberPaint.textSize = numberSize

        for (i in startValue..endValue step showNumberEvery) {
            val angle = getAngleFromValue(i.toFloat(), degreesPerStep)
            val numberText = i.absoluteValue.toString()

            numberPaint.color = if (i <= 0) negativeNumberColor else numberColor
            drawNumber(angle, canvas, numberText, lineEndX)
        }
    }

    private fun drawIndicators(degreesPerStep: Float, canvas: Canvas, lineEndX: Float) {
        val halfDegrees = (degrees / 2f)
        val indicatorSpacing = 5 * smallLineEvery * degreesPerStep
        drawIndicator(-halfDegrees - indicatorSpacing, canvas, "-", lineEndX)
        drawIndicator(halfDegrees + indicatorSpacing, canvas, "+", lineEndX)
    }

    private fun drawLines(canvas: Canvas, lineEndX: Float, degreesPerStep: Float) {
        for (i in startValue..endValue step smallLineEvery) {
            val angle = getAngleFromValue(i.toFloat(), degreesPerStep)

            linePaint.color = getLineColor(i)
            linePaint.strokeWidth = getLineStrokeWidth(i)

            drawLine(angle, canvas, lineEndX, getLineLength(i))
        }
    }

    private fun getLineStrokeWidth(value: Int): Float {
        return when {
            value % bigLineEvery == 0 -> bigLineStrokeWidth
            value % mediumLineEvery == 0 -> mediumLineStrokeWidth
            else -> smallLineStrokeWidth
        }
    }

    private fun getLineColor(value: Int): Int {
        return when {
            value % bigLineEvery == 0 && value <= 0 -> negativeBigLineColor
            value % bigLineEvery == 0 -> bigLineColor
            value % mediumLineEvery == 0 && value <= 0 -> negativeMediumLineColor
            value % mediumLineEvery == 0 -> mediumLineColor
            value <= 0 -> negativeSmallLineColor
            else -> smallLineColor
        }
    }

    private fun getLineLength(value: Int): Float {
        return when {
            value % bigLineEvery == 0 -> bigLineLength
            value % mediumLineEvery == 0 -> mediumLineLength
            else -> smallLineLength
        }
    }

    private fun getAngleFromValue(value: Float, degreesPerStep: Float): Float {
        return -(degrees / 2f) + degreesPerStep * (value - startValue)
    }

    private fun drawNumber(angle: Float,
                           canvas: Canvas,
                           numberText: String,
                           lineEndX: Float) {
        val localRotation = clampDegrees(gaugeRotation + angle)

        canvas.withSave {
            rotate(localRotation, pivotX, pivotY)
            val textWidth = numberPaint.measureText(numberText)
            translate(lineEndX - bigLineLength - textWidth / 2 - 16, pivotY)
            rotate(-localRotation)
            drawText(numberText, 0f, numberSize / 2, numberPaint)
        }
    }

    private fun drawIndicator(angle: Float, canvas: Canvas, numberText: String, lineEndX: Float) {
        val localRotation = clampDegrees(gaugeRotation + angle)

        canvas.withSave {
            rotate(localRotation, pivotX, pivotY)
            val textWidth = numberPaint.measureText(numberText)
            translate(lineEndX - bigLineLength / 2 - textWidth / 2 - 16, pivotY)
            rotate(-localRotation)
            drawText(numberText, 0f, numberPaint.textSize / 2, numberPaint)
        }
    }

    private fun drawLine(rotation: Float,
                         canvas: Canvas,
                         lineEndX: Float,
                         lineLength: Float) {
        canvas.withSave {
            rotate(clampDegrees(gaugeRotation + rotation), pivotX, pivotY)
            drawLine(lineEndX - lineLength, pivotY, lineEndX, pivotY, linePaint)
        }
    }

    private fun clampDegrees(degrees: Float) = when {
        degrees < 0 -> degrees + 360
        degrees > 360 -> degrees - 360
        else -> degrees
    }
}