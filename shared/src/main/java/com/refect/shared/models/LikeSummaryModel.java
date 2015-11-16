package com.refect.shared.models;

import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anelson on 8/4/15.
 */
public class LikeSummaryModel implements Serializable {

    private ArrayList<LikeModel> likes;
    private String pagingAfter;
    private String pagingBefore;
    private int totalCount;
    private boolean canLike;
    private boolean hasLiked;

    public LikeSummaryModel() {

    }

    public LikeSummaryModel(JSONObject json) {
        try {

            ArrayList<LikeModel> likes = new ArrayList<>();
            JSONArray array = json.getJSONArray("data");
            for(int i = 0; i < array.length(); i++) {
                likes.add(new LikeModel(array.getJSONObject(i)));
            }
            setLikes(likes);

            if (json.has("paging")) {
                setPagingAfter(json.getJSONObject("paging").getJSONObject("cursors").getString("after"));
                setPagingBefore(json.getJSONObject("paging").getJSONObject("cursors").getString("before"));
            }

            if(json.has("summary")) {
                setTotalCount(json.getJSONObject("summary").getInt("total_count"));
                //setCanLike(json.getJSONObject("summary").getBoolean("can_like"));
                setHasLiked(json.getJSONObject("summary").getBoolean("has_liked"));
            }

        } catch(JSONException e) {
            Log.e("LikeSummaryModel (constructor)", e.toString());
        }
    }

    public LikeSummaryModel(DataMap dataMap) {
        setPagingAfter(dataMap.getString("pagingAfter"));
        setPagingBefore(dataMap.getString("pagingBefore"));
        setTotalCount(dataMap.getInt("totalCount"));
        setHasLiked(dataMap.getBoolean("hasLiked"));
        //setCanLike(dataMap.getBoolean("canLike"));

        ArrayList<LikeModel> likes = new ArrayList<>();
        ArrayList<DataMap> array = dataMap.getDataMapArrayList("likes");
        for(int i = 0; i < array.size(); i++) {
            likes.add(new LikeModel(array.get(i)));
        }
        setLikes(likes);
    }

    public ArrayList<LikeModel> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<LikeModel> likes) {
        this.likes = likes;
    }

    public String getPagingAfter() {
        return pagingAfter;
    }

    public void setPagingAfter(String pagingAfter) {
        this.pagingAfter = pagingAfter;
    }

    public String getPagingBefore() {
        return pagingBefore;
    }

    public void setPagingBefore(String pagingBefore) {
        this.pagingBefore = pagingBefore;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isCanLike() {
        return canLike;
    }

    public void setCanLike(boolean canLike) {
        this.canLike = canLike;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("pagingAfter", getPagingAfter());
        dataMap.putString("pagingBefore", getPagingBefore());
        dataMap.putInt("totalCount", getTotalCount());
        //dataMap.putBoolean("canLike", isCanLike());
        dataMap.putBoolean("hasLiked", isHasLiked());

        ArrayList<DataMap> likesDataMap = new ArrayList<>();
        for(LikeModel like : getLikes()) {
            likesDataMap.add(like.toDataMap());
        }
        dataMap.putDataMapArrayList("likes", likesDataMap);

        return dataMap;
    }

    /**
     *
     */
    public class LikeModel implements Serializable {

        private String id;
        private String name;

        public LikeModel() {

        }

        public LikeModel(JSONObject json) {
            try {
                setId(json.getString("id"));
                setName(json.getString("name"));
            } catch(JSONException e) {
                Log.e("LikeModel (constructor)", e.toString());
            }
        }

        public LikeModel(DataMap dataMap) {
            setId(dataMap.getString("id"));
            setName(dataMap.getString("name"));
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public DataMap toDataMap() {
            DataMap dataMap = new DataMap();

            dataMap.putString("id", getId());
            dataMap.putString("name", getName());

            return dataMap;
        }
    }
}
