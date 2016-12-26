package com.blingbling.statuslayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blingbling.statuslayout.OnRetryClickListener;
import com.blingbling.statuslayout.OnViewChangedListener;
import com.blingbling.statuslayout.StatusLayout;

/**
 * Created by BlingBling on 2016/12/22.
 */

public class SampleCustomActivity extends AppCompatActivity implements OnRetryClickListener, OnViewChangedListener {

    private StatusLayout mStatusLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_custom);
        mStatusLayout = (StatusLayout) findViewById(R.id.status_layout);
        mStatusLayout.setOnRetryClickListener(this);
        mStatusLayout.setOnViewChangedListener(this);
    }

    int i = 1;

    public void change(View view) {
        switch (i++) {
            case 1:
                mStatusLayout.showLoadingView();
                break;
            case 2:
                mStatusLayout.showNoDataView();
                break;
            case 3:
                mStatusLayout.showFailView("fail->" + System.currentTimeMillis());
                break;
            case 4:
                mStatusLayout.showFailNetWorkView();
                break;
            case 5:
                mStatusLayout.showEmptyView();
                break;
            case 6:
                i = 1;
                mStatusLayout.showDefaultView();
                break;
        }
    }

    @Override
    public void onRetryClick(View view) {
        Toast.makeText(this, "Click Retry", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewChanged(int viewType, View view, Object data) {
        if (viewType == StatusLayout.VIEW_TYPE_FAIL) {
            TextView fail = (TextView) view.findViewById(R.id.tv_fail);
            fail.setText((String) data);
        }
    }
}
