package com.gmu.stratego.board;

import com.gmu.stratego.R;
import com.gmu.stratego.StrategoBoardActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class BoardTile extends View {
	final private Paint paint = new Paint();
	private Drawable currentUnit;
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
		if (currentUnit != null) {
			Rect rect = new Rect(0, 0, 60, 60); // TODO: figure out how to do this rectangle bullshit
			currentUnit.setBounds(rect);
			currentUnit.draw(canvas);
			canvas.drawPaint(paint);
		} else {
			canvas.drawRect(0, 0, 60, 60, paint);
		}
	}
	
	public void changeBlack() {
		paint.setColor(Color.BLACK);
		invalidate();
	}
	
	public void changeBlue() {
		paint.setColor(Color.BLUE);
		invalidate();
	}
	
	public void changeRed() {
		paint.setColor(Color.RED);
		invalidate();
	}
	
	public void setImage(int teamColor, int unitPower) {
		int pieceNum = 0;
		boolean isRed = teamColor == Color.RED;
		switch(unitPower) {
		case 1:
			pieceNum = (isRed) ? R.drawable.r1 : R.drawable.b1;
			break;
		case 2:
			pieceNum = (isRed) ? R.drawable.r2 : R.drawable.b2;
			break;
		case 3:
			pieceNum = (isRed) ? R.drawable.r3 : R.drawable.b3;
			break;
		case 4:
			pieceNum = (isRed) ? R.drawable.r4 : R.drawable.b4;
			break;
		case 5:
			pieceNum = (isRed) ? R.drawable.r5 : R.drawable.b5;
			break;
		case 6:
			pieceNum = (isRed) ? R.drawable.r6 : R.drawable.b6;
			break;
		case 7:
			pieceNum = (isRed) ? R.drawable.r7 : R.drawable.b7;
			break;
		case 8:
			pieceNum = (isRed) ? R.drawable.r8 : R.drawable.b8;
			break;
		case 9:
			pieceNum = (isRed) ? R.drawable.r9 : R.drawable.b9;
			break;
		case 10:
			pieceNum = (isRed) ? R.drawable.r10 : R.drawable.b10;
			break;
		case 11:
			pieceNum = (isRed) ? R.drawable.r11 : R.drawable.b11;
			break;
		case 12:
			pieceNum = (isRed) ? R.drawable.r12 : R.drawable.b12;
			break;
		case 13:
			pieceNum = (isRed) ? R.drawable.r13 : R.drawable.b13;
			break;
		}
		
		currentUnit = getResources().getDrawable(pieceNum);
		invalidate();
	}
}
