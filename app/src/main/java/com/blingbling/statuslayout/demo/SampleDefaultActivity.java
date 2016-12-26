package com.blingbling.statuslayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.blingbling.statuslayout.OnRetryClickListener;
import com.blingbling.statuslayout.StatusLayout;

/**
 * Created by BlingBling on 2016/12/22.
 */

public class SampleDefaultActivity extends AppCompatActivity implements OnRetryClickListener {

    private StatusLayout mStatusLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_default);
        mStatusLayout = (StatusLayout) findViewById(R.id.status_layout);
        mStatusLayout.setOnRetryClickListener(this);
        mStatusLayout.showEmptyView();
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
                mStatusLayout.showFailView();
                break;
            case 4:
                mStatusLayout.showFailNetWorkView();
                break;
            case 5:
                i = 1;
                mStatusLayout.showEmptyView();
                break;
        }
    }

    public void click(View view) {
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetryClick(View view) {
        Toast.makeText(this, "Click Retry", Toast.LENGTH_SHORT).show();
    }
}
