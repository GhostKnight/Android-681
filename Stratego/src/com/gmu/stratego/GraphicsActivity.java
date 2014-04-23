package com.gmu.stratego;

import com.gmu.stratego.opengl.GameSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GraphicsActivity extends Activity {
	
	    private GLSurfaceView mGLView;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Create a GLSurfaceView instance and set it
	        // as the ContentView for this Activity.
	        mGLView = new GameSurfaceView(this);
	        setContentView(mGLView);
	    }
	}
