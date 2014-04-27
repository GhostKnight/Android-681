package com.gmu.stratego.board;

import com.gmu.stratego.StrategoBoardActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class BoardTile extends View {
	final private Paint paint = new Paint();
	private Canvas canvas;

	public BoardTile(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public BoardTile(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoardTile(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		this.canvas = canvas;	
		draw();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		StrategoBoardActivity context = (StrategoBoardActivity) getContext();
		context.setSelectedSpace(this);
		return super.onTouchEvent(event);
	}

	private void draw() {
		canvas.drawRect(0, 0, 60, 60, paint);
	}
	
	public void changeBlack() {
		paint.setColor(Color.BLACK);
		invalidate();
	}
	
	public void changeBlue() {
		paint.setColor(Color.BLUE);
		invalidate();
	}
}
