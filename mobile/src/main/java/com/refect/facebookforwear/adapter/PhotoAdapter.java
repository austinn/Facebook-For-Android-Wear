package com.refect.facebookforwear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.refect.facebookforwear.R;
import com.refect.shared.models.PhotoModel;
import com.refect.shared.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private static final int ANIMATED_ITEMS_COUNT = 7;

    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;
    private List<PhotoModel> models;
	private static Context mContext;
	private int lastPosition = -1;

	public PhotoAdapter(Context context, List<PhotoModel> models) {
		this.models = models;
		this.mContext = context;
	}

    public PhotoAdapter(Context context) {
        this.mContext = context;
        models = new ArrayList<>();
    }

    public List<PhotoModel> getModels() {
        return models;
    }

    public void setModels(List<PhotoModel> models) {
        this.models = models;
        this.notifyDataSetChanged();
    }

    private void runEnterAnimation(View view, int position) {

//        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
//            return;
//        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration((position*100) + 1000)
                    .start();
        }
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_photo, viewGroup, false);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
        runEnterAnimation(viewHolder.itemView, i);
		final PhotoModel model = models.get(i);

		viewHolder.itemView.setTag(model);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.getScreenWidth(mContext), Utils.getScreenWidth(mContext));
        viewHolder.photo.setLayoutParams(params);

        Picasso.with(mContext).load(model.getUrl())
                .into(viewHolder.photo);
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private PhotoModel mModel;
		public ImageView photo;

		public ViewHolder(View itemView) {
			super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.ivPicture);

		}

	}

	public void add(PhotoModel item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

    public void add(PhotoModel item) {
        models.add(item);
        notifyDataSetChanged();
    }

	public void remove(PhotoModel item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}


}