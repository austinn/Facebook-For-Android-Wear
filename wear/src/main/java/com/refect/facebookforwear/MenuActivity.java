package com.refect.facebookforwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.refect.facebookforwear.fragments.FeedFragment;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MenuActivity extends FragmentActivity {

    TextView tvLogo;
    ImageView ivMeFeed;
    ImageView ivUserProfile;
    ImageView ivPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvLogo = (TextView) findViewById(R.id.tv_logo);
        ivMeFeed = (ImageView) findViewById(R.id.iv_me_feed);
        ivUserProfile = (ImageView) findViewById(R.id.iv_user_profile);
        ivPhotos = (ImageView) findViewById(R.id.iv_photos);

        Animation grow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.grow_circle);
        grow.setStartOffset(500);
        tvLogo.startAnimation(grow);

        /**
         *
         */
        ivMeFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        /**
         *
         */
        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        /**
         *
         */
        ivPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlbumsActivity.class);
                startActivity(intent);
            }
        });
    }
}
