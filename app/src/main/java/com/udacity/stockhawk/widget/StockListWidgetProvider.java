package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by wsw on 17-6-18.
 */

public class StockListWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            // create widget view
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stock_list);

            // set pending intent for the whole widget
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, mainActivityPendingIntent);

            // set stock list
            Intent widgetServiceIntent = new Intent(context, StockListWidgetService.class);
            views.setRemoteAdapter(R.id.widget_stock_list, widgetServiceIntent);
            views.setEmptyView(R.id.widget_stock_list, R.id.widget_text_no_data);

            // set list item pending intent template
            Intent detailActivityIntent = new Intent(context, DetailActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(detailActivityIntent);
            PendingIntent detailActivityPendingIntentTemplate = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_stock_list, detailActivityPendingIntentTemplate);

            // update widget
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }


}
