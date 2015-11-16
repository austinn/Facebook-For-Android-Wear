package com.refect.facebookforwear;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.refect.facebookforwear.adapter.FeedAdapter;
import com.refect.facebookforwear.async.ImageDownloader;
import com.refect.facebookforwear.fragments.AlbumFragment;
import com.refect.facebookforwear.fragments.FeedFragment;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.CommentSummaryModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.models.LikeSummaryModel;
import com.refect.shared.models.PhotoModel;
import com.refect.shared.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ProfilePictureView profilePictureView;

    private Button enterAppId;
    private Button page;
    private Button generateKeyHash;
    private EditText keyHash;
    private JSONObject currentJson;
    private ViewPager mPager;
    private CirclePageIndicator dots;
    private FeedAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appId = Utils.getSetting("facebook_app_id", null, getApplicationContext());
        if(appId == null) {
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            finish();
        }

        FacebookSdk.setApplicationId(appId);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        page = (Button) findViewById(R.id.button);
        generateKeyHash = (Button) findViewById(R.id.btn_generate_key_hash);
        keyHash = (EditText) findViewById(R.id.edit_key_hash);
        enterAppId = (Button) findViewById(R.id.btn_enter_app_id);
        enterAppId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dots = (CirclePageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new FeedAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        dots.setViewPager(mPager);
        dots.setFillColor(Color.BLUE);

        generateKeyHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generateKeyHash.getText().equals("Goto Facebook Developers")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://developers.facebook.com/"));
                    startActivity(i);
                } else {
                    keyHash.setText(Utils.printHashKey(getApplicationContext()));
                    generateKeyHash.setText("Goto Facebook Developers");
                }
            }
        });

        page.setVisibility(View.GONE);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    new FeedPaginationDownloader(getApplicationContext()).execute(currentJson.getJSONObject("paging").getString("next"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });

        LoginButton mUserLoginButton = (LoginButton) findViewById(R.id.login_button);
        mUserLoginButton.setReadPermissions(Arrays.asList("user_about_me", "user_friends", "user_likes",
                "user_photos", "user_relationships", "user_posts",
                "user_status", "read_stream"));
        mUserLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        for (String permission : loginResult.getRecentlyGrantedPermissions()) {
                            Log.d("Granted Permission:", permission);
                        }
                    }

                    @Override
                    public void onCancel() {
                        showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });

        updateUI();
    }

    /**
     *
     */
    public static void meFeedGraphApi(final Context ctx, String who) {
        Bundle params = new Bundle();
        params.putInt("limit", 25);
        params.putString("fields", "id,name,link,full_picture,message,story,picture,type,place,from,to");
        params.putBoolean("summary", true);

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + who + "/feed",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            createFeedModels(ctx, response.getJSONObject().getJSONArray("data"), response.getJSONObject().getJSONObject("paging"));
                            //currentJson = response.getJSONObject();
                        } catch (JSONException e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    public static void paginateFeed(Context ctx, String url) {
        new FeedPaginationDownloader(ctx).execute(url);
    }

    /**
     *
     */
    public static void myAlbums(final Context ctx) {
        Bundle params = new Bundle();
        params.putInt("limit", 25);
        params.putString("fields", "photos{id,name,link,picture,images},count,name,link");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray albumArray = response.getJSONObject().getJSONArray("data");
                            for(int i = 0; i < albumArray.length(); i++) {
                                AlbumModel album = new AlbumModel(albumArray.getJSONObject(i));

                                //update mobile
//                                List<Fragment> allFragments = mPagerAdapter.getFragments();
//                                allFragments.add(AlbumFragment.newInstance(album));
//                                mPagerAdapter.setFragments(allFragments);

                                //update wear
                                sendAlbumModelToWear(ctx, album);
                            }

                        } catch (JSONException e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     *
     * @param ctx
     * @param albumId
     */
    public static void myPhotosByAlbum(final Context ctx, String albumId) {
        Bundle params = new Bundle();
        params.putInt("limit", 25);
        params.putString("fields", "photos{id,name,link,picture,images}");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray albumArray = response.getJSONObject().getJSONObject("photos").getJSONArray("data");
                            for(int i = 0; i < albumArray.length(); i++) {
                                PhotoModel photo = new PhotoModel(albumArray.getJSONObject(i));

                                //update wear
                                sendPhotoModelToWear(ctx, photo);
                            }

                        } catch (JSONException e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     *
     * @param feedModel
     */
    public static void getObjectComments(final Context ctx, final FeedModel feedModel, final String lastOne) {
        Bundle params = new Bundle();
        params.putInt("limit", 25);
        params.putBoolean("summary", true);
        params.putString("fields", "from,like_count,comment_count,user_likes,message");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + feedModel.getId() + "/comments",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            CommentSummaryModel comments = new CommentSummaryModel(response.getJSONObject());
                            feedModel.setComments(comments);

                            getObjectLikes(ctx, feedModel, lastOne);
                        } catch (Exception e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     *
     * @param feedModel
     */
    public static void getObjectLikes(final Context ctx, final FeedModel feedModel, final String lastOne) {
        Bundle params = new Bundle();
        params.putInt("limit", 25);
        params.putBoolean("summary", true);
        params.putString("fields", "id,name,pic_small");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + feedModel.getId() + "/likes",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            LikeSummaryModel likes = new LikeSummaryModel(response.getJSONObject());
                            feedModel.setLikes(likes);

                            //update the mobile app
//                            List<Fragment> allFragments = mPagerAdapter.getFragments();
//                            allFragments.add(FeedFragment.newInstance(feedModel));
//                            mPagerAdapter.setFragments(allFragments);

                            //update the wear app
                            sendFeedModelToWear(ctx, feedModel, lastOne);

                        } catch (Exception e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     *
     * @param feedModel
     */
    public static void getToUserPhoto(final Context ctx, final FeedModel feedModel, final String lastOne) {
        Bundle params = new Bundle();
        params.putString("fields", "id,picture,name");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + feedModel.getTo().get(0).getId(),
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Picasso.with(ctx)
                                    .load(response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url"))
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            //feedModel.getTo().get(0).setBitmap(bitmap);
                                            getObjectLikes(ctx, feedModel, lastOne);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        } catch (Exception e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     *
     * @param feedModel
     */
    public static void getFromUserPhoto(final Context ctx, final FeedModel feedModel, final String lastOne) {
        Bundle params = new Bundle();
        params.putString("fields", "id,picture,name");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + feedModel.getFrom().getId(),
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Picasso.with(ctx)
                                    .load(response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url"))
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            //feedModel.getFrom().setBitmap(bitmap);
                                            getObjectLikes(ctx, feedModel, lastOne);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        } catch (Exception e) {
                            Log.e("Error: ", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     * Creates a FeedModel for each JSONObject in the array
     * @param jsonArray
     */
    public static void createFeedModels(final Context ctx, JSONArray jsonArray, JSONObject paging) {
        String lastOne = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            Log.e("MainActivity (createFeedModels)", i + "");
            try {

                if(i == jsonArray.length() - 1) {
                    lastOne = Utils.LAST_ONE;
                }

                JSONObject json = jsonArray.getJSONObject(i);
                Log.i("MainActivity (createFeedModels)", json.toString());
                FeedModel feed = new FeedModel(json);

                feed.setPreviousPagingUrl(paging.getString("previous"));
                feed.setNextPagingUrl(paging.getString("next"));

                getObjectComments(ctx, feed, lastOne);

            } catch(JSONException e) {
                Log.e("MainActivity (printJson)", e.toString());
            }

        }
    }

    /**
     *
     * @param feedModel
     */
    public static void sendFeedModelToWear(Context ctx, final FeedModel feedModel, final String lastOne) {
        new ImageDownloader(ctx, feedModel, lastOne).execute(feedModel.getFullPicture());
    }

    /**
     *
     * @param albumModel
     */
    public static void sendAlbumModelToWear(Context ctx, final AlbumModel albumModel) {
        String url = "";
        if(albumModel.getPhotos() != null) {
            if(albumModel.getPhotos().size() > 0) {
                url = albumModel.getPhotos().get(0).getUrl();
            }
        }

        new ImageDownloader(ctx, albumModel).execute(url);
    }

    /**
     *
     * @param photoModel
     */
    public static void sendPhotoModelToWear(Context ctx, final PhotoModel photoModel) {
        new ImageDownloader(ctx, photoModel).execute(photoModel.getUrl());
    }

    /**
     *
     * @param ctx
     * @param id
     */
    public static void likePost(Context ctx, String id) {
        GraphRequest likeRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                id + "/likes",
                null,
                HttpMethod.POST,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.d("LikePost", graphResponse.toString());
                    }
        });
        GraphRequest.executeBatchAsync(likeRequest);
    }

    /**
     *
     */
    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            profilePictureView.setProfileId(profile.getId());
            //myPhotosByAlbum("/466974975621");
            //meFeedGraphApi("me");
            //myAlbums();
            //greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
        } else {
            profilePictureView.setProfileId(null);
            //greeting.setText(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Created by anelson on 5/11/15.
     */
    public static class FeedPaginationDownloader extends AsyncTask<String, String, JSONObject> {

        private Context ctx;

        public FeedPaginationDownloader(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String[] params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                //urlConnection.setDoOutput(true); //forces POST
                urlConnection.connect();
                String response = streamToString(urlConnection.getInputStream());
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                return jsonObj;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        /**
         * Method that returns String from the InputStream given by p_is
         *
         * @param p_is The given InputStream
         * @return The String from the InputStream
         */
        public String streamToString(InputStream p_is) {
            try {
                BufferedReader m_br;
                StringBuffer m_outString = new StringBuffer();
                m_br = new BufferedReader(new InputStreamReader(p_is));
                String m_read = m_br.readLine();
                while (m_read != null) {
                    m_outString.append(m_read);
                    m_read = m_br.readLine();
                }
                return m_outString.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if(result != null) {
                try {
                    createFeedModels(ctx, result.getJSONArray("data"), result.getJSONObject("paging"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
