package com.refect.shared.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;
import com.refect.shared.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anelson on 8/6/15.
 */
public class AlbumModel implements Serializable {

    private String id;
    private String name;
    private int count;
    private ArrayList<PhotoModel> photos;

    private byte[] data;

    public AlbumModel() {

    }

    public AlbumModel(JSONObject json) {
        try {
            setId(json.getString("id"));
            setName(json.getString("name"));
            setCount(Utils.checkJsonHasInt("count", json));

            ArrayList<PhotoModel> photos = new ArrayList<>();
            JSONArray photosArray = json.getJSONObject("photos").getJSONArray("data");
            for(int i = 0; i < photosArray.length(); i++) {
                photos.add(new PhotoModel(photosArray.getJSONObject(i)));
            }
            setPhotos(photos);

        } catch (JSONException e) {
            Log.e("AlbumModel (constructor)", e.toString());
        }
    }

    public AlbumModel(DataMap dataMap) {
        setId(dataMap.getString("id"));
        setName(dataMap.getString("name"));
        setCount(dataMap.getInt("count"));

        if(dataMap.containsKey("photos")) {
            ArrayList<PhotoModel> photos = new ArrayList<>();
            for (DataMap photo : dataMap.getDataMapArrayList("photos")) {
                photos.add(new PhotoModel(photo));
            }
            setPhotos(photos);
        }
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoModel> photos) {
        this.photos = photos;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("id", getId());
        dataMap.putString("name", getName());
        dataMap.putInt("count", getCount());

        if(getPhotos() != null) {
            ArrayList<DataMap> photosDataMapList = new ArrayList<>();
            for (PhotoModel photo : getPhotos()) {
                photosDataMapList.add(photo.toDataMap());
            }
            dataMap.putDataMapArrayList("photos", photosDataMapList);
        }

        return dataMap;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
