package com.refect.shared.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.refect.shared.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by anelson on 8/6/15.
 */
public class PhotoModel implements Serializable {

    private String id;
    private String name;
    private String url;
    private String link;

    private byte[] data;

    public PhotoModel() {

    }

    public PhotoModel(JSONObject json) {
        try {
            setId(Utils.checkJsonHasString("id", json));
            setName(Utils.checkJsonHasString("name", json));
            setUrl(Utils.checkJsonHasString("picture", json));
            setLink(Utils.checkJsonHasString("link", json));

            setImage(json);
        } catch(JSONException e) {
            Log.e("PhotoModel (constructor)", e.toString());
        }
    }

    public PhotoModel(DataMap dataMap) {
        setId(dataMap.getString("id"));
        setName(dataMap.getString("name"));
        setUrl(dataMap.getString("picture"));
        setLink(dataMap.getString("link"));
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("id", getId());
        dataMap.putString("name", getName());
        dataMap.putString("url", getUrl());
        dataMap.putString("link", getLink());

        return dataMap;
    }


    private void setImage(JSONObject json) throws JSONException {

        ImageWrapper theImage = null;
        Map<Integer, ImageWrapper> map = new TreeMap<>();
        JSONArray arr = json.getJSONArray("images");
        for(int i = 0; i < arr.length(); i++) {
            ImageWrapper image = new ImageWrapper(arr.getJSONObject(i).getString("source"),
                    arr.getJSONObject(i).getInt("width"), arr.getJSONObject(i).getInt("height"));
            map.put(i, image);

            if(image.getWidth() > 300 && image.getWidth() < 400) {
                theImage = image;
            }
        }

        if(theImage == null) {
            theImage = map.get(0);
        }

        setUrl(theImage.getUrl());
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    private class ImageWrapper {
        private String url;
        private int width;
        private int height;

        public ImageWrapper(String url, int width, int height) {
            setUrl(url);
            setWidth(width);
            setHeight(height);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
