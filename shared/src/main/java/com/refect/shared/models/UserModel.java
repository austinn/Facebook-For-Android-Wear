package com.refect.shared.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.refect.shared.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by anelson on 7/30/15.
 */
public class UserModel implements Serializable {

    private String id;
    private String name;
    private String url;
    private Bitmap bitmap;

    public UserModel() {

    }

    public UserModel(JSONObject json) {
        Log.d("~~~UserModel (constructor)", json.toString());
        try {
            setId(Utils.checkJsonHasString("id", json));
            setName(Utils.checkJsonHasString("name", json));

            if(json.has("picture")) {
                setUrl(json.getJSONObject("picture").getJSONObject("data").getString("url"));
            }

        } catch(JSONException e) {
            Log.d("UserModel (constructor)", e.toString());
        }
    }

    public UserModel(DataMap dataMap) {
        setId(dataMap.getString("id"));
        setName(dataMap.getString("name"));
        setUrl(dataMap.getString("url"));
        //setBitmap(Utils.byteArray2Bitmap(dataMap.getByteArray("bitmap")));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Bitmap getBitmap() {
//        return bitmap;
//    }
//
//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("id", getId());
        dataMap.putString("name", getName());
//        dataMap.putByteArray("bitmap", Utils.bitmap2ByteArray(getBitmap()));
//
//        if(getUrl() != null) {
//            dataMap.putString("url", getUrl());
//        }

        return dataMap;
    }
}
