package com.jil.paintf.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * 2020/8/25 15:40
 *
 * @author JIL
 **/
public class ShowProgressImageView extends SubsamplingScaleImageView {
    private int progress;
    private int alp =255;
    private Paint paint =new Paint(Paint.ANTI_ALIAS_FLAG);

    public ShowProgressImageView(Context context, AttributeSet attr) {
        super(context, attr);
        paint.setColor(Color.GREEN);
        paint.setTextSize(50);
    }

    public ShowProgressImageView(Context context) {
        this(context,null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(progress==100&&alp>0){
            if(alp-15<0)
                alp=0;
            else
                alp-=15;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            },60);
        }
        paint.setColor(Color.argb(alp,(int)((1f-progress/100f)*255),(int)(progress/100f*255),0));
        canvas.drawRect(0f,(1f-progress/100f)*getHeight(),getWidth(),getHeight(),paint);
        canvas.drawText("当前进度："+ progress +"%",0,(1f-progress/100f)*getHeight()-5f,paint);
    }

    public void setProgress(int progress){
        this.progress =progress;
        invalidate();
    }
}
