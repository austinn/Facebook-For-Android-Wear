package com.refect.facebookforwear.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.Utils;

/**
 * Created by anelson on 5/15/15.
 */
public class UserImageDownloader extends AsyncTask<String, Asset, Asset> {

    Context mCtx;
    FeedModel mFeedModel;

    public UserImageDownloader(Context ctx, FeedModel feedModel) {
        this.mCtx = ctx;
        this.mFeedModel = feedModel;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Asset doInBackground(String... strings) {
        Asset asset = null;
        try {
            asset = Asset.createFromBytes(Utils.downloadImageFromUrl(strings[0]));
            Log.d("asset", asset.toString());
        } catch(Exception e) {
            Log.d("Error:", e.toString());
        }

        return asset;
    }

    @Override
    protected void onPostExecute(Asset asset) {
        new ImageDownloader(mCtx, mFeedModel, null).execute(mFeedModel.getFullPicture());
    }
}
