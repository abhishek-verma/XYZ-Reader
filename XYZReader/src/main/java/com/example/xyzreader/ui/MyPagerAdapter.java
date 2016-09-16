package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;

class MyPagerAdapter extends FragmentStatePagerAdapter {
    MyPagerAdapterListener mListener;
    public Cursor mCursor;

    public MyPagerAdapter(FragmentManager fm, MyPagerAdapterListener listener) {
        super(fm);
        mListener = listener;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ArticleDetailFragment fragment = (ArticleDetailFragment) object;
        if (fragment != null) {
            int selectedItemUpButtonFloor = fragment.getUpButtonFloor();
            mListener.onFragmentFloorReceived(selectedItemUpButtonFloor);
        }
    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }


    public interface MyPagerAdapterListener {
        void onFragmentFloorReceived(int selectedItemUpButtonFloor);
    }

}