package com.refect.facebookforwear.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.facebookforwear.PhotosActivity;
import com.refect.facebookforwear.R;
import com.refect.facebookforwear.adapter.CommentAdapter;
import com.refect.facebookforwear.adapter.LikeAdapter;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.CommentSummaryModel;
import com.refect.shared.models.FeedModel;
import com.refect.shared.utils.CircleTransform;
import com.refect.shared.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AlbumModel mAlbumModel;
    private int id;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static AlbumFragment newInstance(AlbumModel param1, int id) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, id);
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlbumModel = (AlbumModel)getArguments().getSerializable(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        TextView tvAlbumName = (TextView) view.findViewById(R.id.tv_album_name);
        TextView tvAlbumCount = (TextView) view.findViewById(R.id.tv_album_count);
        ImageView ivAlbumCoverPhoto = (ImageView) view.findViewById(R.id.iv_album_cover_photo);

        tvAlbumName.setText(mAlbumModel.getName());
        tvAlbumCount.setText(mAlbumModel.getCount() + " photos");

        try {
            ivAlbumCoverPhoto.setImageBitmap(Utils.byteArray2Bitmap(mAlbumModel.getData()));
        } catch(Exception e) {

        }

        ivAlbumCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotosActivity.class);
                intent.putExtra("AlbumId", mAlbumModel.getId());
                startActivity(intent);
            }
        });

        return view;
    }

}
