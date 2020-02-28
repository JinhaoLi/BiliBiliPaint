package com.jil.paintf.custom;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ImageSlideTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE1 = 0.75f;
    @Override
    public void transformPage(@NonNull View page, float position) {
        custom(page,position);
        //method3(page,position);
    }

    private void method1(View page, float position){
        float scaleFactor = Math.max(MIN_SCALE1, 1 - Math.abs(position));
        float rotate = 10 * Math.abs(position);
        //position小于等于-1的时候，代表page已经位于中心item的最左边，
        //此时设置为最小的缩放率以及最大的旋转度数
        if (position <= -1) {
            page.setScaleX(MIN_SCALE1);
            page.setScaleY(MIN_SCALE1);
            page.setRotationY(-rotate);
        } else if (position < 0) {//position从0变化到-1，page逐渐向左滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        } else if (position >= 0 && position < 1) {//position从0变化到1，page逐渐向右滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        } else if (position >= 1) {//position大于等于1的时候，代表page已经位于中心item的最右边
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        }
    }

    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2f;
    private void custom(View page, float position){
        float scaleFactor = (float) ((-0.75f*Math.pow(position,2))+(-0.25*position)+1f);
        float rotate = 45 * Math.abs(position);
        if (position <= -1) {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setRotationY(rotate);
        } else if (position < 0) {//position从0变化到-1，page逐渐向左滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        } else if (position >= 0 && position < 1) {//position从0变化到1，page逐渐向右滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        } else if (position >= 1) {//position大于等于1的时候，代表page已经位于中心item的最右边
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setRotationY(rotate);
        }
    }


    //透明度和高度最小值
    private static final float MIN_SCALE3 = 0.70f;
    private static final float MIN_ALPHA = 0;

    private void method3(View page,float position){
        int width = page.getWidth();
        int offset = 20 / width;  // 20为PageMargin属性值

        if(position < -1 - offset){
            page.setAlpha(MIN_ALPHA);
            page.setScaleY(MIN_SCALE3);
        }else if(position <= 1 + offset){//在[-1-offset,1+offset]范围
            if(position == 0){ //当前页面
                page.setAlpha(1.0f);
                page.setScaleY(1.0f);
            }else{
                if(position < 0){ //在[-1-offset,0]范围
                    //平滑变化
                    float f = MIN_ALPHA + (1 - MIN_ALPHA) * (1 + position + offset);
                    page.setAlpha(f);
                    float s = MIN_SCALE3 +(1 - MIN_SCALE3) * (1+position + offset);
                    page.setScaleY(s);
                }else{ //在[0，1+offset]范围
                    //平滑变化
                    float f = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - position + offset);
                    page.setAlpha(f);
                    float s = MIN_SCALE3 +(1 - MIN_SCALE3) * (1 - position + offset);
                    page.setScaleY(s);
                }

            }
        }
    }
}
