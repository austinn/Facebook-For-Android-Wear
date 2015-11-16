package com.refect.facebookforwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.refect.facebookforwear.adapter.FeedAdapter;
import com.refect.facebookforwear.fragments.FeedFragment;
import com.refect.shared.models.FeedModel;
import com.refect.shared.models.UserModel;
import com.refect.shared.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    public static final int SPEECH_REQUEST_CODE = 0;

    private ViewPager mPager;
    private FeedAdapter mPagerAdapter;
    private static GoogleApiClient mApiClient;
    public BroadcastReceiver receiver;

    public ProgressBar progressBar;
    public Button progressBar2;
    public FeedModel mFeedModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setAmbientEnabled();
        initGoogleApiClient();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2 = (Button) findViewById(R.id.progressBar2);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new FeedAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new StackTransformer());

        progressBar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedModel != null) {
                    sendMessage(Utils.WEAR_MESSAGE_PATH, Utils.REQUEST_FEED_PAGINATION + mFeedModel.getNextPagingUrl());
                    mPagerAdapter.clear();
                    progressBar2.setVisibility(View.GONE);
                }
            }
        });

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mPagerAdapter.getCount() - 1) {

                    progressBar2.setVisibility(View.VISIBLE);
                    Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_top);
                    progressBar2.startAnimation(slideDown);
                } else {
                    progressBar2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                progressBar.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                Log.d("MainActivity (onReceive)", mPagerAdapter.getFragments().size() + "");

                FeedModel feedModel = (FeedModel) intent.getSerializableExtra("feedModel");

                if(mFeedModel != null) {
                    Log.d("~~MainActivity (onReceive)", "Old: " + mFeedModel.getNextPagingUrl());
                }
                Log.d("~~MainActivity (onReceive)", "New: " + feedModel.getNextPagingUrl());

                //keep a local instance of the latest feedmodel
                mFeedModel = feedModel;

                //setup feed fragments
                List<Fragment> allFragments = mPagerAdapter.getFragments();
                allFragments.add(FeedFragment.newInstance(feedModel, allFragments.size() + 1));
                mPagerAdapter.setFragments(allFragments);

            }
        };

        sendMessage(Utils.WEAR_MESSAGE_PATH, Utils.REQUEST_FEED);
    }

    public static void sendMessage(final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Utils.FEED_ACTIVITY)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    public void onMessageReceived( final MessageEvent messageEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageEvent.getPath().equalsIgnoreCase(Utils.WEAR_MESSAGE_PATH)) {
                    Toast.makeText(getApplicationContext(), new String(messageEvent.getData()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void showStatusDialog() {

    }


//    @Override
//    public void onEnterAmbient(Bundle ambientDetails) {
//        super.onEnterAmbient(ambientDetails);
//        updateDisplay();
//    }
//
//    @Override
//    public void onUpdateAmbient() {
//        super.onUpdateAmbient();
//        updateDisplay();
//    }
//
//    @Override
//    public void onExitAmbient() {
//        updateDisplay();
//        super.onExitAmbient();
//    }
//
//    private void updateDisplay() {
//        if (isAmbient()) {
////            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
////            mTextView.setTextColor(getResources().getColor(android.R.color.white));
////            mClockView.setVisibility(View.VISIBLE);
////
////            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
//        } else {
////            mContainerView.setBackground(null);
////            mTextView.setTextColor(getResources().getColor(android.R.color.black));
////            mClockView.setVisibility(View.GONE);
//        }
//    }
}
