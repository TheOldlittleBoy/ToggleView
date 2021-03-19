package com.zsl.toggleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * @author zsl
 */
public class ToggleView extends View {


    private float fontHeight;
    private Paint.FontMetrics fontMetrics;

    /**
     * 状态改变监听
     */
    public interface OnCheckedListener {
        /**
         * 状态改变回调
         * @param isChecked 是否选中
         */
        void onChange(boolean isChecked);
    }

    private OnCheckedListener mListener;
    /**
     * view默认的高,view默认的宽是高的两倍(单位:dp)
     */
    public static final int VIEW_HEIGHT = 20;
    /**
     * 默认的控件内边距
     */
    private static final int PADDING = 5;
    /**
     * 移动的判定距离
     */
    private static int MOVE_DISTANCE = 20;
    /**
     * 圆角矩形的宽
     */
    private int toggleWidth;
    /**
     * 控件右边距
     */
    private int slideRight;
    /**
     * 圆角
     */
    private float roundRadius;

    private Scroller mScroller;
    /**
     * 当前按钮的开关状态
     */
    private boolean isChecked = true;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private Paint togglePaint;
    private Paint checkTextPaint;
    private Paint normalTextPaint;
    private float toggleStartX;
    private int centerX;
    private int text1CenterX;
    private int text2CenterX;
    private float textBaseY;
    private float preX = 0;
    private boolean isMove;
    private int viewHeightInt;

    /**
     * 文本
     */
    private String checkedData;
    private String unCheckedData;


    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    /**
     * 设置点击监听
     *
     * @param listener  listener
     */
    public void setOnCheckedListener(OnCheckedListener listener) {
        this.mListener = listener;
    }

    public ToggleView setData(String checkedData, String unCheckedData) {
        this.checkedData = checkedData;
        this.unCheckedData = unCheckedData;
        return this;
    }

    /**
     * 设置按钮状态
     *
     * @param checked   是否选中
     */
    public ToggleView setChecked(boolean checked) {
        this.isChecked = checked;
        if (isChecked) {
            toggleStartX = PADDING;
        } else {
            toggleStartX = centerX + PADDING;
        }
        return this;
    }

    public void build(){
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        isChecked = array.getBoolean(R.styleable.ToggleView_checked, true);
        checkedData = array.getString(R.styleable.ToggleView_checkedData);
        unCheckedData = array.getString(R.styleable.ToggleView_unCheckedData);
        array.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.gray_decoration));
        togglePaint = new Paint();
        togglePaint.setAntiAlias(true);
        togglePaint.setDither(true);
        togglePaint.setAlpha(30);
        togglePaint.setColor(getResources().getColor(R.color.white));

        checkTextPaint = new Paint();
        checkTextPaint.setAntiAlias(true);
        checkTextPaint.setTextSize(DensityUtil.dp2px(context,20));
        checkTextPaint.setTextAlign(Paint.Align.CENTER);
        checkTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        checkTextPaint.setColor(getResources().getColor(R.color.black_title));

        normalTextPaint = new Paint();
        normalTextPaint.setAntiAlias(true);
        normalTextPaint.setTextSize(DensityUtil.dp2px(context,20));
        normalTextPaint.setTextAlign(Paint.Align.CENTER);
        normalTextPaint.setTypeface(Typeface.DEFAULT);
        normalTextPaint.setColor(getResources().getColor(R.color.gray_contact_name));

        mScroller = new Scroller(context);
        viewHeightInt = DensityUtil.dp2px(context, VIEW_HEIGHT);

        fontMetrics = checkTextPaint.getFontMetrics();
        fontHeight = fontMetrics.bottom - fontMetrics.top;
        //外部圆角矩形的半径
        roundRadius = DensityUtil.dp2px(context,8);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            //如果是wrap_content
            heightSize = viewHeightInt;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = heightSize * 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        MOVE_DISTANCE = mWidth / 100;
        //右边距
        slideRight = mWidth - PADDING * 2;
        //内部圆角矩形的长度
        toggleWidth = slideRight / 2;
        //控件的中线
        centerX = mWidth / 2;

        textBaseY = mHeight - (mHeight - fontHeight) / 2 - fontMetrics.bottom;
        //文本1中心X轴坐标
        text1CenterX = mWidth / 4;
        //文本2中心X轴坐标
        text2CenterX = mWidth / 4 * 3;

        if (isChecked) {
            //内部圆的x轴起始坐标
            toggleStartX = PADDING;
        } else {
            //内部开关的x轴起始坐标
            toggleStartX = centerX + PADDING;

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
    }

    /**
     * 画圆角矩形
     *
     * @param canvas    canvas
     */
    private void drawRect(Canvas canvas) {
        //画填充
        canvas.drawRoundRect(0, 0, mWidth, mHeight, roundRadius, roundRadius, mPaint);

        //画透明遮罩
        canvas.drawRoundRect(toggleStartX, PADDING, toggleStartX + toggleWidth, mHeight - PADDING, roundRadius, roundRadius, togglePaint);

        //画文字
        if (checkedData != null && unCheckedData != null) {
            if (isChecked) {
                canvas.drawText(checkedData, text1CenterX, textBaseY, checkTextPaint);
                canvas.drawText(unCheckedData, text2CenterX, textBaseY, normalTextPaint);
            } else {
                canvas.drawText(checkedData, text1CenterX, textBaseY, normalTextPaint);
                canvas.drawText(unCheckedData, text2CenterX, textBaseY, checkTextPaint);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                if (Math.abs(moveX - preX) > MOVE_DISTANCE) {
                    isMove = true;
                    if (moveX < PADDING) {
                        toggleStartX = PADDING;
                    } else if (moveX < centerX && moveX > PADDING) {
                        toggleStartX = Math.max(PADDING, moveX - toggleWidth / 2f);
                    } else if (moveX > centerX && moveX < centerX + toggleWidth/ 2f) {
                        toggleStartX = moveX - toggleWidth / 2f;
                    } else if (moveX >= centerX + - toggleWidth/ 2f) {
                        toggleStartX = centerX;
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    if (toggleStartX >= text1CenterX) {
                        //向右滑动超过一半
                        //关闭(执行关闭)
                        mScroller.startScroll((int) toggleStartX, 0, (int) (centerX - toggleStartX), 0);
                        isChecked = false;
                    } else {
                        //向左滑动超过一半
                        //开启（执行开启）
                        mScroller.startScroll((int) toggleStartX, 0, (int) (PADDING - toggleStartX), 0);
                        isChecked = true;
                    }
                } else {
                    if (preX > centerX && isChecked) {
                        //关闭(执行关闭)
                        mScroller.startScroll((int) toggleStartX, 0, toggleWidth, 0);
                        isChecked = false;
                    } else if (preX < centerX && !isChecked) {
                        //开启（执行开启）
                        mScroller.startScroll((int) toggleStartX, 0, -toggleWidth, 0);
                        isChecked = true;
                    }
                }
                if (mListener != null) {
                    mListener.onChange(isChecked);
                }
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            toggleStartX = mScroller.getCurrX();
            invalidate();
        }
    }

}
