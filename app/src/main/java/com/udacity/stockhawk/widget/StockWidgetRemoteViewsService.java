package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;


/**
 * Created by Khantil on 23-01-2017.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService{

/*    public static final String[] QUOTE_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_NAME,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,

    };

    public static final int POSITION_ID = 0;
    public static final int POSITION_SYMBOL = 1;
    public static final int POSITION_NAME = 2;
    public static final int POSITION_PRICE = 3;
    public static final int POSITION_ABSOLUTE_CHANGE = 4;
    public static final int POSITION_PERCENTAGE_CHANGE = 5;
    //public static final int POSITION_HISTORY = 6;*/

    final private DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    final private DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    final private DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
                Timber.d(" StockWidgetRemoteViewsService on create method called!");
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(Contract.Quote.uri,
                        Contract.Quote.QUOTE_COLUMNS,
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);


                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                String stockName = data.getString(Contract.Quote.POSITION_NAME);
                String price = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);


                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.stock_name, stockName);
                views.setTextViewText(R.id.price, price);

                Timber.d(" "+symbol+" : "+stockName+" : "+price+" : "+rawAbsoluteChange+" : ");

/*                if (rawAbsoluteChange > 0) {
                    views.setBackgroundResource(R.drawable.percent_change_pill_green);
                } else {
                    views.setBackgroundResource(R.drawable.percent_change_pill_red);
                }*/

                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                Context context = StockWidgetRemoteViewsService.this;
                if (PrefUtils.getDisplayMode(context)
                        .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(R.id.change, change);
                } else {
                    views.setTextViewText(R.id.change, percentage);
                }


                final Intent fillInIntent = new Intent();

                fillInIntent.setData(Contract.Quote.uri);

                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                //views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(Contract.Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
