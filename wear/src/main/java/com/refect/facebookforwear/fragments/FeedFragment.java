package com.refect.facebookforwear.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.refect.facebookforwear.MainActivity;
import com.refect.facebookforwear.R;
import com.refect.facebookforwear.UserProfileActivity;
import com.refect.facebookforwear.adapter.CommentAdapter;
import com.refect.facebookforwear.adapter.FeedAdapter;
import com.refect.facebookforwear.adapter.LikeAdapter;
import com.refect.shared.models.CommentSummaryModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.CircleTransform;
import com.refect.shared.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FeedModel mFeedModel;
    private int id;

    private FrameLayout contentView;
    private ImageView ivPicture;
    private ImageView ivLike;
    private ImageView ivComment;
    private ImageView ivUser;
    private TextView tvMessage;
    private TextView tvLikes;
    private TextView tvComments;
    private TextView tvUser;

    private GestureDetector gesture;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static FeedFragment newInstance(FeedModel param1, int id) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, id);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFeedModel = (FeedModel)getArguments().getSerializable(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_main, container, false);

        Log.d("FeedFragment (onCreateView)", mFeedModel.getType());
        contentView = (FrameLayout) view.findViewById(R.id.watch_view_stub);

        WatchViewStub stub = new WatchViewStub(getActivity());

        if(mFeedModel.getData() != null) {
            stub.setRectLayout(R.layout.round_fragment_feed_status);
            stub.setRoundLayout(R.layout.round_fragment_feed_status);
        } else {
            stub.setRectLayout(R.layout.round_fragment_feed_status_no_picture);
            stub.setRoundLayout(R.layout.round_fragment_feed_status_no_picture);
        }

//        if (mFeedModel.getType().equals(Utils.FEED_TYPE_STATUS)) {
//            stub.setRectLayout(R.layout.rect_fragment_feed_status);
//            stub.setRoundLayout(R.layout.round_fragment_feed_status);
//            Log.d("FeedFragment (onLayoutInflated)", Utils.FEED_TYPE_STATUS);
//        } else if (mFeedModel.getType().equals(Utils.FEED_TYPE_PHOTO)) {
//            stub.setRectLayout(R.layout.rect_fragment_feed_photo);
//            stub.setRoundLayout(R.layout.round_fragment_feed_photo);
//            Log.d("FeedFragment (onLayoutInflated)", Utils.FEED_TYPE_PHOTO);
//        } else if (mFeedModel.getType().equals(Utils.FEED_TYPE_LINK)) {
//            stub.setRectLayout(R.layout.rect_fragment_feed_link);
//            stub.setRoundLayout(R.layout.round_fragment_feed_link);
//            Log.d("FeedFragment (onLayoutInflated)", Utils.FEED_TYPE_LINK);
//        } else {
//            Log.e("FeedFragment (onLayoutInflated)", "Unknown Type: " + mFeedModel.getType());
//
//            //TODO: Create default layout
//            stub.setRectLayout(R.layout.rect_fragment_feed_photo);
//            stub.setRoundLayout(R.layout.round_fragment_feed_photo);
//        }

        contentView.addView(stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                if (mFeedModel.getData() != null) {
                    initPictureUI(stub);
                } else {
                    initNoPictureUI(stub);
                }
            }
        });

//        gesture = new GestureDetector(getActivity(),
//                new GestureDetector.SimpleOnGestureListener() {
//
//                    @Override
//                    public boolean onDoubleTap(MotionEvent e) {
//                        return super.onDoubleTap(e);
//                    }
//
//                    @Override
//                    public void onLongPress(MotionEvent ev) {
//                        //mDismissOverlay.show();
//                    }
//
//                    @Override
//                    public boolean onDown(MotionEvent e) {
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                                           float velocityY) {
//                        float sensitvity = 30;
//
//                        if((e1.getY() - e2.getY()) > sensitvity){
//                            if(((MainActivity)getActivity()).viewPostStatus.getVisibility() == View.VISIBLE) {
//                                Animation animUp = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_top);
//                                ((MainActivity)getActivity()).viewPostStatus.startAnimation(animUp);
//                                ((MainActivity)getActivity()).viewPostStatus.setVisibility(View.GONE);
//                            }
//                        }else if((e2.getY() - e1.getY()) > sensitvity){
//                            if(((MainActivity)getActivity()).viewPostStatus.getVisibility() == View.GONE) {
//                                ((MainActivity)getActivity()).viewPostStatus.setVisibility(View.VISIBLE);
//                                Animation animDown = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_top);
//                                ((MainActivity)getActivity()).viewPostStatus.startAnimation(animDown);
//                            }
//                        }
//
//                        return super.onFling(e1, e2, velocityX, velocityY);
//                    }
//                });

        return view;
    }

    /**
     *
     * @param stub
     */
    private void initPictureUI(View stub) {
        //instantiate specific views
        ivPicture = (ImageView) stub.findViewById(R.id.iv_picture);

        //handle specific view stuff
        if(mFeedModel.getData() != null) {
            try {
                ivPicture.setImageBitmap(Utils.byteArray2Bitmap(mFeedModel.getData()));
            } catch(OutOfMemoryError e) {
                ivPicture.setBackgroundColor(getActivity().getResources().getColor(R.color.facebook_blue));
                Log.e("FeedFragment (initPictureUI)", e.toString());
            }
        } else {
            ivPicture.setImageResource(R.drawable.temp);
        }

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullScreenDialog();
            }
        });

        initUI(stub);
    }

    /**
     *
     * @param stub
     */
    private void initNoPictureUI(View stub) {
        initUI(stub);
    }

    private void initUI(View stub) {
        //instantiate shared views
        ivLike = (ImageView) stub.findViewById(R.id.iv_like);
        ivComment = (ImageView) stub.findViewById(R.id.iv_comment);
        ivUser = (ImageView) stub.findViewById(R.id.iv_user);
        tvMessage = (TextView) stub.findViewById(R.id.tv_message);
        tvUser = (TextView) stub.findViewById(R.id.tv_user);
        tvLikes = (TextView) stub.findViewById(R.id.tv_likes);
        tvComments = (TextView) stub.findViewById(R.id.tv_comments);

        tvLikes.setText(mFeedModel.getLikes().getTotalCount() + " likes");
        tvComments.setText(mFeedModel.getComments().getTotalCount() + " comments");

        if(mFeedModel.getFrom() != null) {
            tvUser.setText(mFeedModel.getFrom().getName());
        }

        if(mFeedModel.getMessage() != null) {
            tvMessage.setText(mFeedModel.getMessage());
        } else if(mFeedModel.getStory() != null) {
            tvMessage.setText(mFeedModel.getStory());
        }

        int whiteHighlight = Color.WHITE;
        final PorterDuffColorFilter whiteColorFilter = new PorterDuffColorFilter(whiteHighlight, PorterDuff.Mode.SRC_ATOP);

        ivLike.setColorFilter(whiteColorFilter);
        ivComment.setColorFilter(whiteColorFilter);

        Bitmap original = Utils.resource2Bitmap(getActivity(), R.drawable.rsz_default_profile_picture);
        Bitmap circular = null;
        try {
            circular = new CircleTransform().transform(original);
        } catch(OutOfMemoryError e) {
            Log.e("FeedFragment (initUI)", e.toString());
        }

        original.recycle();

        if(circular != null) {
            ivUser.setImageBitmap(circular);
        } else {
            ivUser.setImageResource(R.drawable.rsz_default_profile_picture);
        }

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation grow = AnimationUtils.loadAnimation(getActivity(), R.anim.grow_circle);
                grow.setDuration(400);
                ivLike.startAnimation(grow);
                Toast.makeText(getActivity(), "Liked", Toast.LENGTH_SHORT).show();
                MainActivity.sendMessage(Utils.WEAR_MESSAGE_PATH, Utils.REQUEST_POST_LIKE + mFeedModel.getId());
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLikeDialog();
            }
        });

        tvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog();
            }
        });
    }

    /**
     *
     */
    private void showFullScreenDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_full_screen_picture);
        dialog.show();

        ImageView ivFullScreenPicture = (ImageView) dialog.findViewById(R.id.iv_full_screen_picture);
        ivFullScreenPicture.setImageBitmap(Utils.byteArray2Bitmap(mFeedModel.getData()));
    }

    /**
     *
     */
    private void showLikeDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_likes);
        dialog.show();

        LikeAdapter likeAdapter = new LikeAdapter(getActivity());
        RecyclerView rvLikes = (RecyclerView) dialog.findViewById(R.id.rv_likes);
        rvLikes.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLikes.setItemAnimator(new DefaultItemAnimator());
        rvLikes.setAdapter(likeAdapter);

        try {
            likeAdapter.setModels(mFeedModel.getLikes().getLikes());
        } catch(Exception e) {
            Log.e("FeedFragment (showLikeDialog)", e.toString());
        }
    }


    /**
     *
     */
    private void showCommentDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_comments);
        dialog.show();

        CommentAdapter commentAdapter = new CommentAdapter(getActivity());
        RecyclerView rvComments = (RecyclerView) dialog.findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvComments.setItemAnimator(new DefaultItemAnimator());
        rvComments.setAdapter(commentAdapter);

        for(CommentSummaryModel.CommentModel comment : mFeedModel.getComments().getComments()) {
            Log.e("Comment: ", comment.getMessage());
        }

        try {
            commentAdapter.setModels(mFeedModel.getComments().getComments());
        } catch(Exception e) {
            Log.e("FeedFragment (showLikeDialog)", e.toString());
        }
    }

}
