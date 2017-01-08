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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class DetailActivity extends AppCompatActivity {

    private String symbol;
    private String history;
    private TextView textView;
    private LineChart chart;
    private List<Entry> historicalQuoteEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        symbol = extras.getString("SYMBOL");
        history = extras.getString("HISTORY");

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(symbol);

        chart = (LineChart) findViewById(R.id.chart);

        extractHistoricalQuotes();
        displayChart();
    }

    protected void extractHistoricalQuotes(){
        GregorianCalendar date = new GregorianCalendar();
        Float close;
        historicalQuoteEntries = new ArrayList<Entry>();

        List<String> historyQuotesList = Arrays.asList(history.split("\n"));

        for(String quote : historyQuotesList){
            String[] quoteItems = quote.split(",");
            date.setTimeInMillis(Long.valueOf(quoteItems[0]));
            close = Float.valueOf(quoteItems[1]);

            historicalQuoteEntries.add(new Entry(date.getTimeInMillis(),close));
        }

    }

    protected  void displayChart(){

        LineDataSet dataSet = new LineDataSet(historicalQuoteEntries, "Label"); // add entries to dataset
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.BLACK); // styling, ...

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        //chart.setScaleXEnabled(true);

    }

}
