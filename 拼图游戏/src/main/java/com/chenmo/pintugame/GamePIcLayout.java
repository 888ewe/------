package com.chenmo.pintugame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者：沉默
 * 日期：2017/3/2
 * QQ:823925783
 */

public class GamePIcLayout extends RelativeLayout implements View.OnClickListener {

    private int mCount = 2;
    /**
     * 容器内边距
     */
    private int mPadding;
    /**
     * 小图片的边距
     */
    private int mMargin = 2;

    /**
     * 小图片的数量
     */
    private ImageView[] imageViews;
    /**
     * 容器的宽度
     */
    private int mWidth;
    /**
     * 小图片的宽度/高度
     */
    private int itemWidth;
    /**
     * 游戏的大图片
     */
    private Bitmap bigBitmap;

    private List<ImageBean> imageBeenslist;

    private boolean once;
    //还有多少秒
    private int mTime;

    private boolean isAniming;
    private boolean isSuccess;

    public interface GamePintuListener {
        void nextLevel(int nextLevel);

        void timeChanged(int currentTime);

        void gameOver();
    }

    private GamePintuListener mListener;


    /**
     * 设置接口回调
     *
     * @param mListener
     */
    public void setOnGamePintuListener(GamePintuListener mListener) {
        this.mListener = mListener;
    }

    private static final int TIME_CHANGED = 1;
    private static final int NEXT_LEVEL = 2;

    private boolean isTimeEabled = false;
    private int level = 1;


    /**
     * 设置开启游戏时间
     *
     * @param timeEabled
     */
    public void setTimeEabled(boolean timeEabled) {
        isTimeEabled = timeEabled;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:

                    break;
                case NEXT_LEVEL:
                    level=level+1;
                    if (mListener != null) {
                        mListener.nextLevel(level);
                    }else {
                        nextLevel();
                    }
                    break;
            }
        }
    };

    public GamePIcLayout(Context context) {
        this(context, null);
    }

    public GamePIcLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GamePIcLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        /**
         * px转dp
         */
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        if (!once) {
            //进行切图以及排序
            initBitmap();
            //设置imageVIew（item）的宽高等属性
            initItem();
            //是否
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);
    }

    //进行切图以及排序
    private void initBitmap() {
        if (bigBitmap == null) {
            bigBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        }
        imageBeenslist = SplitImage.spliteimage(bigBitmap, mCount);

        //乱序
        Collections.sort(imageBeenslist, new Comparator<ImageBean>() {
            @Override
            public int compare(ImageBean o1, ImageBean o2) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    //设置imageVIew（item）的宽高等属性
    private void initItem() {
        itemWidth = ((mWidth - mPadding * 2 - mMargin * (mCount - 1)))/mCount;
        imageViews = new ImageView[mCount * mCount];
        for (int i = 0; i < imageViews.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(imageBeenslist.get(i).getBitmap());
            imageViews[i] = item;
            item.setId(i + 1);
            item.setTag(i + "_" + imageBeenslist.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
            //设置item间隙横向间隙，通过rightMargin
            if ((i + 1) % mCount != 0) {//不是最后一列
                lp.rightMargin = mMargin;
            }
            if (i % mCount != 0) {//不是第一列
                lp.addRule(RelativeLayout.RIGHT_OF, imageViews[i - 1].getId());
            }
            if ((i + 1) > mCount) {//不是第一行
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, imageViews[i - mCount].getId());
            }
            addView(item, lp);
        }
    }

    /**
     * 获取多个参数的最小值
     *
     * @return
     */
    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }


    private ImageView mFirst;
    private ImageView mSecond;


    @Override
    public void onClick(View v) {

        if (isAniming) {
            return;
        }

        //2次点击同一个item
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#5500ff00"));
        } else {
            mSecond = (ImageView) v;

            //交换我们的item
            exchangeView();
        }
    }


    /**
     * 新建一个动画层
     */
    private RelativeLayout mAnimLayout;

    private void exchangeView() {

        mFirst.setColorFilter(null);

        //构建动画层
        setUpAnimLayout();
        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = imageBeenslist.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(itemWidth, itemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);


        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = imageBeenslist.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(itemWidth, itemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);


        //设置动画
        TranslateAnimation animation = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(),
                0, mSecond.getTop() - mFirst.getTop());
        animation.setDuration(300);
        animation.setFillAfter(true);
        first.startAnimation(animation);


        TranslateAnimation animationSecond = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(),
                0, -mSecond.getTop() + mFirst.getTop());
        animationSecond.setDuration(300);
        animationSecond.setFillAfter(true);
        second.startAnimation(animationSecond);

        //监听动画
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String seconfTag = (String) mSecond.getTag();
                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);
                mFirst.setTag(seconfTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);
                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                //成功了
                checkSuccess();
                isAniming = false;
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     * 根据tag获取Id
     *
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    /**
     * 根据tag获取下标
     *
     * @param tag
     * @return
     */
    public int getImageIndex(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构建动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }

    /**
     * 游戏成功了
     */
    private void checkSuccess() {
        isSuccess = true;

        for (int i = 0; i < imageViews.length; i++) {

            ImageView iv = imageViews[i];

            if (getImageIndex((String) iv.getTag()) != i) {//
                isSuccess = false;
            }
        }
        if (isSuccess) {
//            Toast.makeText(getContext(), "成功了", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(NEXT_LEVEL);

        }

    }


    public void nextLevel() {
        if(mCount==8) {
            Toast.makeText(getContext(), "恭喜你，已经通关了", Toast.LENGTH_SHORT).show();
            return;
        }
        this.removeAllViews();
        mAnimLayout = null;
        mCount++;
        Log.e("mcount","mcount"+mCount);
        isSuccess = false;
        initBitmap();
        initItem();

    }
}
