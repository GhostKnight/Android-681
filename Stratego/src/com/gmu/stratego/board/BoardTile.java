package com.gmu.stratego.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmu.stratego.R;
import com.gmu.stratego.StrategoBoardActivity;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;
import com.gmu.stratego.json.StrategoAction;
import com.gmu.stratego.util.StrategoConstants;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

public class BoardTile extends View {
	private int x;
	private int y;
	final private Paint paint = new Paint();
	private Drawable currentUnit;
	private Canvas canvas;
	private final List<Integer> deploymentSquares = new ArrayList<Integer>(0);
	private int teamColor;
	private int unitPower;
	private StrategoClient client;
	private Integer numberOfUnits = null;

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
		// get the tile name and break it into its coordinates.
		// The view doesn't inherently have any clue where on the screen it is
		// as far as the grid is concerned
		final String tileName = getResources().getResourceEntryName(getId()).replace("tile", "");
		if (tileName.length() == 2) {
			y = Integer.parseInt(String.valueOf(tileName.charAt(0))) + 1; // add one to adjust for Chris's setup
			x = Integer.parseInt(String.valueOf(tileName.charAt(1))) + 1;
			setOnDragListener(new DragListener()); // only want to add listeners on the ones in the board
		}
		client = StrategoClient.getInstance();
		
		// List of all of the deployment squares
		deploymentSquares.addAll(Arrays.asList(StrategoConstants.DEP_TABLE_IDS));
		
		
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (currentUnit == null || !deploymentSquares.contains(getId()))
					return false;
				
				Item item = new Item(Integer.toString(teamColor) + ":" + Integer.toString(unitPower));
				String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_HTML};
				ClipData dragData = new ClipData((CharSequence) v.getTag(), mimeTypes, item);
				View.DragShadowBuilder myShadow = new DragShadowBuilder(v);
				v.startDrag(dragData, myShadow, null, 0);
				return true;
			}
		});
	}
	
	/**
	 * This listener is only for the first portion of the game.  This is where we
	 * catch the user dropping their pieces onto a square to place it.
	 * @author Tim
	 *
	 */
	protected class DragListener implements OnDragListener{
		@Override
		public boolean onDrag(View v, DragEvent event) {
			if (event.getAction() != DragEvent.ACTION_DROP || currentUnit != null)
				return true;
			Log.d("", "DROPPED ON: " + x + ", " + y + ", Data: " + event.getClipData().getItemAt(0).getText());
			final StrategoBoardActivity context = (StrategoBoardActivity) getContext();
			final String gameID = context.getGameID(); // Could possibly do this earlier but here is safest in case of game change
			final String url = URLS.POST_ACTION.replace(":id", gameID);
			final StringBuilder builder = new StringBuilder(event.getClipData().getItemAt(0).getText());
			final int temp = Integer.parseInt(builder.toString().split(":")[0]);
			final int unitPower = Integer.parseInt(builder.toString().split(":")[1]);
			final String unitType = (temp == Color.RED) ? StrategoAction.RED_PIECE : StrategoAction.BLUE_PIECE;
			final StrategoHttpTask task = new StrategoHttpTask() {
				@Override
				public String performTask(String... urls) {
					try {
						StrategoAction placement = new StrategoAction(StrategoAction.PLACE_PIECE, client.getUser());
						placement.setX(x);
						placement.setY(y);
						placement.setPieceValue(unitPower);
						placement.setPieceType(unitType);
						return client.POST(url, placement);
					} catch (Exception e) {
						Log.e("", "", e);
					}
					return null;
				}
				
				@Override
				public void afterTask(String result) {
					// TODO change this to get new actions only
					context.startGameCheckTimer(); 
				}
			};
			task.execute(url);
			return true;
		}
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
		if (numberOfUnits != null && numberOfUnits.intValue() == 0) {
			this.setVisibility(INVISIBLE);
		} else if (currentUnit != null) {
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
	
	public void changeYellow() {
		paint.setColor(Color.YELLOW);
		invalidate();
	}
	
	public void changeRed() {
		paint.setColor(Color.RED);
		invalidate();
	}
	
	public BoardTile setImage(int teamColor, int unitPower) {
		int pieceNum = 0;
		boolean isRed = teamColor == Color.RED;
		switch(unitPower) {
		case 0:
			break;
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
		
		this.teamColor = teamColor;
		this.unitPower = unitPower;
		currentUnit = (unitPower != 0) ? getResources().getDrawable(pieceNum) : null;
		invalidate();
		return this;
	}
	
	/**
	 * This method should be called when getting a confirmed response from the server saying
	 * that the placement of a users piece was legal and the placement has officially occurred.
	 * 
	 * This method essentially cleans up the units that can be placed.
	 */
	public void useOneUnit() {
		if (numberOfUnits == null)
			return;
		
		numberOfUnits--;
		if (numberOfUnits.intValue() == 0) {
			this.setVisibility(INVISIBLE);
		}
	}
	
	public void setNumUnit(int numUnit) {
		numberOfUnits = numUnit;
	}
}
