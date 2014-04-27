package com.gmu.stratego.board;

import com.gmu.stratego.R;

import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TableRow;

public class Board extends GridView {
	private final BoardTile[][] tiles = new BoardTile[10][10];
	private final static int boxSize = 60;
	private final static int xOffset = 30;
	private final static int yOffset = 30;
	private final Context context;

	public Board(Context context) {
		super(context);
		this.context = context;
		createBoard();
		//setBackgroundResource(R.drawable.stratego_board);
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}
	
	public void createBoard() {
		for (int vert = 0; vert < 10; vert++) {
			for (int horiz = 0; horiz < 10; horiz++) {
				tiles[vert][horiz] = new BoardTile(context);
				//addView(tiles[vert][horiz]);
			}
		}
	}
}
