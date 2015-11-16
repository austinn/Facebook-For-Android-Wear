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
public class CommentSummaryModel implements Serializable {

    private ArrayList<CommentModel> comments;
    private String pagingAfter;
    private String pagingBefore;
    private int totalCount;
    private boolean canComment;

    public CommentSummaryModel() {

    }

    public CommentSummaryModel(JSONObject json) {
        Log.i("CommentSummaryModel: ", json.toString());
        try {
            ArrayList<CommentModel> comments = new ArrayList<>();
            JSONArray array = json.getJSONArray("data");
            for(int i = 0; i < array.length(); i++) {
                comments.add(new CommentModel(array.getJSONObject(i)));
            }
            setComments(comments);

            if (json.has("paging")) {
                setPagingAfter(json.getJSONObject("paging").getJSONObject("cursors").getString("after"));
                setPagingBefore(json.getJSONObject("paging").getJSONObject("cursors").getString("before"));
            }

            if(json.has("summary")) {
                setTotalCount(json.getJSONObject("summary").getInt("total_count"));
                //setCanComment(json.getJSONObject("summary").getBoolean("can_comment"));
            }

        } catch(JSONException e) {
            Log.e("CommentSummaryModel (constructor)", e.toString());
        }
    }

    public CommentSummaryModel(DataMap dataMap) {
        setPagingAfter(dataMap.getString("pagingAfter"));
        setPagingBefore(dataMap.getString("pagingBefore"));
        setTotalCount(dataMap.getInt("totalCount"));
        //setCanComment(dataMap.getBoolean("canComment"));

        ArrayList<CommentModel> comments = new ArrayList<>();
        ArrayList<DataMap> array = dataMap.getDataMapArrayList("comments");
        for(int i = 0; i < array.size(); i++) {
            comments.add(new CommentModel(array.get(i)));
        }
        setComments(comments);
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
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

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public DataMap toDataMap() {
        DataMap dataMap = new DataMap();

        dataMap.putString("pagingAfter", getPagingAfter());
        dataMap.putString("pagingBefore", getPagingBefore());
        dataMap.putInt("totalCount", getTotalCount());
        //dataMap.putBoolean("canComment", isCanComment());

        ArrayList<DataMap> commentsDataMap = new ArrayList<>();
        for(CommentModel comment : getComments()) {
            commentsDataMap.add(comment.toDataMap());
        }
        dataMap.putDataMapArrayList("comments", commentsDataMap);

        return dataMap;
    }

    /**
     *
     */
    public class CommentModel implements Serializable {

        private String id;
        private String message;
        private UserModel from;
        private boolean userLikes;
        private int likeCount;

        public CommentModel() {

        }

        public CommentModel(JSONObject json) {
            try {
                setId(json.getString("id"));
                setMessage(json.getString("message"));
                setFrom(new UserModel(json.getJSONObject("from")));
                setUserLikes(json.getBoolean("user_likes"));
                setLikeCount(json.getInt("like_count"));
            } catch(JSONException e) {
                Log.e("CommentModel (constructor)", e.toString());
            }
        }

        public CommentModel(DataMap dataMap) {
            setId(dataMap.getString("id"));
            setMessage(dataMap.getString("message"));
            setFrom(new UserModel(dataMap.getDataMap("from")));
            setUserLikes(dataMap.getBoolean("user_likes"));
            setLikeCount(dataMap.getInt("like_count"));
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

        public UserModel getFrom() {
            return from;
        }

        public void setFrom(UserModel from) {
            this.from = from;
        }

        public boolean isUserLikes() {
            return userLikes;
        }

        public void setUserLikes(boolean userLikes) {
            this.userLikes = userLikes;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public DataMap toDataMap() {
            DataMap dataMap = new DataMap();

            dataMap.putString("id", getId());
            dataMap.putString("message", getMessage());
            dataMap.putDataMap("from", getFrom().toDataMap());
            dataMap.putBoolean("user_likes", isUserLikes());
            dataMap.putInt("like_count", getLikeCount());

            return dataMap;
        }
    }
}
