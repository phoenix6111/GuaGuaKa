package com.wanghaisheng.view.guaguaka;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wanghaisheng.guaguaka.R;

/**
 * Author: sheng on 2016/9/12 11:22
 * Email: 1392100700@qq.com
 * 一个实现刮刮卡效果的View
 */
public class GuaGuaKa extends View {

    //默认文字颜色
    public static final int DEFAULT_TEXT_COLOR = 0xff333333;
    //默认文字大小
    public static final int DEFAULT_TEXT_SIZE = 22;
    public static final String DEFAULT_TEXT = "谢谢惠顾";
    //默认覆盖层颜色
    public static final int DEFAULT_OUTER_COLOR = 0xffe0e0e0;
    //默认刮刮卡的图片
    public static final int DEFAULT_OUTER_IMG = R.drawable.fg_guaguaka;
    //默认刮的时候的画笔的宽度
    public static final int DEFAULT_PEN_SIZE = 16;

    //画笔
    private Paint mOuterPaint;
    private Canvas mCanvas;
    private Path mPath;
    //外层bitmap的背景bitmap
    private Bitmap mOuterBitmapBG;
    //外层覆盖的图片
    private Bitmap mOuterBitmap;
    //外层覆盖图片的res
    private int mOuterImg;
    //外层图片背景颜色
    private int mOuterColor;
    //刮刮卡画笔宽度
    private int mPenSize;

    //上一次触摸点的横、纵坐标
    private int mLastX;
    private int mLastY;

    /******内层属性****/
    //内层文字画笔
    private Paint mInnerPaint;
    //文字所在的矩形
    private Rect mInnerTextBounds;
    //内层显示的文字
    private String mInnerText;
    //内层显示文字的颜色
    private int mInnerTextColor;
    //内层文字的大小
    private int mInnerTextSize;
    //刮刮是否成功的信号（被刮区域达到60%）
    private volatile boolean mIsCompleted;

    //刮刮卡完成回调接口
    private OnGuaGuaKaCompleteListener mCompleteListener;

    /**
     * 刮刮卡完成回调接口
     */
    public interface OnGuaGuaKaCompleteListener {
        void onComplete();
    }

    public void setCompleteListener(OnGuaGuaKaCompleteListener completeListener) {
        mCompleteListener = completeListener;
    }

    public GuaGuaKa(Context context) {
        this(context,null);
    }

    public GuaGuaKa(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GuaGuaKa(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainAttrValue(attrs);

        initOuterDatas();

        initInnerDatas();

    }

    /**
     * 设置文字内容
     * @param innerText
     */
    public void setText(String innerText) {
        mInnerText = innerText;

        //重新设置获奖信息的文本的宽和高
        mInnerPaint.getTextBounds(mInnerText,0,mInnerText.length(),mInnerTextBounds);
    }

    /**
     * sp 单位转化为 px
     * @param spValue
     * @return
     */
    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spValue,getResources().getDisplayMetrics());
    }

    /**
     * 获取自定义属性值
     * @param attrs
     */
    private void obtainAttrValue(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.GuaGuaKa);

        mPenSize = (int) typedArray.getDimension(R.styleable.GuaGuaKa_guaguaka_pen_size,sp2px(DEFAULT_PEN_SIZE));

        mInnerTextColor = typedArray.getColor(R.styleable.GuaGuaKa_guaguaka_textcolor,DEFAULT_TEXT_COLOR);
        mInnerTextSize = (int) typedArray.getDimension(R.styleable.GuaGuaKa_guaguaka_textsize,sp2px(DEFAULT_TEXT_SIZE));
        mOuterColor = typedArray.getColor(R.styleable.GuaGuaKa_guaguaka_color,DEFAULT_OUTER_COLOR);
        mOuterImg = typedArray.getResourceId(R.styleable.GuaGuaKa_guaguaka_img,DEFAULT_OUTER_IMG);

        typedArray.recycle();
    }

    private void initInnerDatas() {
        mInnerPaint = new Paint();
        mInnerTextBounds = new Rect();
        if(TextUtils.isEmpty(mInnerText)) {
            mInnerText = DEFAULT_TEXT;
        }

        setupInnerPaint();
    }

    /**
     * 设置绘制获奖内容的文本的相关属性
     */
    private void setupInnerPaint() {
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(mInnerTextColor);
        mInnerPaint.setTextSize(mInnerTextSize);

        //获得绘制获奖信息的文本的宽和高
        mInnerPaint.getTextBounds(mInnerText,0,mInnerText.length(),mInnerTextBounds);
    }

    private void initOuterDatas() {
        mOuterPaint = new Paint();
        mPath = new Path();
        mOuterBitmap = BitmapFactory.decodeResource(getResources(), mOuterImg);

        //设置paing属性
        setupOuterPaint();
    }

    /**
     * 设置paint属性
     */
    private void setupOuterPaint() {
        mOuterPaint.setColor(mOuterColor);
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setDither(true);
        mOuterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setStyle(Paint.Style.FILL);
        mOuterPaint.setStrokeWidth(mPenSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mOuterBitmapBG = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mOuterBitmapBG);

        //绘制灰色图层
        mCanvas.drawRoundRect(new RectF(0,0,width,height),30,30,mOuterPaint);
        mCanvas.drawBitmap(mOuterBitmap,null,new Rect(0,0,width,height),null);

        Log.d("onmeasure  ","onmeasure  ");


    }

    //新开一个线程计算被刮开点的像素面积占总面积的百分比，如果被刮面积已经达到60%，则直接显示中奖信息区域，清除上层覆盖层
    private Runnable mCalWipeAreaRunnable = new Runnable() {
        @Override
        public void run() {
            //获得宽和高
            int width = getWidth();
            int height = getHeight();

            //被刮开区域面积
            float wipeArea = 0;
            //总区域面积
            float totalArea = width * height;
            //存储像素点的信息
            int[] pixels = new int[width*height];
            Bitmap bitmap = mOuterBitmapBG;
            bitmap.getPixels(pixels,0,width,0,0,width,height);

            //计算被刮点的区域面积
            for(int i=0; i<width; i++) {
                for(int j=0; j<height; j++) {
                    int index = i + j*width;
                    if(pixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }

            //被刮开区域大于60%，则取消覆盖层，直接显示中奖信息
            if((totalArea>0 && wipeArea>0)) {

                int percent = (int)(wipeArea*100/totalArea);
                Log.d("percent","percent "+percent);
                if(percent >= 60) {
                    mIsCompleted = true;

                    //重绘
                    postInvalidate();
                }

            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸点的坐标
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            //按下时为触摸起始点
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX,mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int absX = Math.abs(x-mLastX);
                int absY = Math.abs(y-mLastY);
                //当移动距离中x或y的距离大于3px时才有效
                if(absX > 3 || absY >3) {
                    mPath.lineTo(x,y);
                }

                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开屏幕的时候，计算刮开区域是否已经达到总区域的60%
                new Thread(mCalWipeAreaRunnable).start();
                break;

        }

        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("onDraw ","ondraw  ");
        canvas.drawText(mInnerText,getWidth()/2-mInnerTextBounds.width()/2,getHeight()/2+mInnerTextBounds.height()/2,mInnerPaint);

        //刮奖完成回调接口
        if(mIsCompleted && mCompleteListener!=null) {
            mCompleteListener.onComplete();
        }

        //如果被刮区域还没达到60%，则还显示覆盖层
        if(!mIsCompleted) {
            drawPath();
            canvas.drawBitmap(mOuterBitmapBG,0,0,null);
        }
    }

    private void drawPath() {
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath,mOuterPaint);
    }
}
