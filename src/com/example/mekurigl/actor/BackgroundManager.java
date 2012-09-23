package com.example.mekurigl.actor;

import android.content.Context;
import android.os.SystemClock;
import com.example.graphic.TextureDrawer;
import com.example.graphic.TextureLoader;
import com.example.mekurigl.R;

import javax.microedition.khronos.opengles.GL10;

public class BackgroundManager implements ActorManager{

	public static final long WAIT_TIME= 1000;

	private int scrollPointer = 0;
	private int scrollSpeed = 1;
	private int backgroundID;
	private int surfaceWidth;
	private int surfaceHeight;

	private long startTime = -1;

	public BackgroundManager() {
	}

	public void init(GL10 gl, Context context) {
		backgroundID = TextureLoader.loadTexture(gl, context, R.drawable.back, false);
	}

	public void change(GL10 gl, int width, int height, int offsetLeft, int offsetTop) {
		surfaceWidth = width;
		surfaceHeight = height;
	}

	public void update() {
		if (scrollSpeed > 2){
			long nowTime = SystemClock.uptimeMillis();
			if ((nowTime - startTime) > WAIT_TIME){
				scrollSpeed--;
			}
		}
	}

	public void draw(GL10 gl) {
		if (scrollPointer < surfaceWidth){
			TextureDrawer.drawTextureExt(gl, backgroundID, surfaceWidth, surfaceHeight, 0, 0, 0, scrollPointer, surfaceWidth, surfaceHeight - scrollPointer);
		}
		if (scrollPointer > 0){
			TextureDrawer.drawTextureExt(gl, backgroundID, surfaceWidth, surfaceHeight, 0, surfaceHeight - scrollPointer, 0, 0, surfaceWidth, scrollPointer);
		}

		scrollPointer+=scrollSpeed;
		if (scrollPointer >= surfaceHeight){
			scrollPointer = 0;
		}
	}

	public boolean isAlive() {
		return true;
	}

	public void start(){
		scrollSpeed++;
		startTime = SystemClock.uptimeMillis();
	}

	public int getSurfaceWidth() {
		return surfaceWidth;
	}

	public void setSurfaceWidth(int surfaceWidth) {
		this.surfaceWidth = surfaceWidth;
	}

	public int getSurfaceHeight() {
		return surfaceHeight;
	}

	public void setSurfaceHeight(int surfaceHeight) {
		this.surfaceHeight = surfaceHeight;
	}

	public int getOffsetLeft() {
		return 0;
	}

	public void setOffsetLeft(int offsetLeft) {
	}

	public int getOffsetTop() {
		return 0;
	}

	public void setOffsetTop(int offsetTop) {
	}
}
