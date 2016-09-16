package com.example.xyzreader.ui;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

public class ViewPagerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ArticleListActivity.ArticleChangeListener {
    private long mStartId;

    private long mSelectedItemId;
//    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;

    private MyPagerAdapter mPagerAdapter;
    private UpButtonCallbacks mUpButtonCallbacksReceiver = null;
    private ViewPager mPager;

    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getActivity().getIntent() != null && getActivity().getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getActivity().getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setUpButtonCallbackReceiver(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager(),
                new MyPagerAdapter.MyPagerAdapterListener() {
                    @Override
                    public void onFragmentFloorReceived(int selectedItemUpButtonFloor) {
                        if (mUpButtonCallbacksReceiver != null) {
                            mUpButtonCallbacksReceiver.upButtonFloorReceived(selectedItemUpButtonFloor);
                        }
                    }
                });
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        1,
                        getResources().getDisplayMetrics()));//Margin between two pages
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (mUpButtonCallbacksReceiver != null) {
                    mUpButtonCallbacksReceiver.onPageScrollStateChanged(state);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mPagerAdapter.mCursor != null) {
                    mPagerAdapter.mCursor.moveToPosition(position);

                    mSelectedItemId = mPagerAdapter.mCursor.getLong(ArticleLoader.Query._ID);
                }
                if (mUpButtonCallbacksReceiver != null)
                    mUpButtonCallbacksReceiver.onPositionObsolete();
            }
        });



        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPagerAdapter.mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mPagerAdapter.mCursor.moveToFirst();
            // TODO: optimize
//            while (!mPagerAdapter.mCursor.isAfterLast()) {
//                if (mPagerAdapter.mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
//                    final int position = mPagerAdapter.mCursor.getPosition();
//                    mPager.setCurrentItem(position, false);
//                    break;
//                }
//                mPagerAdapter.mCursor.moveToNext();
//            }

            mPagerAdapter.mCursor.moveToPosition((int) mStartId);
            final int position = mPagerAdapter.mCursor.getPosition();
            mPager.setCurrentItem(position, false);

            mStartId = 0;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startPostponedEnterTransition();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPagerAdapter.mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setUpButtonCallbackReceiver(Context activity) {
        if (activity instanceof UpButtonCallbacks) {
            mUpButtonCallbacksReceiver = (UpButtonCallbacks) activity;
        }
    }

    @Override
    public void onPageChanged(int position) {
        mPager.setCurrentItem(position, true);
    }

    public interface UpButtonCallbacks {
        void onPositionObsolete();

        void upButtonFloorReceived(int selectedItemUpButtonFloor);

        void onPageScrollStateChanged(int state);
    }

}
