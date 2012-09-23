package com.example.mekurigl.actor;

import javax.microedition.khronos.opengles.GL10;

public interface Actor {

	public void update();

	public void draw(GL10 gl);

	public boolean isAlive();

}
