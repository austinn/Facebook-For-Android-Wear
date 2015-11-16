package com.refect.facebookforwear.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.refect.facebookforwear.R;
import com.refect.shared.models.FeedModel;
import com.refect.shared.models.LikeSummaryModel;
import com.refect.shared.models.UserModel;
import com.squareup.picasso.Picasso;

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

    private FeedModel mFeedModel;

    private TextView tvId;
    private TextView tvType;
    private TextView tvMessage;
    private TextView tvDescription;
    private TextView tvStory;
    private TextView tvLikeCount;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvLink;
    private ImageView ivPicture;
    private Button btnLikes;
    private Button btnComments;
    private Button btnPlace;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static FeedFragment newInstance(FeedModel param1) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        tvId = (TextView) view.findViewById(R.id.tvId);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvStory = (TextView) view.findViewById(R.id.tvStory);
        tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
        tvFrom = (TextView) view.findViewById(R.id.tvFrom);
        tvTo = (TextView) view.findViewById(R.id.tvTo);
        tvLink = (TextView) view.findViewById(R.id.tvLink);
        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        btnLikes = (Button) view.findViewById(R.id.btnLikes);
        btnComments = (Button) view.findViewById(R.id.btnComments);
        btnPlace = (Button) view.findViewById(R.id.btnPlace);

        tvId.setText(mFeedModel.getId());
        tvId.setVisibility(View.GONE);

        tvType.setText("Type: " + mFeedModel.getType());
        tvMessage.setText("Message: " + mFeedModel.getMessage());
        tvDescription.setText("Description: " + mFeedModel.getDescription());
        tvStory.setText("Story: " + mFeedModel.getStory());
        tvLink.setText("Link: " + mFeedModel.getLink());
        tvLikeCount.setText("Like Count: " + mFeedModel.getLikes().getTotalCount());
        tvLink.setTextColor(Color.BLUE);
        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tvLink.getText().toString()));
                startActivity(i);
            }
        });

        try {
            tvFrom.setText("From: " + mFeedModel.getFrom().getName());

            if(mFeedModel.getTo() != null) {
                for(UserModel user : mFeedModel.getTo()) {
                    tvTo.setText(tvTo.getText() + user.getName());
                }
            }
        } catch(Exception e) {
            Log.e("FeedFragment (onCreateView)", e.toString());
        }

        if(mFeedModel.getPicture() != null) {
            ivPicture.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(mFeedModel.getPicture())
                    .into(ivPicture);
        } else {
            ivPicture.setVisibility(View.GONE);
        }

        btnLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLikeDialog();
            }
        });

        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaceDialog();
            }
        });

        return view;
    }

    /**
     *
     */
    private void showLikeDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_likes);
        dialog.setTitle("Likes");
        dialog.show();

        TextView tvLikes = (TextView) dialog.findViewById(R.id.tvLikes);
        for(LikeSummaryModel.LikeModel like : mFeedModel.getLikes().getLikes()) {
            tvLikes.setText(tvLikes.getText() + like.getName() + "\n");
        }
    }

    /**
     *
     */
    private void showPlaceDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_place);
        dialog.setTitle("Place");
        dialog.show();

        TextView tvPlaceName = (TextView) dialog.findViewById(R.id.tvPlaceName);
        TextView tvStreet = (TextView) dialog.findViewById(R.id.tvPlaceStreet);
        TextView tvCity = (TextView) dialog.findViewById(R.id.tvPlaceCity);
        TextView tvState = (TextView) dialog.findViewById(R.id.tvPlaceState);
        TextView tvZip = (TextView) dialog.findViewById(R.id.tvPlaceZip);

        try {
            tvPlaceName.setText(mFeedModel.getPlace().getName());
            tvStreet.setText(mFeedModel.getPlace().getLocation().getStreet());
            tvCity.setText(mFeedModel.getPlace().getLocation().getCity());
            tvState.setText(mFeedModel.getPlace().getLocation().getState());
            tvZip.setText(mFeedModel.getPlace().getLocation().getZip());
        } catch(Exception e) {
            Log.e("FeedFragment (showPlaceDialog)", e.toString());
        }

    }

}
