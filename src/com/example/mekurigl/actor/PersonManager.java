package com.example.mekurigl.actor;

import android.content.Context;
import android.os.SystemClock;
import com.example.graphic.TextureDrawer;
import com.example.graphic.TextureLoader;
import com.example.mekurigl.R;

import javax.microedition.khronos.opengles.GL10;

public class PersonManager implements ActorManager{

	public static final long WAIT_TIME= 2000;

	private int mainID;

	private int surfaceWidth;
	private int surfaceHeight;

	private int offsetLeft = 0;
	private int offsetTop = 0;

	private boolean nowAnimate = false;
	private long startTime;

	public void init(GL10 gl, Context context) {
		mainID = TextureLoader.loadTexture(gl, context, R.drawable.main);
	}

	public void change(GL10 gl, int width, int height) {
		surfaceWidth = width;
		surfaceHeight = height;
	}

	public void change(GL10 gl, int width, int height, int offsetLeft, int offsetTop) {
		surfaceWidth = width;
		surfaceHeight = height;
		this.offsetLeft = offsetLeft;
		this.offsetTop = offsetTop;
	}

	public void update() {
		if (nowAnimate){
			long nowTime = SystemClock.uptimeMillis();
			if ((nowTime - startTime) > WAIT_TIME){
				nowAnimate = false;
			}
		}
	}

	public void draw(GL10 gl) {
		TextureDrawer.drawTextureExt(gl, mainID, surfaceWidth, surfaceHeight, offsetLeft, offsetTop, 0, 0, surfaceWidth, surfaceHeight);
		if (nowAnimate){
			TextureDrawer.drawTextureExt(gl, mainID, surfaceWidth, surfaceHeight, 0, 600, 200 - offsetLeft, -offsetTop, 256, 256);
		}
	}

	public boolean isAlive() {
		return true;
	}

	public void start(){
		nowAnimate = true;
		startTime = SystemClock.uptimeMillis();
	}
}
