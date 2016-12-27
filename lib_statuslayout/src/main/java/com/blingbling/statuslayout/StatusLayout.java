package com.blingbling.statuslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BlingBling on 2016/12/22.
 */

public class StatusLayout extends FrameLayout implements View.OnClickListener {

    public static final String TAG = "StatusLayout";

    public static final int VIEW_TYPE_EMPTY = 0;            //空的
    public static final int VIEW_TYPE_DEFAULT = 1;          //默认布局
    public static final int VIEW_TYPE_LOADING = 2;          //加载中
    public static final int VIEW_TYPE_NO_DATA = 3;          //数据为空
    public static final int VIEW_TYPE_FAIL = 4;             //加载失败
    public static final int VIEW_TYPE_FAIL_NETWORK = 5;     //网络错误

    protected int mDefaultLayoutId;
    protected int mLoadingLayoutId;
    protected int mNoDataLayoutId;
    protected int mFailLayoutId;
    protected int mFailNetWorkLayoutId;

    private OnRetryClickListener mOnRetryClickListener;
    private AnimationDrawable mAnimationDrawable;
    private OnAnimListener mOnAnimListener;

    private boolean mNeedLoad = true;

    public StatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        if (getBackground() == null) {
            setBackgroundColor(Color.WHITE);
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusLayout);
        mDefaultLayoutId = a.getResourceId(R.styleable.StatusLayout_statusDefaultLayoutId, R.layout.status_layout_view_default);
        mLoadingLayoutId = a.getResourceId(R.styleable.StatusLayout_statusLoadingLayoutId, R.layout.status_layout_view_loading);
        mNoDataLayoutId = a.getResourceId(R.styleable.StatusLayout_statusNoDataLayoutId, R.layout.status_layout_view_no_data);
        mFailLayoutId = a.getResourceId(R.styleable.StatusLayout_statusFailLayoutId, R.layout.status_layout_view_fail);
        mFailNetWorkLayoutId = a.getResourceId(R.styleable.StatusLayout_statusFailNetWorkLayoutId, R.layout.status_layout_view_fail_network);

        boolean enableShowDefaultView = a.getBoolean(R.styleable.StatusLayout_statusEnableShowDefaultView, true);
        a.recycle();

        if (enableShowDefaultView) {
            showDefaultView();
        } else {
            showEmptyView();
        }
    }

    protected View addView(int viewType, @LayoutRes int layoutId, String string) {
        removeAllViews();
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(layoutId, this);

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        stopAnimationDrawable();
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                startAnimationDrawable(view);
                break;
            case VIEW_TYPE_NO_DATA:
            case VIEW_TYPE_FAIL:
            case VIEW_TYPE_FAIL_NETWORK:
                setRetryClick(view);
                break;
        }
        setText(view, string);
        return view;
    }

    protected void setRetryClick(View view) {
        final View retryClickView = view.findViewById(R.id.status_id_retry_click_view);
        if (retryClickView != null) {
            retryClickView.setOnClickListener(this);
        }
    }

    protected void startAnimationDrawable(View view) {
        final View animView = view.findViewById(R.id.status_id_anim_view);
        if (animView != null) {
            if (animView instanceof OnAnimListener) {
                mOnAnimListener = (OnAnimListener) animView;
                mOnAnimListener.animStart();
            } else if (animView instanceof ImageView) {
                final ImageView imageView = (ImageView) animView;
                Drawable drawable = imageView.getDrawable();
                if (drawable == null || !(drawable instanceof AnimationDrawable)) {
                    drawable = imageView.getBackground();
                }
                if (drawable != null && drawable instanceof AnimationDrawable) {
                    mAnimationDrawable = (AnimationDrawable) drawable;
                    mAnimationDrawable.start();
                }
            } else {
                final Drawable drawable = animView.getBackground();
                if (drawable != null && drawable instanceof AnimationDrawable) {
                    mAnimationDrawable = (AnimationDrawable) drawable;
                    mAnimationDrawable.start();
                }
            }
        }
    }

    protected void stopAnimationDrawable() {
        if (mOnAnimListener != null) {
            mOnAnimListener.animStop();
            mOnAnimListener = null;
        } else if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }
    }

    protected void setText(View view, String string) {
        if (!TextUtils.isEmpty(string)) {
            final TextView textView = (TextView) view.findViewById(R.id.status_id_text_view);
            if (textView != null) {
                textView.setText(string);
            } else {
                Log.e(TAG, "If you want to customize the text, set the corresponding Layout TextView ID for status_id_text_view.");
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            stopAnimationDrawable();
            removeAllViews();
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnRetryClickListener != null) {
            mOnRetryClickListener.onRetryClick(v);
        }
    }

    /**
     * 清空隐藏
     */
    public void showEmptyView() {
        setVisibility(GONE);
    }

    /**
     * 显示默认布局
     *
     * @return
     */
    public View showDefaultView() {
        return addView(VIEW_TYPE_DEFAULT, mDefaultLayoutId, null);
    }

    /**
     * 显示默认布局
     *
     * @param string 对id为status_id_text_view的TextView设置text
     * @return
     */
    public View showDefaultView(String string) {
        return addView(VIEW_TYPE_DEFAULT, mDefaultLayoutId, string);
    }

    /**
     * 显示加载中布局
     *
     * @return
     */
    public View showLoadingView() {
        return addView(VIEW_TYPE_LOADING, mLoadingLayoutId, null);
    }

    /**
     * 显示加载中布局
     *
     * @param string 对id为status_id_text_view的TextView设置text
     * @return
     */
    public View showLoadingView(String string) {
        return addView(VIEW_TYPE_LOADING, mLoadingLayoutId, string);
    }

    /**
     * 显示没有数据布局
     *
     * @return
     */
    public View showNoDataView() {
        return addView(VIEW_TYPE_NO_DATA, mNoDataLayoutId, null);
    }

    /**
     * 显示没有数据布局
     *
     * @param string 对id为status_id_text_view的TextView设置text
     * @return
     */
    public View showNoDataView(String string) {
        return addView(VIEW_TYPE_NO_DATA, mNoDataLayoutId, string);
    }

    /**
     * 显示加载失败布局
     *
     * @return
     */
    public View showFailView() {
        return addView(VIEW_TYPE_FAIL, mFailLayoutId, null);
    }

    /**
     * 显示加载失败布局
     *
     * @param string 对id为status_id_text_view的TextView设置text
     * @return
     */
    public View showFailView(String string) {
        return addView(VIEW_TYPE_FAIL, mFailLayoutId, string);
    }

    /**
     * 显示网络错误布局
     *
     * @return
     */
    public View showFailNetWorkView() {
        return addView(VIEW_TYPE_FAIL_NETWORK, mFailNetWorkLayoutId, null);
    }

    /**
     * 显示网络错误布局
     *
     * @param string 对id为status_id_text_view的TextView设置text
     * @return
     */
    public View showFailNetWorkView(String string) {
        return addView(VIEW_TYPE_FAIL_NETWORK, mFailNetWorkLayoutId, string);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        mOnRetryClickListener = listener;
    }

    public boolean isNeedLoad() {
        return mNeedLoad;
    }

    public void setNeedLoad(boolean needLoad) {
        mNeedLoad = needLoad;
    }
}