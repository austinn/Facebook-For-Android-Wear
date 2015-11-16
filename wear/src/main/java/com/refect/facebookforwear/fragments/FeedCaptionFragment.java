package com.refect.facebookforwear.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.refect.facebookforwear.R;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FeedCaptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedCaptionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private FeedModel mFeedModel;

    private TextView tvMessage;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static FeedCaptionFragment newInstance(FeedModel param1) {
        FeedCaptionFragment fragment = new FeedCaptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedCaptionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_feed_caption, container, false);

        TextView tvStory = (TextView) view.findViewById(R.id.tv_story);
        tvStory.setText(mFeedModel.getMessage());

        return view;
    }

}
