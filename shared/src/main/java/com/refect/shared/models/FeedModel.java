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
import java.util.List;

/**
 * Created by anelson on 7/30/15.
 */
public class FeedModel implements Serializable {

    private String id;
    private String type;
    private String message;
    private String description;
    private String story;
    private String link;
    private String picture;
    private String fullPicture;
    private LikeSummaryModel likes;
    private CommentSummaryModel comments;
    private List<UserModel> to;
    private UserModel from;
    private PlaceModel place;
    private String previousPagingUrl;
    private String nextPagingUrl;


    private byte[] data;

    public FeedModel() {

    }

    public FeedModel(DataMap dataMap) {
        setId(dataMap.getString("id"));
        setType(dataMap.getString("type"));
        setMessage(dataMap.getString("message"));
        setDescription(dataMap.getString("description"));
        setStory(dataMap.getString("story"));
        setPicture(dataMap.getString("picture"));
        setFullPicture(dataMap.getString("full_picture"));
        setLink(dataMap.getString("link"));
        setPreviousPagingUrl(dataMap.getString("previous"));
        setNextPagingUrl(dataMap.getString("next"));
        setLikes(new LikeSummaryModel(dataMap.getDataMap("likes")));
        setComments(new CommentSummaryModel(dataMap.getDataMap("comments")));

        if(dataMap.containsKey("from")) {
            setFrom(new UserModel(dataMap.getDataMap("from")));
        }

        if(dataMap.containsKey("place")) {
            setPlace(new PlaceModel(dataMap.getDataMap("place")));
        }
    }

    public FeedModel(JSONObject json) {
        try {
            setId(json.getString("id"));
            setType(Utils.checkJsonHasString("type", json));
            setMessage(Utils.checkJsonHasString("message", json));
            setDescription(Utils.checkJsonHasString("description", json));
            setStory(Utils.checkJsonHasString("story", json));
            setLink(Utils.checkJsonHasString("link", json));
            setPicture(Utils.checkJsonHasString("picture", json));
            setFullPicture(Utils.checkJsonHasString("full_picture", json));

            if(json.has("place")) {
                setPlace(new PlaceModel(json.getJSONObject("place")));
            }

            if(json.has("paging")) {
                setPreviousPagingUrl(json.getJSONObject("paging").getString("previous"));
                setNextPagingUrl(json.getJSONObject("paging").getString("next"));
            }

            if(json.has("from")) {
                setFrom(new UserModel(json.getJSONObject("from")));
            }

            //To User Array
            if(json.has("to")) {
                List<UserModel> temp = new ArrayList<>();
                JSONArray toArrary = json.getJSONObject("to").getJSONArray("data");
                for(int i = 0; i < toArrary.length(); i++) {
                    temp.add(new UserModel(toArrary.getJSONObject(i)));
                }
                setTo(temp);
            }

        } catch(JSONException e) {
            Log.e("FeedModel (constructor)", e.toString());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public List<UserModel> getTo() {
        return to;
    }

    public void setTo(List<UserModel> to) {
        this.to = to;
    }

    public UserModel getFrom() {
        return from;
    }

    public void setFrom(UserModel from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPreviousPagingUrl() {
        return previousPagingUrl;
    }

    public void setPreviousPagingUrl(String previousPagingUrl) {
        this.previousPagingUrl = previousPagingUrl;
    }

    public String getNextPagingUrl() {
        return nextPagingUrl;
    }

    public void setNextPagingUrl(String nextPagingUrl) {
        this.nextPagingUrl = nextPagingUrl;
    }

    public LikeSummaryModel getLikes() {
        return likes;
    }

    public void setLikes(LikeSummaryModel likes) {
        this.likes = likes;
    }

    public PlaceModel getPlace() {
        return place;
    }

    public void setPlace(PlaceModel place) {
        this.place = place;
    }

    public CommentSummaryModel getComments() {
        return comments;
    }

    public void setComments(CommentSummaryModel comments) {
        this.comments = comments;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("id", getId());
        dataMap.putString("message", getMessage());
        dataMap.putString("description", getDescription());
        dataMap.putString("story", getStory());
        dataMap.putString("picture", getPicture());
        dataMap.putString("full_picture", getFullPicture());
        dataMap.putString("type", getType());
        dataMap.putString("link", getLink());
        dataMap.putString("previous", getPreviousPagingUrl());
        dataMap.putString("next", getNextPagingUrl());
        dataMap.putDataMap("likes", getLikes().toDataMap());
        dataMap.putDataMap("comments", getComments().toDataMap());
        dataMap.putDataMap("from", getFrom().toDataMap());

        if(getPlace() != null) {
            dataMap.putDataMap("place", getPlace().toDataMap());
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
