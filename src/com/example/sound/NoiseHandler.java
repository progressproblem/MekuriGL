package com.example.sound;

public interface NoiseHandler {

	public void onStartNoise(float level);

	public void onNoising(float level);

	public void onEndNoise(float level);
}
