package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_PERCENTAGE_CHANGE;

/**
 * Created by wsw on 17-6-18.
 */

public class StockListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockListWidgetViewFactory();
    }

    private class StockListWidgetViewFactory
            implements RemoteViewsFactory {
        private Cursor mData;

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mData != null) {
                mData.close();
            }

            final long identityToken = Binder.clearCallingIdentity();
            mData = getContentResolver().query(Contract.Quote.URI,
                    Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    null);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mData != null) {
                mData.close();
                mData = null;
            }
        }

        @Override
        public int getCount() {
            if (mData != null) {
                return mData.getCount();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    mData == null || !mData.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.list_item_quote);

            final DecimalFormat dollarFormatWithPlus;
            final DecimalFormat dollarFormat;
            final DecimalFormat percentageFormat;
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");

            views.setTextViewText(R.id.symbol, mData.getString(Contract.Quote.POSITION_SYMBOL));
            views.setTextViewText(R.id.price,
                    dollarFormat.format(mData.getFloat(Contract.Quote.POSITION_PRICE)));

            float rawAbsoluteChange = mData.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = mData.getFloat(POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                views.setInt(R.id.change, "setBackgroundResource",
                        R.drawable.percent_change_pill_green);
            } else {
                views.setInt(R.id.change, "setBackgroundResource",
                        R.drawable.percent_change_pill_red);
            }

            String change = dollarFormatWithPlus.format(rawAbsoluteChange);
            String percentage = percentageFormat.format(percentageChange / 100);

            if (PrefUtils.getDisplayMode(StockListWidgetService.this)
                    .equals(StockListWidgetService.this.getString(R.string.pref_display_mode_absolute_key))) {
                views.setTextViewText(R.id.change, change);
            } else {
                views.setTextViewText(R.id.change, percentage);
            }

            final Intent fillInIntent = new Intent();
            String symbol = mData.getString(Contract.Quote.POSITION_SYMBOL);
            Uri uri = Contract.Quote.makeUriForStock(symbol);
            fillInIntent.setData(uri);
            views.setOnClickFillInIntent(R.id.stock_item, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

}
