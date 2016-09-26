package com.example.xyzreader.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

public class ViewPagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ArticleListActivity.ArticleChangeListener {
    private static final String LOG_TAG = ViewPagerFragment.class.getSimpleName();
    private long mStartId = 1;

    private long mSelectedItemId = 0;
//    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;

    private MyPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private boolean mTwoPane;
    private ViewPager.SimpleOnPageChangeListener mSimpleOnPageChangeListener;

    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArticleDetailFragment);

        mTwoPane = a.getBoolean(R.styleable.ArticleDetailFragment_twoPane, false);

        a.recycle();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getActivity().getIntent() != null && getActivity().getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getActivity().getIntent().getData());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager(), mTwoPane);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        1,
                        getResources().getDisplayMetrics()));//Margin between two pages
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mSimpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mPagerAdapter.mCursor != null) {
                    mPagerAdapter.mCursor.moveToPosition(position);

                    mSelectedItemId = mPagerAdapter.mCursor.getLong(ArticleLoader.Query._ID);
                }
            }
        };

        mPager.addOnPageChangeListener(mSimpleOnPageChangeListener);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPager != null && mSimpleOnPageChangeListener != null)
            mPager.removeOnPageChangeListener(mSimpleOnPageChangeListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished abhi: mSelectedId: " + mSelectedItemId + "startId: " + mStartId);

        mPagerAdapter.mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();


        // Select the start ID
        if (mStartId > 0) {
            mPagerAdapter.mCursor.moveToFirst();
            // TODO: optimize
            while (!mPagerAdapter.mCursor.isAfterLast()) {
                if (mPagerAdapter.mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mPagerAdapter.mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mPagerAdapter.mCursor.moveToNext();
            }
            mStartId = 0;
        }

//        // Select the start ID
//        if (mStartId >= 0) {
//            mPagerAdapter.mCursor.moveToFirst();
//
//            mPagerAdapter.mCursor.moveToPosition((int) mStartId);
//            final int position = mPagerAdapter.mCursor.getPosition();
//            mPager.setCurrentItem(position, false);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startPostponedEnterTransition();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPagerAdapter.mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPageChanged(int position) {
        mPager.setCurrentItem(position, true);
    }

}
