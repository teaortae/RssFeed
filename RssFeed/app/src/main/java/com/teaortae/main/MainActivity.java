package com.teaortae.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.teaortae.adapters.RssAdapter;
import com.teaortae.model.RSSFeedData;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class MainActivity extends Activity {
    private static final String TOPSTORIES = "http://rss.cbs.co.kr/nocutnews.xml";
    private NewsFeedParser mNewsFeeder;
    private RecyclerView mRecyclerView;
    private RssAdapter mRssAdap;
    private List<RSSFeedData> mRssFeedList;
    private LinearLayoutManager linearLayoutManager;
    public static DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();
        if (isOnline()) {
            new DoRssFeedTask().execute(TOPSTORIES);
            imageLoaderSettings();
        } else {
            internetNotConnectedDialog();
        }
    }

    private void internetNotConnectedDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("인터넷 연결 안됨");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


    public class DoRssFeedTask extends AsyncTask<String, Void, List<RSSFeedData>> {
        ProgressDialog prog;
        Handler innerHandler;

        @Override
        protected void onPreExecute() {
            prog = new ProgressDialog(MainActivity.this);
            prog.setMessage("Loading....");
            prog.show();
        }

        @Override
        protected List<RSSFeedData> doInBackground(String... params) {
            for (String urlVal : params) {
                mNewsFeeder = new NewsFeedParser(urlVal);
            }
            mRssFeedList = mNewsFeeder.parse();
            return mRssFeedList;
        }

        @Override
        protected void onPostExecute(List<RSSFeedData> result) {
            prog.dismiss();
            if (MainActivity.this == null)
                return;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mRssAdap = new RssAdapter(MainActivity.this, R.layout.activity_main, mRssFeedList);
                    int count = mRssAdap.getItemCount();
                    if (count != 0 && mRssAdap != null) {
                        mRecyclerView.setAdapter(mRssAdap);
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RssAdapter.AnimateFirstDisplayListener.displayedImages.clear();
    }

    private void imageLoaderSettings() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this.getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();

        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_loader)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
    }

    private void setLayout() {
        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        mRssFeedList = new ArrayList<RSSFeedData>();
    }

    // network 연결 상태 확인
    public boolean isOnline() {
        try {
            ConnectivityManager conMan =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }
            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}
