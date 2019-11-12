package com.cjkj.dral.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.cjkj.dral.R;

public class DiagnosisView extends View {

    // circle and text colors
    private int circleColour;
    private int labelColour;

    // paint for drawing custom view
    private Paint circlePaint;

    private int percentage;

    private int angle;

    private RectF r;

    private int[] origin;


    public DiagnosisView(Context context, AttributeSet attributes) {
        super(context, attributes);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attributes, R.styleable.DiagnosisView, 0, 0);

        r = new RectF();

        origin = new int[2];
        this.getLocationOnScreen(origin);

        try {
            //get the text and colors specified using the names in attrs.xml
            circleColour = a.getInteger(R.styleable.DiagnosisView_circleColour, 0);     // 0 is default
            labelColour = a.getInteger(R.styleable.DiagnosisView_labelColour, 0);
            angle = 0;
            percentage = 0;
        } finally {
            a.recycle();
        }
    }

    public DiagnosisView(Context context) {
        super(context);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circleColour = 0;
        labelColour = 0;
        angle = 0;
        percentage = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        int radius = (viewWidthHalf > viewHeightHalf) ? viewHeightHalf - 10 : viewWidthHalf - 10;
        int x = origin[0];
        int y = origin[1];
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleColour);
        r.set(x, y, x + 2 * radius, y + 2 * radius);
        canvas.drawArc(r, 270, angle, true, circlePaint);

        circlePaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        canvas.drawCircle(x + radius, y + radius, radius / 1.5f, circlePaint);

        int textSize = 150;
        circlePaint.setColor(labelColour);
        circlePaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setTextSize(textSize);
        circlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawText(String.valueOf((int) (100 * angle / 360.0 + 0.5)), viewWidthHalf - textSize / 12.0f, viewHeightHalf + textSize / 4.0f, circlePaint);
    }

    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, (int) (360 * percentage / 100.0 + 0.5));
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    public int getCircleColor(){
        return circleColour;
    }

    public int getLabelColor(){
        return labelColour;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setCircleColor(int newColor){
        //update the instance variable
        circleColour = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }
    public void setLabelColor(int newColor){
        //update the instance variable
        labelColour = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }

    public void setPercentage(int circlePercentage) {
        percentage = circlePercentage;
    }

}
