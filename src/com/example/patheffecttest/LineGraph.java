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
import android.util.AttributeSet;
import android.util.Log;
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
	
	private Path mTempPath;
	
	private Paint mPaint;
	private ObjectAnimator mAnimator;
	private float mLength;
	
	public LineGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mTempPath = new Path();
		
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
		
		Collections.sort(series, new Comparator<Pair<Float, Float>>() {
			@Override
			public int compare(Pair<Float, Float> left, Pair<Float, Float> right) {
				if(left.first == right.first) return 0;
				if(left.first > right.first) return 1;
				return -1;
			}
		});
		
		mSeries.add(series);
		
		mXAxisMin = Float.MAX_VALUE;
		mXAxisMax = Float.MIN_VALUE;
		mYAxisMin = Float.MAX_VALUE;
		mYAxisMax = Float.MIN_VALUE;
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
		
		mPathSeries = new ArrayList<Path>();
	}
	
	public void setData(List<List<Pair<Float, Float>>> data) {
		mSeries = data;
		invalidate();
	}
	
	public void addSeries(List<Pair<Float,Float>> series) {
		mSeries.add(series);
		invalidate();
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
		float width = getWidth();
		float height = getHeight();
		float xSize = Math.abs(mXAxisMax - mXAxisMin);
		float ySize = Math.abs(mYAxisMax - mYAxisMin);

		float paddingLeft = getPaddingLeft();
		float paddingRight = getPaddingRight();
		float paddingTop = getPaddingTop();
		float paddingBottom = getPaddingBottom();
		
		xSize = xSize - paddingLeft - paddingRight;
		ySize = ySize - paddingTop - paddingBottom;
		
		//Now we know our dimensions, so go ahead and determine adjusted points.
		for(List<Pair<Float, Float>> series : mSeries) {
			for(Pair<Float, Float> pair : series) {
				Log.d("TK","before - " + pair.first + ", " + pair.second);
			}
		}
		
		//NEED TO REMEMBER WHERE ZERO IS IN THE CANVAS AND ADJUST ACCORDINGLY!
		
		if (mPathSeries != null && mPathSeries.size() == 0 && width > 0) {
			for (List<Pair<Float, Float>> series : mSeries) {
				float adjustedX = series.get(0).first;
				float adjustedY = series.get(0).second;
				adjustedX = (adjustedX - mXAxisMin) / xSize;
				adjustedX = adjustedX * width;
				adjustedY = (adjustedY - mYAxisMin) / ySize;
				adjustedY = adjustedY * height;
				Log.d("TK", "... " + adjustedX + ", " + adjustedY);
				mTempPath.moveTo(adjustedX+paddingLeft, height - adjustedY + paddingTop);

				for (int i = 1; i < series.size(); i++) {
					adjustedX = series.get(i).first;
					adjustedY = series.get(i).second;
					adjustedX = (adjustedX - mXAxisMin) / xSize;
					adjustedX = adjustedX * width;
					adjustedY = (adjustedY - mYAxisMin) / ySize;
					adjustedY = adjustedY * height;
					Log.d("TK", "... " + adjustedX + ", " + adjustedY);
					mTempPath.lineTo(adjustedX + paddingLeft, height - adjustedY + paddingTop);
				}
				
				mPathSeries.add(mTempPath);
			}
		}
		for(List<Pair<Float, Float>> series : mSeries) {
			for(Pair<Float, Float> pair : series) {
				Log.d("TK","after - " + pair.first + ", " + pair.second);
			}
		}
		
		
		
		init();
	}
	
	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		mPaint.setPathEffect(createPathEffect(760, 
				mLength, 
				0));
		for(Path path : mPathSeries) {
			c.drawPath(path, mPaint);
		}
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
