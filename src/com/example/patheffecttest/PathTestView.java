package com.example.patheffecttest;

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
import android.view.View;

public class PathTestView extends View implements View.OnClickListener {

	private float mLength;
	private Path mPath;
	private Path mTextPath;
	private Paint mPaint;
	private ObjectAnimator mAnimator;
	
	public PathTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mPath = new Path();
		mPath.moveTo(10, 10);
		mPath.lineTo(10, 200);
		mPath.lineTo(200, 200);
		mPath.lineTo(200, 10);
		mPath.lineTo(10, 10);
		mPath.arcTo(new RectF(10,-85,200,105), 180, -180, true);
		
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
		
		init();
		
		mTextPath = new Path();
		mPaint.setTextSize(65);
		mPaint.getTextPath("Thomas", 0, 6, 10, 300, mTextPath);
		mTextPath.close();
		setOnClickListener(this);
	}
	
	private void init() {
		mAnimator = ObjectAnimator.ofFloat(this, "length", 1f, 0.0f).setDuration(3000);
		post(new Runnable() {
            @Override
            public void run() {
                mAnimator.start();
            }
        });
	}
	
	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.RED);
		mPaint.setPathEffect(createPathEffect(760, mLength, 0));
		c.drawPath(mTextPath, mPaint);
		c.drawPath(mPath, mPaint);
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
