package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        ArticleListFragment.ListFragmentCallbacksListener {

    private Toolbar mToolbar;

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_article_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTwoPane = findViewById(R.id.view_pager_fragment) != null;

        if (savedInstanceState == null) {
            refresh();
        }
    }
    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }



    @Override
    public void onItemClicked(int pos, ArticleListFragment.ViewHolder vh) {

        if(mTwoPane){
            ViewPagerFragment viewPagerFragment = (ViewPagerFragment) getFragmentManager().findFragmentById(R.id.view_pager_fragment);
            if(viewPagerFragment!=null && viewPagerFragment instanceof ArticleChangeListener)
            viewPagerFragment.onPageChanged(pos);

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
            detailIntent.setData(ItemsContract.Items.buildItemUri(pos));
            Bundle bundle = ActivityOptions
                    .makeSceneTransitionAnimation(this)
                    .toBundle();
            startActivity(detailIntent, bundle);
        }
    }

    public interface ArticleChangeListener {
        void onPageChanged(int position);
    }

}
