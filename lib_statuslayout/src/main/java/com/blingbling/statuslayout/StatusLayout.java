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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by BlingBling on 2016/12/22.
 */

public class StatusLayout extends FrameLayout implements View.OnClickListener {

    public static final int VIEW_TYPE_EMPTY = 0;            //空的
    public static final int VIEW_TYPE_DEFAULT = 1;          //默认布局
    public static final int VIEW_TYPE_LOADING = 2;          //加载中
    public static final int VIEW_TYPE_NO_DATA = 3;          //数据为空
    public static final int VIEW_TYPE_FAIL = 4;             //加载失败
    public static final int VIEW_TYPE_FAIL_NETWORK = 5;     //网络错误

    private int mDefaultLayoutId;
    private int mLoadingLayoutId;
    private int mNoDataLayoutId;
    private int mFailLayoutId;
    private int mFailNetWorkLayoutId;

    private int mLoadingAnimationDrawableViewId;

    private int mNoDataRetryClickViewId;
    private int mFailRetryClickViewId;
    private int mFailNetWorkRetryClickViewId;

    private OnRetryClickListener mOnRetryClickListener;
    private OnViewChangedListener mOnViewChangedListener;
    private AnimationDrawable mAnimationDrawable;
    private boolean mLoadSuccess = false;
    private Object mData;

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

        mLoadingAnimationDrawableViewId = a.getResourceId(R.styleable.StatusLayout_statusLoadingAnimationDrawableViewId, 0);

        if (mDefaultLayoutId == R.layout.status_layout_view_default) {
            mNoDataRetryClickViewId = 0;
        } else {
            mNoDataRetryClickViewId = a.getResourceId(R.styleable.StatusLayout_statusNoDataRetryClick, 0);
        }
        if (mFailLayoutId == R.layout.status_layout_view_fail) {
            mFailRetryClickViewId = R.id.status_id_retry_click;
        } else {
            mFailRetryClickViewId = a.getResourceId(R.styleable.StatusLayout_statusFailRetryClick, 0);
        }
        if (mFailNetWorkLayoutId == R.layout.status_layout_view_fail_network) {
            mFailNetWorkRetryClickViewId = R.id.status_id_retry_click;
        } else {
            mFailNetWorkRetryClickViewId = a.getResourceId(R.styleable.StatusLayout_statusFailNetWorkRetryClick, 0);
        }

        boolean enableShowDefaultView = a.getBoolean(R.styleable.StatusLayout_statusEnableShowDefaultView, true);
        a.recycle();

        if (enableShowDefaultView) {
            showDefaultView();
        } else {
            showEmptyView();
        }
    }

    private void addView(int viewType, @LayoutRes int layoutId) {
        removeAllViews();
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(layoutId, this);

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        setRetryClick(viewType, view);
        stopAnimationDrawable();
        if (viewType == VIEW_TYPE_LOADING) {
            startAnimationDrawable(view);
        }
        if (mOnViewChangedListener != null) {
            mOnViewChangedListener.onViewChanged(viewType, view, mData);
        }
    }

    private void setRetryClick(int viewType, View view) {
        int retryClickViewId = 0;
        switch (viewType) {
            case VIEW_TYPE_NO_DATA:
                retryClickViewId = mNoDataRetryClickViewId;
                break;
            case VIEW_TYPE_FAIL:
                retryClickViewId = mFailRetryClickViewId;
                break;
            case VIEW_TYPE_FAIL_NETWORK:
                retryClickViewId = mFailNetWorkRetryClickViewId;
                break;
        }
        if (retryClickViewId != 0) {
            view.findViewById(retryClickViewId).setOnClickListener(this);
        }
    }

    private void startAnimationDrawable(View view) {
        if (mLoadingAnimationDrawableViewId != 0) {
            final ImageView imageView = (ImageView) view.findViewById(mLoadingAnimationDrawableViewId);
            Drawable drawable = imageView.getDrawable();
            if (drawable == null || !(drawable instanceof AnimationDrawable)) {
                drawable = imageView.getBackground();
            }
            if (drawable != null && drawable instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) drawable;
                mAnimationDrawable.start();
            }
        }
    }

    private void stopAnimationDrawable() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
            mAnimationDrawable = null;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            stopAnimationDrawable();
            removeAllViews();
            if (mOnViewChangedListener != null) {
                mOnViewChangedListener.onViewChanged(VIEW_TYPE_EMPTY, null, mData);
            }
            mData = null;
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
     */
    public void showDefaultView() {
        addView(VIEW_TYPE_DEFAULT, mDefaultLayoutId);
    }

    public void showDefaultView(Object data) {
        mData = data;
        addView(VIEW_TYPE_DEFAULT, mDefaultLayoutId);
    }

    /**
     * 显示加载中
     */
    public void showLoadingView() {
        addView(VIEW_TYPE_LOADING, mLoadingLayoutId);
    }

    public void showLoadingView(Object data) {
        mData = data;
        addView(VIEW_TYPE_LOADING, mLoadingLayoutId);
    }

    /**
     * 显示没有数据
     */
    public void showNoDataView() {
        addView(VIEW_TYPE_NO_DATA, mNoDataLayoutId);

    }

    public void showNoDataView(Object data) {
        mData = data;
        addView(VIEW_TYPE_NO_DATA, mNoDataLayoutId);
    }

    /**
     * 显示加载失败
     */
    public void showFailView() {
        addView(VIEW_TYPE_FAIL, mFailLayoutId);
    }

    public void showFailView(Object data) {
        mData = data;
        addView(VIEW_TYPE_FAIL, mFailLayoutId);
    }

    /**
     * 显示网络错误
     */
    public void showFailNetWorkView() {
        addView(VIEW_TYPE_FAIL_NETWORK, mFailNetWorkLayoutId);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        mOnRetryClickListener = listener;
    }

    public void setOnViewChangedListener(OnViewChangedListener mOnViewChangedListener) {
        this.mOnViewChangedListener = mOnViewChangedListener;
    }

    public void setData(Object mData) {
        this.mData = mData;
    }

    public boolean isLoadSuccess() {
        return mLoadSuccess;
    }

    public void setLoadSuccess(boolean loadSuccess) {
        mLoadSuccess = loadSuccess;
    }
}