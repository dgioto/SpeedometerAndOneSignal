package com.dgioto.speedometerandonesignal.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint()
    private val paintC = Paint()
    // Угол начала рисования секторов (в градусах)
    private val startAngle = -180f
    private val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
    // Угол, на который делится круг для каждого цвета
    private val sweepAngle = 360f / colors.size
    private var buttonClicked = -1

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        paint.strokeWidth = 5f
        paintC.style = Paint.Style.FILL
        paintC.color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircleButton(canvas)
    }

    private fun drawCircleButton(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = width / 2f
        val radius = width.coerceAtMost(height) / 2f

        for (i in colors.indices) {
            paintC.color =
                if (i == buttonClicked) Color.GRAY
                else colors[i]
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle + i * sweepAngle,
                sweepAngle,
                true,
                paintC
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val centerX = width / 2f
        val centerY = width / 2f

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //click on RED
                if (x < centerX && y < centerY) buttonClicked = 0
                //click on BLUE
                if (x > centerX && y < centerY) buttonClicked = 1
                //click on GREEN
                if (x > centerX && y > centerY) buttonClicked = 2
                //click on YELLOW
                if (x < centerX && y > centerY) buttonClicked = 3
                // Перерисовка вида
                invalidate()
            }
            // Сброс выбранной кнопки при отпускании экрана
            MotionEvent.ACTION_UP -> {
                buttonClicked = -1
                invalidate()
            }
        }
        // Возвращение true, чтобы показать, что событие обработано
        return true
    }
}