package com.dgioto.speedometerandonesignal.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpeedView2 extends View {

    private float maxValue = 120;
    private int value = 25;

    public SpeedView2(Context context) {
        super(context);
    }

    public SpeedView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeedView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Объектом класса Paint с флагом ANTI_ALIAS_FLAG. Этот объект используется для рисования
    // на холсте (canvas). флагом ANTI_ALIAS_FLAG - представляет собой инструкцию для сглаживания
    // краев и линий при рисовании
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        //Сохраняет текущее состояние холста, чтобы его можно было восстановить позже
        canvas.save();

        float width = getWidth();
        float height = getHeight();

        float aspect = width / height;
        final float normalAspect = 2f / 1f;
        if (aspect > normalAspect) width = normalAspect * height;
        if (aspect < normalAspect) height = width / normalAspect;

        //Масштабирует координаты холста. Увеличивает масштаб по осям X и Y
        canvas.scale(.5f * getWidth(), -1f * getHeight());
        //Перемещает начало координат холста.
        canvas.translate(1.f, -1.f);

        paint.setColor(0x40ffffff);//полупрозрачный белый
        paint.setStyle(Paint.Style.FILL);//Устанавливает стиль рисования как заливку

        canvas.drawCircle(0, 0, 1, paint);

        paint.setColor(0x20000000);//близок к полностью прозрачному

        canvas.drawCircle(0, 0, 0.8F, paint);

        paint.setColor(0xff88ff99);//зеленый
        //Устанавливает стиль рисования как контур
        paint.setStyle(Paint.Style.STROKE);
        //Устанавливает толщину контура
        paint.setStrokeWidth(0.005f);

//        int maxValue = 120;
//        int value = 25;

        float scale = 0.9f;

        double step = Math.PI / maxValue;
        for (int i=0; i <= maxValue; i++) {
            //Вычисляют координаты x1 и y1 для текущей итерации
            float x1 = (float) Math.cos(Math.PI - step * i);
            float y1 = (float) Math.sin(Math.PI - step * i);
            float x2;
            float y2;

            //В зависимости от значения i определяется, какие значения должны быть установлены
            // для x2 и y2. Это зависит от того, является ли i кратным 20 или нет
            if (i % 20 == 0) {
                x2 = x1 * scale * 0.9f;
                y2 = y1 * scale * 0.9f;
            } else {
                x2 = (x1 * scale);
                y2 = (y1 * scale);
            }
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

        canvas.save();

        //Вращает холст на определенный угол в зависимости от значения value и maxValue.
        canvas.rotate(90 - ((float) 180 * (value / (float) maxValue)));
        paint.setColor(0xffff8899);//зеленый
        paint.setStrokeWidth(0.02f);
        canvas.drawLine(0.01f, 0, 0, 1f, paint);
        canvas.drawLine(-0.01f, 0, 0, 1f, paint);

        paint.setStyle(Paint.Style.FILL);//Устанавливает стиль рисования как заливку
        paint.setColor(0xff88ff99);//зеленый
        canvas.drawCircle(0f, 0f, .05f, paint);

        //Восстанавливает предыдущее состояние холста
        canvas.restore();

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(widthMeasureSpec);

        float aspect = width / (float) height;
        final float normalAspect = 2f / 1f;
        if (aspect > normalAspect) {
            if (widthMode != MeasureSpec.EXACTLY)
                width = Math.round(normalAspect * height);
        } if (aspect < normalAspect) {
            if (heightMode != MeasureSpec.EXACTLY)
                height = Math.round(width / normalAspect);
        }
        setMeasuredDimension(width, height);
    }

//    public void setValue(int value) {
//        this.value = Math.min(value, maxValue);
//        invalidate();
//    }

    ObjectAnimator objectAnimator;
    @SuppressLint("ObjectAnimatorBinding")
    private void setValueAnimated(int value) {
        if (objectAnimator != null){
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofInt(this, "value", this.value, value);
        objectAnimator.setDuration(100L * Math.abs(this.value - value) * 5);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int newValue = getTouchValue(event.getX(), event.getY());
                setValueAnimated(newValue);
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private int getTouchValue(float x, float y) {
        if (x != 0 & y != 0) {
            float startX = getWidth() / 2;
            float startY = getHeight();

            float dirX = startX - x;
            float dirY = startY - y;

            float andle = (float) Math.acos(dirX / Math.sqrt((dirX * dirX + dirY * dirY)));

            return Math.round(maxValue * (andle / (float) Math.PI));
        }
        return value;
    }
}
