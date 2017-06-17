package com.udacity.stockhawk.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String STOCK_ITEM_URI = "item uri";

    private Unbinder mUnbinder;
    private Uri mStockItemUri;

    @BindView(R.id.stock_graph)
    GraphView mStockGraph;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(STOCK_ITEM_URI)) {
            mStockItemUri = arguments.getParcelable(STOCK_ITEM_URI);
        }

        if (mStockItemUri != null) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                mStockItemUri,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        if (data.moveToFirst()) {
            String historyString = data.getString(Contract.Quote.POSITION_HISTORY);

            String[] historyItemArrays = historyString.split("\n");

            DataPoint[] dataPoints = new DataPoint[historyItemArrays.length];

            int subStringSeparatorIndex = historyItemArrays[0].indexOf(",");

            for (int i = 0; i < historyItemArrays.length; i++) {
                long timeInMillSeconds = Long.parseLong(historyItemArrays[i]
                        .substring(0, subStringSeparatorIndex));

                Date date = new Date(timeInMillSeconds);

                double price = Double.parseDouble(historyItemArrays[i]
                        .substring(subStringSeparatorIndex + 1));

                dataPoints[i] = new DataPoint(date, price);
            }
            series = new LineGraphSeries<>(dataPoints);
        }

        mStockGraph.addSeries(series);
        mStockGraph.getGridLabelRenderer()
                .setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        mStockGraph.getViewport().setXAxisBoundsManual(true);
        mStockGraph.getGridLabelRenderer().setHumanRounding(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
