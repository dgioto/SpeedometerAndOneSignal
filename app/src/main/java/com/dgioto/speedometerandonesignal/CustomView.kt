package com.dgioto.speedometerandonesignal

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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CustomView(context: Context, attributeSet: AttributeSet)
    : View(context, attributeSet) {

    var  listener: Listener? = null
    // Ширина контура круга
    private val paintCWidth = 170f
    private val paintText = Paint()
    private val paint = Paint()
    private val paintBm = Paint()
    private val paintC = Paint()
    // Угол начала рисования секторов (в градусах)
    private val startAngle = 0f
    private var mainColor = Color.BLUE
    private var mainColor2 = Color.BLUE
    // Объект Bitmap для изображения
    private var bm: Bitmap
    // Список цветов для секторов круга
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
    // Индекс кнопки, на которую было произведено нажатие (-1, если ничего не выбрано)
    private var buttonClicked = -1

    init {
        // Создание изображения Bitmap из ресурса Drawable
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

        // Инициализация основного и дополнительного цветов
        mainColor = ContextCompat.getColor(context, R.color.main_menu_color)
        mainColor2 = ContextCompat.getColor(context, R.color.main_menu_color2)

        // Инициализация параметров рисования текста
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

    // Метод для отрисовки круговой кнопки
    private fun drawCircleButton(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = width / 2f
        // Определение радиуса круга, с учетом минимальной ширины и высоты
        val radius = (width.coerceAtMost(height) / 2f) - paintCWidth / 2f
        // Установка стиля кисти для рисования контура круга
        paintC.style = Paint.Style.STROKE

        // Цикл для отрисовки секторов круга
        for (i in colors.indices) {
            // Установка цвета кисти в зависимости от нажатия кнопки
            paintC.color =
                if (i == buttonClicked) Color.BLACK
                else mainColor
            // Отрисовка сектора круга
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
        // Установка стиля кисти для рисования заполненного круга
        paintC.style = Paint.Style.FILL
        paintC.color = mainColor2
        // Отрисовка заполненного круга в центре
        canvas.drawCircle(
            centerX ,
            centerY ,
            radius / 1.5f,
            paintC
        )
        // Отрисовка изображения Bitmap в центре круга
        canvas.drawBitmap(
            bm,
            centerX - bm.width / 2f,
            centerY - bm.height / 2f,
            paintBm
        )
    }

    // Метод для отрисовки текста меню
    private fun drawMenuText(canvas: Canvas){
        // Для каждого элемента в списке меню TextUtils.menuList выполняется следующий код
        TextUtils.menuList.forEachIndexed { index, text ->
            // Создается прямоугольник rect для определения границ текста
            val rect = Rect()
            // Получение границ текста с помощью кисти paintText
            paintText.getTextBounds(text, 0, text.length, rect)
            // Вычисление угла для размещения текста вокруг круга
            val angle = ((360f / colors.size) * index) + ((360f / colors.size) / 2f)
            // Получение координат (x, y) для размещения текста на основе угла
            val coordinate = getXY(angle)

            //Поворот холста для правильного размещения текста в зависимости от угла.
            if (index in 0 .. 1 || index in 6 .. 7) {
                // Поворот холста на угол 90 + angle + 180 вокруг точки с координатами (coordinate.first, coordinate.second)
                canvas.rotate(90f + angle + 180, coordinate.first, coordinate.second)
            } else {
                canvas.rotate(90f + angle, coordinate.first, coordinate.second)
            }

            // Отрисовка текста на холсте с учетом координат и центрированием по горизонтали и вертикали
            canvas.drawText(
                text,
                coordinate.first - rect.exactCenterX(),
                coordinate.second - rect.exactCenterY(),
                paintText
            )

            //Возвращение холста в начальное положение после отрисовки текста.
            if (index in 0 .. 1 || index in 6 .. 7) {
                canvas.rotate(-90f - angle - 180, coordinate.first, coordinate.second)
            } else {
                canvas.rotate(-90f - angle, coordinate.first, coordinate.second)
            }
        }
    }

    // Метод для определения координат (x, y) по углу
    private fun getXY(angle: Float): Pair<Float, Float> {
        val centerX = (width / 2f)
        val centerY = (height / 2f)
        val radius = (width / 2f) - (paintCWidth / 2f)

        val x = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
        return Pair(x, y)
    }

    // Обработка события касания экрана для определения клика на элементе
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Получение координат X и Y касания
        val x = event.x
        val y = event.y
        // Вычисление координат центра круга
        val centerX = width / 2f
        val centerY = width / 2f

        // Определение действия события касания
        when (event.action) {
            // В случае, если произошло нажатие
            MotionEvent.ACTION_DOWN -> {
                val angle = (Math.toDegrees(
                    atan2(y - centerY,x - centerX).toDouble()
                ) + 360) % 360
                // Определение индекса сектора, на который было произведено нажатие
                buttonClicked = (angle / ( 360 / colors.size)).toInt()
                // Вызов слушателя клика с передачей индекса нажатой кнопки
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