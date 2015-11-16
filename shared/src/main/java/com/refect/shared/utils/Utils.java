package com.refect.shared.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Created by anelson on 7/30/15.
 */
public class Utils {

    public final static String WEAR_MESSAGE_PATH = "/facebookforwear";
    public static final String FEED_ACTIVITY = "FEED";
    public static final String ALBUM_ACTIVITY = "ALBUM";
    public static final String PHOTO_ACTIVITY = "PHOTO";

    public static final String REQUEST_FEED = "001";
    public static final String REQUEST_ALBUM = "002";
    public static final String REQUEST_PHOTOS = "003";
    public static final String REQUEST_FEED_PAGINATION = "004";
    public static final String REQUEST_POST_STATUS = "005";
    public static final String REQUEST_POST_COMMENT = "006";
    public static final String REQUEST_POST_LIKE = "007";

    public static final String LAST_ONE = "LAST_ONE";

    public final static String FEED_TYPE_STATUS = "status";
    public final static String FEED_TYPE_PHOTO = "photo";
    public final static String FEED_TYPE_LINK = "link";

    public static String printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Utils (printHashKey)", "printHashKey() Hash Key: " + hashKey);
                return hashKey;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Utils (printHashKey)", "printHashKey()", e);
            return "Could not generate key hash";
        } catch (Exception e) {
            Log.e("Utils (printHashKey)", "printHashKey()", e);
            return "Could not generate key hash";
        }
        return "Could not generate key hash";
    }

    public static String checkJsonHasString(String keyword, JSONObject json) throws JSONException {
        if(json.has(keyword)) {
            return json.getString(keyword);
        } else {
            return null;
        }
    }

    public static int checkJsonHasInt(String keyword, JSONObject json) throws JSONException {
        if(json.has(keyword)) {
            return json.getInt(keyword);
        } else {
            return -1;
        }
    }

    public static JSONObject checkJsonHasObject(String keyword, JSONObject json) throws JSONException {
        if(json.has(keyword)) {
            return json.getJSONObject(keyword);
        } else {
            return null;
        }
    }

    public static JSONArray checkJsonHasArray(String keyword, JSONObject json) throws JSONException {
        if(json.has(keyword)) {
            return json.getJSONArray(keyword);
        } else {
            return null;
        }
    }

    public static int getScreenHeight(Context c) {
        int screenHeight = 0;
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        int screenWidth = 0;
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    /**
     * Converts a resource to a bitmap
     * @param resource
     * @return
     */
    public static Bitmap resource2Bitmap(Context ctx, int resource) {
        return BitmapFactory.decodeResource(ctx.getResources(), resource);
    }

    public static byte[] resource2ByteArray(Context ctx, int resource) {
        return bitmap2ByteArray(resource2Bitmap(ctx, resource));
    }

    /**
     * Converts a bitmap into a byte array
     * @param bmp
     * @return
     */
    public static byte[] bitmap2ByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byte array into a bitmap
     * @param data
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static String convertArrayToString(Object[] array) {
        String strSeparator = "__,__";
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    /**
     *
     * @return
     */
    public static String loadJSONFromAsset(Context ctx, String jsonFileFromAssets) {
        String json = null;
        try {
            InputStream is = ctx.getAssets().open(jsonFileFromAssets);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static Bitmap loadBitmapFromAsset(GoogleApiClient mApiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mApiClient.blockingConnect(2000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mApiClient, asset).await().getInputStream();
        mApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("TAG", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static byte[] downloadImageFromUrl(String url) throws IOException {
        URL url1 = new URL(url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url1.openStream ();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", url1.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) { is.close(); }
        }

        return baos.toByteArray();
    }

}
