package com.example.jagr.alexandria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.jagr.alexandria.fragments.BookListFragment;
import com.example.jagr.alexandria.fragments.DetailFragment;
import com.example.jagr.superduo20.R;

/**
 * Created by ryan.gilreath on 10/8/2015.
 */
public class MainActivity extends AppCompatActivity implements BookListFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BOOKLIST_FRAGMENT_TAG   = "LBFT";
    private static final String DETAIL_FRAGMENT_TAG   = "LBFT";

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                DetailFragment fragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment, DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public void onBookSelected(String ean) {

    }
}
