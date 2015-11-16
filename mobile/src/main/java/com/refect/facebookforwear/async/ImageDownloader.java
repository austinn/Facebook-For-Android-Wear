package com.refect.facebookforwear.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.models.PhotoModel;
import com.refect.shared.utils.Utils;

/**
 * Created by anelson on 5/15/15.
 */
public class ImageDownloader extends AsyncTask<String, Asset, Asset> {

    Context mCtx;
    FeedModel mFeedModel;
    AlbumModel mAlbumModel;
    PhotoModel mPhotoModel;
    GoogleApiClient mApiClient;
    String lastOne;

    public ImageDownloader(Context ctx, FeedModel feedModel, String lastOne) {
        this.mCtx = ctx;
        this.mFeedModel = feedModel;
        this.lastOne = lastOne;
    }

    public ImageDownloader(Context ctx, AlbumModel albumModel) {
        this.mCtx = ctx;
        this.mAlbumModel = albumModel;
    }

    public ImageDownloader(Context ctx, PhotoModel photoModel) {
        this.mCtx = ctx;
        this.mPhotoModel = photoModel;
    }

    @Override
    protected void onPreExecute() {
        mApiClient = new GoogleApiClient.Builder( mCtx )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    protected Asset doInBackground(String... strings) {
        Asset asset = null;
        try {
            asset = Asset.createFromBytes(Utils.downloadImageFromUrl(strings[0]));
            Log.d("asset", asset.toString());
        } catch(Exception e) {
            Log.d("ImageDownloader (doInBackground):", e.toString());
        }

        return asset;
    }

    @Override
    protected void onPostExecute(Asset asset) {

        if(mFeedModel != null) {
            sendFeedModel(asset);
        } else if(mAlbumModel != null) {
            sendAlbumModel(asset);
        } else if(mPhotoModel != null) {
            sendPhotoModel(asset);
        }
    }

    /**
     *
     * @param asset
     */
    private void sendFeedModel(Asset asset) {
        String path = "/feed";

        PutDataMapRequest putRequest = PutDataMapRequest.create(path);
        DataMap map = putRequest.getDataMap();
        map.putString("lastOne", lastOne);
        map.putDataMap("feed", mFeedModel.toDataMap());

        if(asset != null) {
            map.putAsset("image", asset);
            map.putLong("dataSize", asset.getData().length);
        }

        Wearable.DataApi.putDataItem(mApiClient, putRequest.asPutDataRequest());
    }

    /**
     *
     * @param asset
     */
    private void sendAlbumModel(Asset asset) {
        String path = "/album";

        PutDataMapRequest putRequest = PutDataMapRequest.create(path);
        DataMap map = putRequest.getDataMap();
        map.putDataMap("album", mAlbumModel.toDataMap());

        if(asset != null) {
            map.putAsset("image", asset);
            map.putLong("dataSize", asset.getData().length);
        }

        Wearable.DataApi.putDataItem(mApiClient, putRequest.asPutDataRequest());
    }

    /**
     *
     * @param asset
     */
    private void sendPhotoModel(Asset asset) {
        String path = "/photo";

        PutDataMapRequest putRequest = PutDataMapRequest.create(path);
        DataMap map = putRequest.getDataMap();
        map.putDataMap("photo", mPhotoModel.toDataMap());

        if(asset != null) {
            map.putAsset("image", asset);
            map.putLong("dataSize", asset.getData().length);
        }

        Wearable.DataApi.putDataItem(mApiClient, putRequest.asPutDataRequest());
    }
}
