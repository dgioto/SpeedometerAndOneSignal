package com.dgioto.speedometerandonesignal.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.dgioto.speedometerandonesignal.R
import com.dgioto.speedometerandonesignal.TextUtils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CustomView(context: Context, attributeSet: AttributeSet)
    : View(context, attributeSet) {

    var  listener: Listener? = null
    private val paintCWidth = 170f
    private val paintText = Paint()
    private val paint = Paint()
    private val paintBm = Paint()
    private val paintC = Paint()
    // Угол начала рисования секторов (в градусах)
    private val startAngle = 0f
    private var mainColor = Color.BLUE
    private var mainColor2 = Color.BLUE

    private var bm: Bitmap
    private val colors = listOf(
        Color.RED,
        Color.DKGRAY,
        Color.GREEN,
        Color.CYAN,
        Color.BLACK,
        Color.BLUE,
        Color.MAGENTA,
        Color.GREEN)
    // Угол, на который делится круг для каждого цвета
    private val sweepAngle = 360f / colors.size
    private var buttonClicked = -1

    init {
        val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_local_florist_24)
        bm = Bitmap.createBitmap(
            150,
            150,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bm)
        canvas.rotate(-90f, canvas.width / 2f, canvas.height / 2f)
        drawable?.setBounds(0, 0, 150, 150)
        drawable?.draw(canvas)

        mainColor = ContextCompat.getColor(context, R.color.main_menu_color)
        mainColor2 = ContextCompat.getColor(context, R.color.main_menu_color2)

        paintText.style = Paint.Style.FILL
        paintText.color = Color.WHITE
        paintText.textSize = 35f

        paint.style = Paint.Style.STROKE
        paint.color = Color.GRAY
        paint.strokeWidth = 5f

        paintC.color = Color.RED
        paintC.strokeWidth = paintCWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircleButton(canvas)
        drawMenuText(canvas)
    }

    private fun drawCircleButton(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = width / 2f
        val radius = (width.coerceAtMost(height) / 2f) - paintCWidth / 2f
        paintC.style = Paint.Style.STROKE

        for (i in colors.indices) {
            paintC.color =
                if (i == buttonClicked) Color.BLACK
                else mainColor
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle + i * sweepAngle + 1,
                sweepAngle - 2,
                false,
                paintC
            )
        }
        paintC.style = Paint.Style.FILL
        paintC.color = mainColor2
        canvas.drawCircle(
            centerX ,
            centerY ,
            radius / 1.5f,
            paintC
        )

        canvas.drawBitmap(
            bm,
            centerX - bm.width / 2f,
            centerY - bm.height / 2f,
            paintBm
        )
    }

    private fun drawMenuText(canvas: Canvas){
        TextUtils.menuList.forEachIndexed { index, text ->
            val rect = Rect()
            paintText.getTextBounds(text, 0, text.length, rect)
            val angle = ((360f / colors.size) * index) + ((360f / colors.size) / 2f)
            val coordinate = getXY(angle)

            if (index in 0 .. 1 || index in 6 .. 7) {
                canvas.rotate(90f + angle + 180, coordinate.first, coordinate.second)
            } else {
                canvas.rotate(90f + angle, coordinate.first, coordinate.second)
            }

            canvas.drawText(
                text,
                coordinate.first - rect.exactCenterX(),
                coordinate.second - rect.exactCenterY(),
                paintText
            )

            if (index in 0 .. 1 || index in 6 .. 7) {
                canvas.rotate(-90f - angle - 180, coordinate.first, coordinate.second)
            } else {
                canvas.rotate(-90f - angle, coordinate.first, coordinate.second)
            }
        }
    }

    private fun getXY(angle: Float): Pair<Float, Float> {
        val centerX = (width / 2f)
        val centerY = (height / 2f)
        val radius = (width / 2f) - (paintCWidth / 2f)

        val x = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
        return Pair(x, y)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val centerX = width / 2f
        val centerY = width / 2f

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val angle = (Math.toDegrees(
                    atan2(
                        y - centerY,
                        x - centerX
                    ).toDouble()
                ) + 360) % 360
                buttonClicked = (angle / ( 360 / colors.size)).toInt()

                listener?.onClick(buttonClicked)

                Log.d("MyLog", "Angle: $angle")

                // Перерисовка вида
                invalidate()
            }
        }
        // Возвращение true, чтобы показать, что событие обработано
        return true
    }

    interface Listener{
        fun onClick(index: Int)
    }
}