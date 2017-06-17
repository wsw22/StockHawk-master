package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

public class DetailActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_DETAIL = "detail fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent launchIntent = getIntent();
        Uri stockItemUri = null;
        if (launchIntent != null) {
            stockItemUri = launchIntent.getData();
        }

        if (savedInstanceState == null) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.STOCK_ITEM_URI, stockItemUri);
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, detailFragment, FRAGMENT_TAG_DETAIL)
                    .commit();
        }
    }
}
