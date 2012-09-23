package com.example.mekurigl.actor;

import android.os.SystemClock;
import com.example.graphic.OpenGLUtils;

import javax.microedition.khronos.opengles.GL10;

public class Circle implements Actor{

	private static final long WAIT_TIME = 25;
	public static final int MAX = 16;
	public static final int START_SIZE = 3;

	private float x;
	private float y;
	private float radius = 0;
	private int life = 1;
	private boolean alive = true;
	private long startTime = -1;

	public Circle(float x, float y) {
		this.x = x;
		this.y = y;
		startTime = SystemClock.uptimeMillis();
		radius = START_SIZE ;
	}

	public void update(){
		long nowTime = SystemClock.uptimeMillis();

		if ((nowTime - startTime) < WAIT_TIME){
			return;
		}
		startTime = nowTime;

		radius += life * life / 16 + START_SIZE ;

		life++;
		if (life > MAX){
			alive = false;
		}
	}

	public void draw(GL10 gl){
		if (!alive){
			return;
		}

		int value = (MAX - life + 1) * MAX - 1;

		OpenGLUtils.drawCircle(gl, x, y, radius, 30, 3, value, value, value, value);

	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
