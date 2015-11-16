package com.refect.facebookforwear.listeners;

import android.view.View;

public interface OnRecyclerViewItemLongClickListener<T> {
	public void onItemLongClick(View view, T model);
}

