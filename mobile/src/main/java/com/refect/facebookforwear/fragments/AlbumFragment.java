package com.refect.facebookforwear.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.refect.facebookforwear.R;
import com.refect.facebookforwear.adapter.FeedAdapter;
import com.refect.facebookforwear.adapter.PhotoAdapter;
import com.refect.shared.models.AlbumModel;
import com.refect.shared.models.PhotoModel;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

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

    private AlbumModel mAlbumModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static AlbumFragment newInstance(AlbumModel param1) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        TextView album = (TextView) view.findViewById(R.id.tvAlbum);
        album.setText(mAlbumModel.getName() + "(" + mAlbumModel.getCount() + ")");
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return view;
    }

    private void showDialog() {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_photos);
        dialog.show();

        RecyclerView rvPhotos = (RecyclerView) dialog.findViewById(R.id.rv_photos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));
        PhotoAdapter adapter = new PhotoAdapter(getActivity());
        rvPhotos.setAdapter(adapter);

        adapter.setModels(mAlbumModel.getPhotos());
    }

}
