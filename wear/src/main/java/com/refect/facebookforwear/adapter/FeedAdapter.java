package com.refect.facebookforwear.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class FeedAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    public FeedAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.fragments.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
