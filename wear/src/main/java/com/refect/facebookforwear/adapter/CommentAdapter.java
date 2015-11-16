package com.refect.facebookforwear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.refect.facebookforwear.R;
import com.refect.facebookforwear.listeners.OnRecyclerViewItemClickListener;
import com.refect.shared.models.CommentSummaryModel;
import com.refect.shared.models.LikeSummaryModel;
import com.refect.shared.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> implements View.OnClickListener {

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private static final int ANIMATED_ITEMS_COUNT = 7;

    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;
    private List<CommentSummaryModel.CommentModel> models;
	private OnRecyclerViewItemClickListener<CommentSummaryModel.CommentModel> itemClickListener;
	private static Context mContext;
	private int lastPosition = -1;

	public CommentAdapter(Context context, List<CommentSummaryModel.CommentModel> models) {
		this.models = models;
		this.mContext = context;
	}

    public CommentAdapter(Context context) {
        this.mContext = context;
        models = new ArrayList<>();
    }

    public List<CommentSummaryModel.CommentModel> getModels() {
        return models;
    }

    public void setModels(List<CommentSummaryModel.CommentModel> models) {
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
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_comment, viewGroup, false);
		v.setOnClickListener(this);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
        runEnterAnimation(viewHolder.itemView, i);
		final CommentSummaryModel.CommentModel model = models.get(i);

		viewHolder.itemView.setTag(model);
        viewHolder.message.setText(model.getMessage());
        viewHolder.user.setText(model.getFrom().getName());
        viewHolder.likes.setText(model.getLikeCount() + " likes");
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private CommentSummaryModel.CommentModel mModel;
		public TextView message;
        public TextView user;
        public TextView likes;

		public ViewHolder(View itemView) {
			super(itemView);
			message = (TextView) itemView.findViewById(R.id.tv_comment_message);
            user = (TextView) itemView.findViewById(R.id.tv_comment_user);
            likes = (TextView) itemView.findViewById(R.id.tv_comment_likes);
		}

	}

	public void add(CommentSummaryModel.CommentModel item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

    public void add(CommentSummaryModel.CommentModel item) {
        models.add(item);
        notifyDataSetChanged();
    }

	public void remove(CommentSummaryModel.CommentModel item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener<CommentSummaryModel.CommentModel> listener) {
		this.itemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null) {
			CommentSummaryModel.CommentModel model = (CommentSummaryModel.CommentModel) v.getTag();
			itemClickListener.onItemClick(v, model);
		}
	}


}