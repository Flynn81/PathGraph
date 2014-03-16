package com.example.patheffecttest;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Pair;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LineGraph graph = (LineGraph) findViewById(R.id.line_graph);
		
		List<Pair<Float,Float>> series = new ArrayList<Pair<Float,Float>>();
		series.add(new Pair<Float,Float>(1f,1f));
		series.add(new Pair<Float,Float>(2f,1f));
		series.add(new Pair<Float,Float>(3f,2f));
		series.add(new Pair<Float,Float>(1.5f,2.2f));
		series.add(new Pair<Float,Float>(0.75f,4f));
		
		graph.addSeries(series);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
