package com.example.jagr.alexandria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jagr.alexandria.fragments.DetailFragment;
import com.example.jagr.superduo20.R;

/**
 * Created by ryan.gilreath on 10/8/2015.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            DetailFragment fragment = new DetailFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }
}
