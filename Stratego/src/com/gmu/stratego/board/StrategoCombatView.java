package com.gmu.stratego.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class StrategoCombatView extends View {
	private int blueNum;
	private int redNum;
	private boolean redWins;
	public StrategoCombatView(Context context, int blueNum, int redNum, boolean redWins) {
		super(context);
		this.blueNum = blueNum;
		this.redNum = redNum;
		this.redWins = redWins;
	}
	
	private void init(final Canvas canvas) {
		//canvas.setBitmap(Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888));
		
		final Paint paint = new Paint();
		paint.setColor((redWins) ? Color.RED : Color.BLUE);
		
		canvas.drawText((redWins) ? "Red wins!" : "Blue Wins!", 135, 15, paint);
		canvas.drawText("vs.", 145, 70, paint);
		
		//left //top //right //bottom
		final Rect blueRect = new Rect(10, 20, 130, 140);
		final Drawable blueGfx = getResources().getDrawable(blueNum);
		blueGfx.setBounds(blueRect);
		blueGfx.draw(canvas);
		
		final Rect redRect = new Rect(200, 20, 320, 140);
		final Drawable redGfx = getResources().getDrawable(redNum);
		redGfx.setBounds(redRect);
		redGfx.draw(canvas);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		init(canvas);
	}
}
