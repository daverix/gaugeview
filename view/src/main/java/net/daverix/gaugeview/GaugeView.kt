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
import androidx.graphics.withSave
import kotlin.math.absoluteValue


class GaugeView : View {
    private val smallLinePaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val negativeSmallLinePaint = Paint().apply {
        color = smallLinePaint.color
        style = Paint.Style.STROKE
        strokeWidth = smallLinePaint.strokeWidth
    }

    private val mediumLinePaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val negativeMediumLinePaint = Paint().apply {
        color = mediumLinePaint.color
        style = Paint.Style.STROKE
        strokeWidth = mediumLinePaint.strokeWidth
    }

    private val bigLinePaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    private val negativeBigLinePaint = Paint().apply {
        color = bigLinePaint.color
        style = Paint.Style.STROKE
        strokeWidth = bigLinePaint.strokeWidth
    }

    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 64f
        textAlign = Paint.Align.CENTER
    }
    private val negativeNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = numberPaint.color
        style = Paint.Style.FILL
        textSize = numberPaint.textSize
        textAlign = Paint.Align.CENTER
    }

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val pointerCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
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
    var pointerColor: Int = Color.RED

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {

        val arr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.GaugeView,
                0, 0)
        try {
            pointerColor = arr.getInt(R.styleable.GaugeView_pointerColor, pointerColor)
            startValue = arr.getInt(R.styleable.GaugeView_startValue, startValue)
            endValue = arr.getInt(R.styleable.GaugeView_endValue, endValue)
            value = arr.getFloat(R.styleable.GaugeView_value, value)
            showNumberEvery = arr.getInt(R.styleable.GaugeView_showNumberEvery, showNumberEvery)
            degrees = arr.getInt(R.styleable.GaugeView_arc, degrees)
            gaugeRotation = arr.getFloat(R.styleable.GaugeView_rotation, gaugeRotation)


            smallLineLength = arr.getDimension(R.styleable.GaugeView_smallLineLength, smallLineLength)
            mediumLineLength = arr.getDimension(R.styleable.GaugeView_mediumLineLength, mediumLineLength)
            bigLineLength = arr.getDimension(R.styleable.GaugeView_mediumLineLength, bigLineLength)

            smallLineEvery = arr.getInt(R.styleable.GaugeView_smallLineEvery, smallLineEvery)
            mediumLineEvery = arr.getInt(R.styleable.GaugeView_mediumLineEvery, mediumLineEvery)
            bigLineEvery = arr.getInt(R.styleable.GaugeView_bigLineEvery, bigLineEvery)

            numberPaint.apply {
                textSize = arr.getDimension(R.styleable.GaugeView_numberSize, textSize)
                color = arr.getColor(R.styleable.GaugeView_numberColor, color)
            }
            negativeNumberPaint.apply {
                textSize = numberPaint.textSize
                color = arr.getColor(R.styleable.GaugeView_negativeNumberColor, color)
            }

            smallLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_smallLineColor, color)
                strokeWidth = arr.getDimension(R.styleable.GaugeView_smallLineStrokeWidth, strokeWidth)
            }
            negativeSmallLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_negativeSmallLineColor, color)
                strokeWidth = smallLinePaint.strokeWidth
            }

            mediumLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_mediumLineColor, color)
                strokeWidth = arr.getDimension(R.styleable.GaugeView_mediumLineStrokeWidth, strokeWidth)
            }
            negativeMediumLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_negativeMediumLineColor, color)
                strokeWidth = mediumLinePaint.strokeWidth
            }

            bigLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_bigLineColor, color)
                strokeWidth = arr.getDimension(R.styleable.GaugeView_bigLineStrokeWidth, strokeWidth)
            }
            negativeBigLinePaint.apply {
                color = arr.getColor(R.styleable.GaugeView_negativeBigLineColor, color)
                strokeWidth = bigLinePaint.strokeWidth
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

        pointerPath = Path().apply {
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
        pointerCenterPath = Path().apply {
            addOval(RectF(-pointerRadiusBottom,
                    -pointerRadiusBottom,
                    pointerRadiusBottom,
                    pointerRadiusBottom), Path.Direction.CW)
        }
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
    }

    private fun drawPointer(canvas: Canvas, degreesPerStep: Float) {
        val angle = getAngleFromValue(value, degreesPerStep)

        pointerPaint.color = pointerColor

        canvas.withSave {
            rotate(angle, pivotX, pivotY)
            translate(pivotX, pivotY)
            drawPath(pointerPath, pointerPaint)
            drawPath(pointerCenterPath, pointerCenterPaint)
        }
    }

    private fun drawNumbers(canvas: Canvas, lineEndX: Float, degreesPerStep: Float) {
        for (i in startValue..endValue) {
            if (i % showNumberEvery == 0) {
                val angle = getAngleFromValue(i.toFloat(), degreesPerStep)
                val numberText = i.absoluteValue.toString()

                val textPaint = if (i <= 0) negativeNumberPaint else numberPaint
                drawText(angle, canvas, numberText, lineEndX, textPaint)
            }
        }

        if(startValue < 0) {
            val halfDegrees = (degrees / 2f)
            val indicatorSpacing = 5 * smallLineEvery * degreesPerStep
            drawIndicator(-halfDegrees - indicatorSpacing, canvas, "-", lineEndX)
            drawIndicator(halfDegrees + indicatorSpacing, canvas, "+", lineEndX)
        }
    }

    private fun drawLines(canvas: Canvas, lineEndX: Float, degreesPerStep: Float) {
        for (i in startValue..endValue step smallLineEvery) {
            val angle = getAngleFromValue(i.toFloat(), degreesPerStep)

            val lineLength = when {
                i % bigLineEvery == 0 -> bigLineLength
                i % mediumLineEvery == 0 -> mediumLineLength
                else -> smallLineLength
            }

            val linePaint = when {
                i % bigLineEvery == 0 && i <= 0 -> negativeBigLinePaint
                i % bigLineEvery == 0 -> bigLinePaint
                i % mediumLineEvery == 0 && i <= 0 -> negativeMediumLinePaint
                i % mediumLineEvery == 0 -> mediumLinePaint
                i <= 0 -> negativeSmallLinePaint
                else -> smallLinePaint
            }

            drawLine(angle, canvas, lineEndX, lineLength, linePaint)
        }
    }

    private fun getAngleFromValue(value: Float, degreesPerStep: Float): Float {
        return -(degrees / 2f) + degreesPerStep * (value - startValue)
    }

    private fun drawText(angle: Float,
                         canvas: Canvas,
                         numberText: String,
                         lineEndX: Float,
                         paint: Paint) {
        val localRotation = clampDegrees(gaugeRotation + angle)

        canvas.withSave {
            rotate(localRotation, pivotX, pivotY)
            val textWidth = paint.measureText(numberText)
            translate(lineEndX - bigLineLength - textWidth / 2 - 16, pivotY)
            rotate(-localRotation)
            drawText(numberText, 0f, paint.textSize / 2, paint)
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
                         lineLength: Float,
                         linePaint: Paint) {
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