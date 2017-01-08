package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import timber.log.Timber;

import static android.R.attr.entries;

public class DetailActivity extends AppCompatActivity {

    private String symbol;
    private String history;
    private String name;

    private BarChart chart;
    private List<BarEntry> historicalQuoteEntries;
    private List<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        symbol = extras.getString("SYMBOL");
        history = extras.getString("HISTORY");
        name = extras.getString("NAME");

        setTitle(name); //title of the action bar


        chart = (BarChart) findViewById(R.id.chart);

        extractHistoricalQuotes();
        displayChart();
    }

    protected void extractHistoricalQuotes(){
        Calendar date = Calendar.getInstance();
        Float close;
        historicalQuoteEntries = new ArrayList<BarEntry>();
        labels = new ArrayList<String>();
        List<String> historyQuotesList = Arrays.asList(history.split("\n"));
        int i = 0;
        for(String quote : historyQuotesList){
            String[] quoteItems = quote.split(",");
            date.setTimeInMillis(Long.valueOf(quoteItems[0]));
            close = Float.valueOf(quoteItems[1]);

            historicalQuoteEntries.add(new BarEntry(i, close));
            i++;

        }

        Timber.d(historicalQuoteEntries.toString());

    }

    protected  void displayChart(){
        BarDataSet set = new BarDataSet(historicalQuoteEntries, "Stock Closing Value");
        set.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.animateY(50);
    }

}
