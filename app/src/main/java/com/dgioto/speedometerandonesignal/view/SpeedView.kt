package com.dgioto.speedometerandonesignal.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class SpeedView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private var maxValue = 0
    private var value = 0

    //Объектом класса Paint с флагом ANTI_ALIAS_FLAG. Этот объект используется для рисования
    // на холсте (canvas). флагом ANTI_ALIAS_FLAG - представляет собой инструкцию для сглаживания
    // краев и линий при рисовании
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Сохраняет текущее состояние холста, чтобы его можно было восстановить позже
        canvas.save()

        var width = width
        var height = height

        val aspect = width / height
        val normalAspect = 2f / 1f

        if (aspect < normalAspect) width = (normalAspect * height).toInt()
        else if (aspect < normalAspect) height = (width / normalAspect).toInt()

        //Масштабирует координаты холста. Увеличивает масштаб по осям X и Y
        canvas.scale(.5f * width, -1f * height)
        //Перемещает начало координат холста.
        canvas.translate(1f, -1f)

        paint.color = 0x40ffffff //полупрозрачный белый
        paint.style = Paint.Style.FILL //Устанавливает стиль рисования как заливку

        canvas.drawCircle(0F, 0F, 1F, paint)

        paint.color = 0x20000000 //близок к полностью прозрачному

        canvas.drawCircle(0F, 0F, 0.8F, paint)

        paint.color = 0xff88ff99.toInt() //зеленый
        //Устанавливает стиль рисования как контур
        paint.style = Paint.Style.STROKE
        //Устанавливает толщину контура
        paint.strokeWidth = 0.005f

        maxValue = 120
        value = 25

        val scale = 0.9f

        val step = Math.PI / maxValue

        for (i in 0..maxValue) {
            //Вычисляют координаты x1 и y1 для текущей итерации
            val x1 = cos(Math.PI - step * i)
            val y1 = sin(Math.PI - step * i)
            val x2: Float
            val y2: Float

            //В зависимости от значения i определяется, какие значения должны быть установлены
            // для x2 и y2. Это зависит от того, является ли i кратным 20 или нет
            if (i % 20 == 0) {
                x2 = (x1 * scale * 0.9f).toFloat()
                y2 = (y1 * scale * 0.9f).toFloat()
            } else {
                x2 = (x1 * scale).toFloat()
                y2 = (y1 * scale).toFloat()
            }
            canvas.drawLine(x1.toFloat(), y1.toFloat(), x2, y2, paint)
        }

        canvas.save()

        //Вращает холст на определенный угол в зависимости от значения value и maxValue.
        canvas.rotate((90 - 180 * (value / maxValue)).toFloat())

        paint.color = 0xffff8899.toInt() //зеленый
        paint.strokeWidth = 0.02f
        canvas.drawLine(0.01f, 0F, 0f, 1f, paint)
        canvas.drawLine(-0.01f, 0f, 0f, 1f, paint)

        paint.style = Paint.Style.FILL //Устанавливает стиль рисования как заливку
        paint.color = 0xff88ff99.toInt() //зеленый
        canvas.drawCircle(0f, 0f, .05f, paint)

        //Восстанавливает предыдущее состояние холста
        canvas.restore()

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(widthMeasureSpec)
        var height = MeasureSpec.getSize(widthMeasureSpec)

        val aspect = width / height
        val normalAspect = 2f / 1f
        if (aspect > normalAspect) {
            if (widthMode != MeasureSpec.EXACTLY)
                width = (normalAspect * height).roundToInt()
        } else if (aspect < normalAspect) {
            if (heightMode != MeasureSpec.EXACTLY)
                height = (width / normalAspect).roundToInt()
        }
        setMeasuredDimension(width, height)
    }

    fun setValue(value: Int) {
        this.value = min(value, maxValue)
        invalidate()
    }

    private var objectAnimator: ObjectAnimator? = null
    private fun setValueAnimated(value: Int) {
        objectAnimator?.cancel()
        objectAnimator = ObjectAnimator.ofInt(this, "value", this.value, value)
        objectAnimator?.run {
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newValue = getTouchValue(event.x, event.y)
                setValueAnimated(newValue)
                true
            }
            MotionEvent.ACTION_MOVE -> true
            MotionEvent.ACTION_UP -> true
            else -> super.onTouchEvent(event)
        }
    }

    private fun getTouchValue(x: Float, y: Float): Int {
        if (x.toInt() != 0 && y.toInt() != 0) {
            val startX = width / 2
            val startY = height

            val dirX = startX - x
            val dirY = startY - y

            val andle = acos(dirX / sqrt((dirX * dirX + dirY * dirY)))

            return (maxValue * (andle / PI)).roundToInt()
        }
        return value
    }
}