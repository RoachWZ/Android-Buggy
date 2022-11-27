package io.agora.customComponents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.csst.videotalk.VideoTalkActivity;

import io.agora.tutorials1v1vcall.R;

/**
 *  垂直 从中间可以向两边滑动 下负上正
 * Created by slack on 2016/12/16 10:11.
 * Modify by wz on 2022/11/27 22:07
 */
public class SeekBarViewVertical extends View {

    private static final int DEFAULT_TOUCH_TARGET_SIZE = 40;
    private final int DEFAULT_TEXT_PADDING = 10;
    private final int DEFAULT_THUMB_COLOR = Color.GRAY;

    private final Paint paint;

    private float height = 400; // need <= getHeight()

    /**
     * progress start max
     */
    private int minProgress = 0;

    /**
     * progress end max
     */
    private int maxProgress = 100;

    /**
     * 进度条的颜色 底色 背景色
     */
    @ColorInt
    private int progressBackColor = Color.BLACK;

    /**
     * 进度条的底色 宽度
     */
    private float progressBackWidth = 10;

    /**
     * 进度条 底色 圆角矩形边框 描边
     */
    @ColorInt
    private int progressFrameColor = Color.WHITE;

    /**
     * 进度条圆角矩形边框 宽度
     */
    private float progressFrameWidth = 3;

    /**
     * 进度条的颜色
     */
    @ColorInt
    private int progressColor = Color.GREEN;

    /**
     * 进度条的 宽度
     */
    private float progressWidth = 20;

    /**
     * 如果0在中间,负进度条的颜色
     */
    @ColorInt
    private int progressMinusColor = Color.RED;

    /**
     * current progress
     */
    private int progress = 50;

    /**
     * seekBar Thumb normal radius
     */
    private float mThumbNormalRadius = 14;

    /**
     * seekBar Thumb radius when touched
     */
    private float mThumbPressedRadius = 24;

    /**
     * seekBar Thumb color
     */
    @ColorInt
    private int mThumbColor = DEFAULT_THUMB_COLOR;

    private float mTextLocation = 1;
    /**
     * progress 字体大小
     */
    private float mTextSize = 40;

    /**
     * progress 字体 color
     */
    @ColorInt
    private int mTextColor = Color.WHITE;

    /**
     * progress 字体 背景 color
     */
    @ColorInt
    private int mTextBackColor = 0x7DD2D3D4;

    /**
     * progress 字体 背景 radius
     */
    private float mTextBackRadius = 10;

    /**
     * 判断是否是 0 在中间
     */
    private boolean mIsCenterState = false;
    private boolean mIsVerticalState = true;

    private float mThumbRadius = mThumbNormalRadius;
    private float progressPosition;
    private boolean isTouchLegal = false;
    private ObjectAnimator mAnimator; //  seekBar Thumb Animator
    private RectF mTextRectF, mBackRectF, mProgressRectF;
    private int mThumbDrawColor = DEFAULT_THUMB_COLOR;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private OnSeekBarProgressListener mOnSeekBarProgressListener;
    private OnSeekBarFinishedListener mOnSeekBarFinishedListener;

    public SeekBarViewVertical(Context context) {
        this(context, null);
    }

    public SeekBarViewVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarViewVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);

        if (attrs != null) {
            TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs,
                    R.styleable.SeekBarView, 0, 0);
            maxProgress = styledAttrs.getInteger(R.styleable.SeekBarView_maxProgress, 100);
            minProgress = styledAttrs.getInteger(R.styleable.SeekBarView_minProgress, 0);
            height = styledAttrs.getDimension(R.styleable.SeekBarView_height, 150);
            mIsCenterState = styledAttrs.getBoolean(R.styleable.SeekBarView_centerState, false);
            mIsVerticalState = styledAttrs.getBoolean(R.styleable.SeekBarView_VerticalState, true);
            progressBackColor = styledAttrs.getColor(R.styleable.SeekBarView_backColor, Color.BLACK);
            progressBackWidth = styledAttrs.getDimension(R.styleable.SeekBarView_backWidth, 10);
            progressFrameColor = styledAttrs.getColor(R.styleable.SeekBarView_backFrameColor, Color.WHITE);
            progressFrameWidth = styledAttrs.getDimension(R.styleable.SeekBarView_backFrameSize, 3);
            progressColor = styledAttrs.getColor(R.styleable.SeekBarView_progressColor, Color.GREEN);
            progressWidth = styledAttrs.getDimension(R.styleable.SeekBarView_progressWidth, progressBackWidth);
            progressMinusColor = styledAttrs.getColor(R.styleable.SeekBarView_progressMinusColor, Color.RED);
            progress = styledAttrs.getInteger(R.styleable.SeekBarView_progress, 50);
            mThumbNormalRadius = styledAttrs.getDimension(R.styleable.SeekBarView_thumbNormalRadius, 14);
            mThumbPressedRadius = styledAttrs.getDimension(R.styleable.SeekBarView_thumbPressRadius, 24);
            mThumbColor = styledAttrs.getColor(R.styleable.SeekBarView_thumbColor, Color.BLUE);
            progressColor = styledAttrs.getColor(R.styleable.SeekBarView_progressColor, Color.BLUE);
            mTextLocation = styledAttrs.getInteger(R.styleable.SeekBarView_textLocation, 1);
            mTextColor = styledAttrs.getColor(R.styleable.SeekBarView_textColor, Color.WHITE);
            mTextSize = styledAttrs.getDimension(R.styleable.SeekBarView_textSize, 40);
            mTextBackColor = styledAttrs.getColor(R.styleable.SeekBarView_textBackColor, 0x7DD2D3D4);
            mTextBackRadius = styledAttrs.getDimension(R.styleable.SeekBarView_textBackRadius, 10);

            mThumbRadius = mThumbNormalRadius;
            mThumbDrawColor = mThumbColor;
            styledAttrs.recycle();
        }

        mAnimator = getTargetAnimator(false);
        mTextRectF = new RectF();
        mBackRectF = new RectF();
        mProgressRectF = new RectF();
    }

    /**
     * 设置是否是 0 在中间
     *
     * @param enable
     */
    public SeekBarViewVertical setCenterModeEnable(boolean enable) {
        // 将负值 变成正值
        if (mIsCenterState) {
            if (!enable) {
                if (progress < 0) {
                    progress = -progress;
                }
            }
        }
        mIsCenterState = enable;
        invalidate();
        return this;
    }

    public SeekBarViewVertical setVerticalModeEnable(boolean enable) {
        mIsVerticalState = enable;
        invalidate();
        return this;
    }

    public SeekBarViewVertical setProgress(int progress) {
        if (mIsCenterState) {
            if (progress <= maxProgress && progress >= minProgress - maxProgress) {
                this.progress = progress;
            } else {
                this.progress = minProgress;
            }
        } else {
            if (progress <= maxProgress && progress >= minProgress) {
                this.progress = progress;
            } else {
                this.progress = minProgress;
            }
        }
        invalidate();
        return this;
    }

    /**
     * 是否可用
     */
    public SeekBarViewVertical setProgressEnable(boolean enable) {
        if (enable) {
            this.setEnabled(true);
            mThumbDrawColor = mThumbColor;
        } else {
            this.setEnabled(false);
            this.progress = 0;
            mThumbDrawColor = DEFAULT_THUMB_COLOR;
        }
        invalidate();
        return this;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(!mIsVerticalState){
            canvas.rotate(-90);
            canvas.translate(-getHeight(),0);
        }
        super.onDraw(canvas);
//        Log.i("slack","onDraw... " + mThumbRadius);
        int centerX = getWidth() / 2; // x 是center
        int centerY = getHeight() / 2; // y 是center
        float startY = centerY - height / 2 ;
//        Log.d("begain first ", "startY : " + startY +" centerY :"+centerY);

        // draw background line
        paint.setColor(progressBackColor);
        paint.setStrokeWidth(progressBackWidth);
        paint.setStyle(Paint.Style.FILL); // 实心
        mBackRectF.left = centerX;
        mBackRectF.top = startY + height;
        mBackRectF.right = centerX - progressBackWidth;
        mBackRectF.bottom = startY;
        canvas.drawRoundRect(mBackRectF, mTextBackRadius, mTextBackRadius, paint);
        paint.setColor(progressFrameColor);
        paint.setStrokeWidth(progressFrameWidth);
        paint.setStyle(Paint.Style.STROKE); // 空心
        canvas.drawRoundRect(mBackRectF, mTextBackRadius, mTextBackRadius, paint);

//        canvas.drawLine(startY, centerX, centerY + height / 2, centerX, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(progressColor);

        // draw progress
        paint.setStrokeWidth(progressWidth);//设置画笔宽度
        paint.setColor(progressColor);
        if (mIsCenterState) {
//            if (progress < 0) {
//                paint.setColor(progressMinusColor);
//            }
            startY = centerY;
            progressPosition = startY + (int) ((progress * (height / 2f) / (maxProgress - minProgress)));
        } else {
            progressPosition = startY + ((progress * height / (maxProgress - minProgress)));
        }
        mProgressRectF.right = centerX - progressBackWidth;
        mProgressRectF.left = centerX;
        if (progress > 0) {
            mProgressRectF.bottom = startY;
            mProgressRectF.top = progressPosition;
        } else {
            mProgressRectF.bottom = progressPosition;
            mProgressRectF.top = startY;
        }
        canvas.drawRoundRect(mProgressRectF, mTextBackRadius, mTextBackRadius, paint);

//        canvas.drawLine(startY, centerX, progressPosition, centerX, paint);

        // draw point
        paint.setColor(mThumbDrawColor);
        canvas.drawCircle(centerX - progressBackWidth / 2 , progressPosition, mThumbRadius, paint);
//        Log.d("begain first ", "progressPosition : " + progressPosition );

        /** mThumbRadius will change
         * mThumbRadius  : mThumbNormalRadius  ----------------- mThumbPressedRadius
         *  alpha        :  0                  ------------------ 255
         *
         */
        int alpha = (int) (255 * (mThumbRadius - mThumbNormalRadius) / (mThumbPressedRadius - mThumbNormalRadius));

        // draw text progress  up the Thumb  code is ok

        if (mTextLocation == 1) {
            paint.setColor(mTextBackColor);
            paint.setAlpha(alpha);
            mTextRectF.left = centerX - mThumbPressedRadius - DEFAULT_TEXT_PADDING;
            mTextRectF.top = progressPosition + mTextSize + DEFAULT_TEXT_PADDING;
            mTextRectF.right = mTextRectF.left - mTextSize - DEFAULT_TEXT_PADDING * 3;
            mTextRectF.bottom = progressPosition - mTextSize - DEFAULT_TEXT_PADDING;
            canvas.drawRoundRect(mTextRectF, mTextBackRadius, mTextBackRadius, paint);
            paint.setTextSize(mTextSize);
            paint.setColor(mTextColor);
            paint.setAlpha(alpha);
            // 为了让text 在背景的中心
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(-progress + "%",mTextRectF.left - DEFAULT_TEXT_PADDING * 2 , progressPosition, paint);
        } else if (mTextLocation == 2) {
            // draw text in Thumb
            paint.setTextSize(mTextSize);
            paint.setColor(mTextColor);
            paint.setAlpha(alpha);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(-progress + "%", centerX, progressPosition, paint);
        }
    }

//    private int mLastProgress;
    private long mLastTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("slack","onTouchEvent " + event.toString());
        if (!isEnabled())
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkTouchingTarget(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchLegal) {
                    if(!mIsVerticalState){
                        progress = (int) clamp((int) event.getRawX() - getLeft());
                    }else {
                        progress = (int) clamp((int) event.getRawY() - getTop());
                    }
//                    if (mLastProgress == progress) {
//                        // 两次一样就不需要重画
//                        break;
//                    }

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - mLastTime < 50) {
                        // 刷新 FPS 不超过 20 fps
                        break;
                    }
                    mLastTime = currentTime;

//                    Log.i("slack","progress " + progress);
                    invalidate(); // 在UI线程中使用 刷新View
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onProgress(progress);
                    } else if (mOnSeekBarProgressListener != null) {
                        mOnSeekBarProgressListener.onProgress(progress);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                progress = 0;//松开时归0，回到中间位置
                invalidate();
//                mLastProgress = -1;
                if (isTouchLegal) {
                    mAnimator.cancel();
                    mAnimator = getTargetAnimator(false);
                    mAnimator.start();
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onFinished(progress);
                    } else if (mOnSeekBarFinishedListener != null) {
                        mOnSeekBarFinishedListener.onFinished(progress);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * if touch , seekBar Thumb Animation
     */
    private void checkTouchingTarget(MotionEvent event) {
        if (isTouchingTarget(event)) {
            mAnimator.cancel();
            mAnimator = getTargetAnimator(true);
            mAnimator.start();
        }
    }

    /**
     * 判断是否 touch 在 seekBar thumb 上
     *
     * @param event
     * @return
     */
    private boolean isTouchingTarget(MotionEvent event) {
        if(!mIsVerticalState){
            float location = progressPosition + getLeft();
            isTouchLegal = event.getRawX() > location - DEFAULT_TOUCH_TARGET_SIZE
                    && event.getRawX() < location + DEFAULT_TOUCH_TARGET_SIZE;
        }else {
            float location = progressPosition + getTop();
            isTouchLegal = event.getRawY() > location - DEFAULT_TOUCH_TARGET_SIZE
                    && event.getRawY() < location + DEFAULT_TOUCH_TARGET_SIZE;
        }
        //D.i("slack", "isTouchLegal " + isTouchLegal + " "  + event.getRawX() + " " + event.getRawY() + " " + progressPosition);
//        Log.d("slack", "isTouchLegal " + isTouchLegal + " " + event.getRawX() + " " + event.getRawY() + " " + progressPosition);

        return isTouchLegal;
    }

    /**
     * 自定义动画  ofFloat(Object target, String propertyName, float... values)
     * 第一个参数用于指定这个动画要操作的是哪个控件
     * 第二个参数用于指定这个动画要操作这个控件的哪个属性
     * 第三个参数是可变长参数，这个就跟ValueAnimator中的可变长参数的意义一样了，就是指这个属性值是从哪变到哪
     * 对于自定义的属性,d第二个参数需要提供setXXX 方法,like:  public void setMThumbRadius(int mThumbRadius)
     * set方法 属性的第一个字母记得要大写 !  ObjectAnimator.ofInt , ofInt 对应参数的类型,如果float 为ofFloat
     */
    private ObjectAnimator getTargetAnimator(boolean touching) {
        final ObjectAnimator anim = ObjectAnimator.ofFloat(this,
                "mThumbRadius",
                mThumbRadius,
                touching ? mThumbPressedRadius : mThumbNormalRadius);
        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                Log.i("slack","onAnimationUpdate...");
                postInvalidate(); // 在子线程中使用刷新View
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                Log.i("slack","onAnimationEnd...");
                anim.removeAllListeners();
            }
        });
        anim.setInterpolator(new AccelerateInterpolator());
        return anim;
    }

    public void setMThumbRadius(float mThumbRadius) {
//        Log.i("slack","setmThumbRadius...");
        this.mThumbRadius = mThumbRadius;
    }

    /**
     * return  progress
     * -maxProgress          minProgress              maxProgress
     * \------------------------0---------------------------\
     * min                   center     touch-->\          max
     * (min center touch max are positions in the screen)
     * touch progress = (touch - center) / (max - center) * maxProgress;
     */
    private float clamp(int value) {
        if (mIsCenterState) {
            int centerY = getHeight() / 2;
            float min = centerY - height / 2;// the start point
            float max = centerY + height / 2;// the end point
            if (value > centerY) {
                if (value >= max) {
                    return maxProgress;
                } else {
                    return (int) ((maxProgress - minProgress) * (value - centerY) / (height / 2f));
                }
            } else if (value < centerY) {
                if (value <= min) {
                    return -maxProgress;
                } else {
                    return (int) ((maxProgress - minProgress) * (value - centerY) / (height / 2f));
                }
            } else {
                return minProgress;
            }
        } else {
            int centerY = getHeight() / 2;
            float min = centerY - height / 2;// the start point
            float max = centerY + height / 2;// the end point
            if (value >= max) {
                return maxProgress;
            } else if (value <= min) {
                return minProgress;
            } else {
                return (maxProgress - minProgress) * (value - min) / height;
            }
        }
    }

    public int getProgress() {
        return progress;
    }

    public SeekBarViewVertical setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
        return this;
    }

    public SeekBarViewVertical setOnSeekBarProgressListener(OnSeekBarProgressListener l) {
        mOnSeekBarProgressListener = l;
        return this;
    }

    public SeekBarViewVertical setOnSeekBarFinishedListener(OnSeekBarFinishedListener l) {
        mOnSeekBarFinishedListener = l;
        return this;
    }

    public interface OnSeekBarProgressListener {
        void onProgress(int progress);
    }
 
    public interface OnSeekBarFinishedListener {
        void onFinished(int progress);
    }
 
    public interface OnSeekBarChangeListener extends OnSeekBarProgressListener, OnSeekBarFinishedListener {
    }

}