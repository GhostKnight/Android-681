package com.gmu.stratego.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameSurfaceView extends GLSurfaceView {

	public GameSurfaceView(Context context) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(new MyRenderer());
	}
}
