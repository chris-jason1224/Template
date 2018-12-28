package com.cj.main.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.cj.log.util.ScreenUtil;
import com.cj.main.R;

/**
 * Created by mayikang on 2018/11/19.
 */

public class InfiniteView extends View {

    private Context mContext;
    private Paint mP=new Paint();
    private int width,height;//控件实际的宽高
    private int startWidth;//初始宽度
    private int layerIndex;//一共执行几次缩放 scale

    /**默认尺寸**/
    @IntDef({DefaultSize.defaultWidth,DefaultSize.defaultLayerIndex})
    public @interface DefaultSize{
        int defaultWidth =200;
        int defaultLayerIndex=9999;
    }


    /**默认都调用xml方式的三参数构造方法**/
    public InfiniteView(Context context) {
        this(context,null,0);
    }

    public InfiniteView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public InfiniteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init(attrs,defStyleAttr);
    }


    /**初始化相关参数**/
    private void init(AttributeSet attributeSet,int defStyleAttr){

        TypedArray typedArray=mContext.obtainStyledAttributes(attributeSet,R.styleable.biz_main_InfiniteView,defStyleAttr,0);

        if(typedArray == null){
            return;
        }

        startWidth=typedArray.getDimensionPixelSize(R.styleable.biz_main_InfiniteView_biz_main_start_width,DefaultSize.defaultWidth);
        layerIndex=typedArray.getDimensionPixelSize(R.styleable.biz_main_InfiniteView_biz_main_layer_index,DefaultSize.defaultLayerIndex);

        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //画布中心点移动到中间
        canvas.translate(width/2,height/2);
        mP.setStyle(Paint.Style.STROKE);
        mP.setColor(Color.BLACK);
        float paintWidth=5;
        //画笔宽度10dp

        for(int i=0;i<layerIndex;i++){
            mP.setStrokeWidth(ScreenUtil.dip2px(mContext,paintWidth));
            RectF rf=new RectF(
                    -width/2+ScreenUtil.dip2px(mContext,2.5f),
                    -height/2+ScreenUtil.dip2px(mContext,2.5f),
                    width/2-ScreenUtil.dip2px(mContext,2.5f),
                    height/2-ScreenUtil.dip2px(mContext,2.5f));

            canvas.drawRect(rf,mP);
            paintWidth=paintWidth-0.3f;
            width=width-ScreenUtil.dip2px(mContext,20);
            height=width;


            if(width<ScreenUtil.dip2px(mContext,2)){
                break;
            }
        }

        canvas.save();
        width=getWidth();
        height=getHeight();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mw=MeasureSpec.getSize(widthMeasureSpec);
        int mh=MeasureSpec.getSize(heightMeasureSpec);
        int x=0;

        //高度和宽度取小的
        if(mh>mw){
            x=mw;
        }else {
            x=mh;
        }

        //强制绘制成一个正方形
        width=height=x;

        setMeasuredDimension(x,x);
    }
}
