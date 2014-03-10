package com.example.patheffecttest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class LineGraph extends View implements View.OnClickListener {

	private List<List<Pair<Float,Float>>> mSeries;
	
	private boolean mDrawXAxisLabels;
	private boolean mDrawYAxisLabels;
	
	private boolean mDrawXAxisValues;
	private boolean mDrawYAxisValues;
	
	private boolean mDrawXAxisIntervals;
	private boolean mDrawYAxisIntervals;
	
	private float mXAxisMin;
	private float mXAxisMax;
	private float mXAxisIntervalLength;
	
	private float mYAxisMin;
	private float mYAxisMax;
	private float mYAxisIntervalLength;
	
	private float mXAxisLength;
	private float mYAxisLength;
	private List<Float> mSeriesPathLength;
	
	private Path mPathXAxis;
	private Path mPathYAxis;
	private List<Path> mPathXAxisIntervals;
	private List<Path> mPathYAxisIntervals;
	private List<Path> mPathSeries;
	private List<List<Path>> mPathSeriesPoints;
	
	private Paint mPaint;
	private ObjectAnimator mAnimator;
	private float mLength;
	
	public LineGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
		
		setOnClickListener(this);
		
		mDrawXAxisLabels = true;
		mDrawYAxisLabels = true;
		mDrawXAxisValues = true;
		mDrawYAxisValues = true;
		mDrawXAxisIntervals = true;
		mDrawYAxisIntervals = true;
		mSeries = new ArrayList<List<Pair<Float,Float>>>();
		List<Pair<Float,Float>> series = new ArrayList<Pair<Float,Float>>();
		series.add(new Pair<Float,Float>(1f,1f));
		series.add(new Pair<Float,Float>(2f,1f));
		series.add(new Pair<Float,Float>(3f,2f));
		series.add(new Pair<Float,Float>(1.5f,2.2f));
		series.add(new Pair<Float,Float>(0.75f,4f));
		
		
		//TODO: need to order the pairs by an axis to make sure the line is formed correctly
		Collections.sort(series, new Comparator<Pair<Float, Float>>() {
			@Override
			public int compare(Pair<Float, Float> left, Pair<Float, Float> right) {
				if(left.first == right.first) return 0;
				if(left.first > right.first) return 1;
				return -1;
			}
		});
		
		mSeries.add(series);
		
		mXAxisMin = Float.MIN_VALUE;
		mXAxisMax = Float.MAX_VALUE;
		mYAxisMin = Float.MIN_VALUE;
		mYAxisMax = Float.MAX_VALUE;
		for(List<Pair<Float,Float>> s : mSeries) {
			for(Pair<Float,Float> p : s) {
				if(p.first < mXAxisMin) {
					mXAxisMin = p.first;
				}
				if(p.first > mXAxisMax) {
					mXAxisMax = p.first;
				}
				if(p.second < mYAxisMin) {
					mYAxisMin = p.second;
				}
				if(p.second > mYAxisMax) {
					mYAxisMax = p.second;
				}
			}
		}
	}
	
	private void init() {
		mAnimator = ObjectAnimator.ofFloat(this, "length", 1f, 0.0f).setDuration(3000);
		mAnimator.setInterpolator(new AccelerateInterpolator (1.0f));
		post(new Runnable() {
            @Override
            public void run() {
                mAnimator.start();
            }
        });
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//Now we know our dimensions, so go ahead and determine adjusted points.
		Path p = new Path();
		p.moveTo(mSeries.get(0).get(0).first * 100f, mSeries.get(0).get(0).first * 100f);
		for(int i=1; i<mSeries.get(0).size(); i++) {
			p.lineTo(mSeries.get(0).get(i).first*100f, mSeries.get(0).get(i).second*100f);
		}
		mPathSeries = new ArrayList<Path>();
		mPathSeries.add(p);
		
		init();
	}
	
	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.RED);
		mPaint.setPathEffect(createPathEffect(760, 
				mLength, 
				0));
		c.drawPath(mPathSeries.get(0), mPaint);
		
	}
	
	public void setLength(float length) {
		mLength = length;
		invalidate();
	}
	
	public float getLength() {
		return mLength;
	}

	private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

	@Override
	public void onClick(View v) {
		init();
	}

}
