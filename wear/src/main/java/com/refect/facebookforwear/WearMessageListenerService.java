package com.refect.facebookforwear;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.refect.facebookforwear.fragments.FeedFragment;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.CommentSummaryModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.models.PhotoModel;
import com.refect.shared.utils.Utils;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulruiz on 9/26/14.
 */
public class WearMessageListenerService extends WearableListenerService implements DataApi.DataListener{

    private static final String START_ACTIVITY = Utils.WEAR_MESSAGE_PATH;

    private LocalBroadcastManager broadcaster;
    private GoogleApiClient mApiClient;

    public WearMessageListenerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {
            //Toast.makeText(this, new String(messageEvent.getData()), Toast.LENGTH_LONG).show();
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getDataItem().getUri().getPath().equals("/feed")) {
                doFeedStuff(event);
            } else if(event.getDataItem().getUri().getPath().equals("/album")) {
                doAlbumStuff(event);
            } else if(event.getDataItem().getUri().getPath().equals("/photo")) {
                doPhotoStuff(event);
            }
        }
    }

    /**
     *
     * @param event
     */
    private void doFeedStuff(DataEvent event) {
        DataItem item = event.getDataItem();
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        Bitmap bitmap = null;

        DataMap feedMap = dataMap.getDataMap("feed");
        FeedModel feedModel = new FeedModel(feedMap);

        String lastOne = null;
        if(dataMap.containsKey("lastOne")) {
            lastOne = Utils.LAST_ONE;
        }

        if(dataMap.containsKey("image")) {
            Asset profileAsset = dataMap.getAsset("image");
            try {
                bitmap = Utils.loadBitmapFromAsset(mApiClient, profileAsset);
                feedModel.setData(Utils.bitmap2ByteArray(bitmap));
                bitmap.recycle();
            } catch(OutOfMemoryError e) {
                Log.e("WearMessageListenerService (doFeedStuff)", e.toString());
            } catch(NullPointerException e) {
                Log.e("WearMessageListenerService (doFeedStuff)", e.toString());
            }
        }

        Intent intent = new Intent(Utils.FEED_ACTIVITY);
        intent.putExtra("lastOne", Utils.LAST_ONE);
        intent.putExtra("feedModel", feedModel);

        broadcaster.sendBroadcast(intent);
    }

    /**
     *
     * @param event
     */
    private void doAlbumStuff(DataEvent event) {
        DataItem item = event.getDataItem();
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        Bitmap bitmap = null;

        DataMap albumMap = dataMap.getDataMap("album");
        AlbumModel albumModel = new AlbumModel(albumMap);

        if(dataMap.containsKey("image")) {
            Asset profileAsset = dataMap.getAsset("image");

            try {
                bitmap = Utils.loadBitmapFromAsset(mApiClient, profileAsset);
                albumModel.setData(Utils.bitmap2ByteArray(bitmap));
                bitmap.recycle();
            } catch(OutOfMemoryError e) {
                Log.e("WearMessageListenerService (doAlbumStuff)", e.toString());
            }
        }

        Intent intent = new Intent(Utils.ALBUM_ACTIVITY);
        intent.putExtra("albumModel", albumModel);

        broadcaster.sendBroadcast(intent);
    }

    /**
     *
     * @param event
     */
    private void doPhotoStuff(DataEvent event) {
        DataItem item = event.getDataItem();
        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
        Bitmap bitmap = null;

        DataMap photoMap = dataMap.getDataMap("photo");
        PhotoModel photoModel = new PhotoModel(photoMap);

        if(dataMap.containsKey("image")) {
            Asset profileAsset = dataMap.getAsset("image");

            try {
                bitmap = Utils.loadBitmapFromAsset(mApiClient, profileAsset);
                photoModel.setData(Utils.bitmap2ByteArray(bitmap));
                bitmap.recycle();
            } catch(OutOfMemoryError e) {
                Log.e("WearMessageListenerService (doPhotoStuff)", e.toString());
            }
        }

        Intent intent = new Intent(Utils.PHOTO_ACTIVITY);
        intent.putExtra("photoModel", photoModel);

        broadcaster.sendBroadcast(intent);
    }

}
