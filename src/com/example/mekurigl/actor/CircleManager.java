package com.example.mekurigl.actor;

import android.content.Context;
import android.os.SystemClock;

import javax.microedition.khronos.opengles.GL10;

public class CircleManager implements ActorManager{

	private static final int CIRCLE_MAX = 8;
	private static final long WAIT_TIME = 100;

	private Circle[] circles = new Circle[CIRCLE_MAX];
	private long startTime = -1;

	public void init(GL10 gl, Context context) {
		for (int i = 0; i < CIRCLE_MAX; i++){
			circles[i] = null;
		}
		startTime = SystemClock.uptimeMillis();
	}

	public void change(GL10 gl, int width, int height, int offsetLeft, int offsetTop) {
	}

	public void update(){
		for (int i = 0; i < CIRCLE_MAX; i++){
			Circle circle = circles[i];
			if (circle != null){
				circle.update();
				if (!circle.isAlive()){
					circles[i] = null;
				}
			}
		}
	}

	public void draw(GL10 gl) {
		for (int i = 0; i < CIRCLE_MAX; i++){
			Circle circle = circles[i];
			if (circle != null){
				circle.draw(gl);
			}
		}
	}

	public boolean isAlive() {
		return true;
	}

	public void start() {
	}

	public void addCircle(float x, float y){
		for (int i = 0; i < CIRCLE_MAX; i++){
			if (circles[i] == null){
				circles[i] = new Circle(x, y);
				return;
			}
		}
	}

	public void addCircleWait(float x, float y){
		long nowTime = SystemClock.uptimeMillis();

		if ((nowTime - startTime) > WAIT_TIME){
			startTime = nowTime;
			addCircle(x, y);
		}
	}
}
