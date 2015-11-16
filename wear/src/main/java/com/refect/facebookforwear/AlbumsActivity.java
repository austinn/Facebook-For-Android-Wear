package com.refect.facebookforwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.refect.facebookforwear.adapter.FeedAdapter;
import com.refect.facebookforwear.fragments.AlbumFragment;
import com.refect.facebookforwear.fragments.FeedFragment;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.Utils;

import java.util.List;

/**
 * What should happen:
 * Activity starts
 * Sends a signal to mobile asking for albums
 * BroadcastReceiver starts listening for the incoming signal
 * Creates AlbumFragments as they come in
 */
public class AlbumsActivity extends FragmentActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private ViewPager mPager;
    private FeedAdapter mPagerAdapter;
    public BroadcastReceiver receiver;
    private GoogleApiClient mApiClient;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGoogleApiClient();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new FeedAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new StackTransformer());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                progressBar.setVisibility(View.GONE);

                AlbumModel albumModel = (AlbumModel) intent.getSerializableExtra("albumModel");
                List<Fragment> allFragments = mPagerAdapter.getFragments();
                allFragments.add(AlbumFragment.newInstance(albumModel, allFragments.size() + 1));
                mPagerAdapter.setFragments(allFragments);

            }
        };

        sendMessage(Utils.WEAR_MESSAGE_PATH, Utils.REQUEST_ALBUM);
    }

    public void sendMessage(final String path, final String text ) {
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
                new IntentFilter(Utils.ALBUM_ACTIVITY)
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
}
