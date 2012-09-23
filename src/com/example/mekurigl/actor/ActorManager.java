package com.example.mekurigl.actor;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

public interface ActorManager extends Actor {

	public void init(GL10 gl, Context context);

	public void change(GL10 gl, int width, int height, int offsetLeft, int offsetTop);

	public void start();

}
