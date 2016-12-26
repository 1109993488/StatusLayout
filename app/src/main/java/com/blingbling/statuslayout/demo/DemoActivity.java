package com.blingbling.statuslayout.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blingbling.statuslayout.OnRetryClickListener;
import com.blingbling.statuslayout.StatusLayout;

/**
 * Created by BlingBling on 2016/12/22.
 */

public class DemoActivity extends AppCompatActivity implements OnRetryClickListener {

    private StatusLayout mStatusLayout;
    private boolean isError = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mStatusLayout = (StatusLayout) findViewById(R.id.status_layout);
        mStatusLayout.setOnRetryClickListener(this);
        onRefresh();
    }

    @Override
    public void onRetryClick(View view) {
        onRefresh();
    }

    private void onRefresh() {
        if (!mStatusLayout.isLoadSuccess()) {
            mStatusLayout.showLoadingView();
        }
        new RequestTask().execute();
    }

    public class RequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2222);
            } catch (InterruptedException e) {
            }
            if (isError) {
                isError = false;
                return "error";
            } else {
                return "success";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            if ("success".equals(string)) {
                mStatusLayout.setLoadSuccess(true);
                mStatusLayout.showEmptyView();
            } else {
                mStatusLayout.showFailView();
            }
        }
    }

}
