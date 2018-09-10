package com.qdotdash.gameofbatteries;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Statistics extends AppCompatActivity {
    int avgs=0,av=0,j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        SharedPreferences sp = getSharedPreferences("AGOB",0);
        SharedPreferences.Editor editor = sp.edit();

        TextView bestgraph = findViewById(R.id.bestgraph);
        TextView averagescore = findViewById(R.id.averagescore);
        GraphView graph = findViewById(R.id.graph);
        int p = sp.getInt("datanumber",0);
        if(p>5){
            p=5;
        }
        BarGraphSeries<DataPoint> series2,series3;

        series2 = new BarGraphSeries<DataPoint>(new DataPoint[]{
        });
        if(p>=1) {
            avgs = sp.getInt("hoursstatistics0", 0);
            series2 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(1, sp.getInt("hoursstatistics0", 0)),

            });
        }

        series3 = new BarGraphSeries<DataPoint>(new DataPoint[]{
        });
        switch(p){
            case 2 : series3 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(2,sp.getInt("hoursstatistics1",-1)),
                    });
                    break;
            case 3 : series3 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(2,sp.getInt("hoursstatistics1",-1)),
                    new DataPoint(3,sp.getInt("hoursstatistics2",-1)),

                    });
                    break;
            case 4 :  series3 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(2,sp.getInt("hoursstatistics1",-1)),
                    new DataPoint(3,sp.getInt("hoursstatistics2",-1)),
                    new DataPoint(4,sp.getInt("hoursstatistics3",-1)),

                    });
                    break;
            case 5 :  series3 = new BarGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(2,sp.getInt("hoursstatistics1",-1)),
                    new DataPoint(3,sp.getInt("hoursstatistics2",-1)),
                    new DataPoint(4,sp.getInt("hoursstatistics3",-1)),
                    new DataPoint(5,sp.getInt("hoursstatistics4",-1)),
                    });
                    break;
        }
        if(p>1) {
            while (j < 5) {
                av = av + sp.getInt("hoursstatistics" + toString().valueOf(j), 0);
                j = j + 1;
            }
            avgs = av/p;
        }

        series2.setColor(Color.parseColor("#ffffff"));
        series2.setAnimated(true);
        series2.setValuesOnTopColor(Color.WHITE);
        series2.setDrawValuesOnTop(true);
        series2.setSpacing(50);
        series2.setDataWidth(2d);
        graph.addSeries(series2);

        series3.setColor(Color.parseColor("#acacac"));
        series3.setAnimated(true);
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.WHITE);
        series3.setSpacing(50);
        series3.setDataWidth(2d);
        graph.addSeries(series3);

        graph.getViewport().setMinX(0d);
        graph.getViewport().setMaxX(6d);
        graph.getViewport().setMinY(0d);
        graph.getViewport().setMaxY(50d);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        series2.setTitle("Latest rate");
        series3.setTitle("Last 4 rates");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(R.color.graph);

        bestgraph.setText("Average Rate");
        averagescore.setText(toString().valueOf(avgs));

    }
}
