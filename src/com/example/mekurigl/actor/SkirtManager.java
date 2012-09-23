package com.example.mekurigl.actor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import com.example.graphic.TextureDrawer;
import com.example.graphic.TextureLoader;
import com.example.mekurigl.R;

import javax.microedition.khronos.opengles.GL10;

public class SkirtManager implements ActorManager{

	private static final String TAG = SkirtManager.class.getSimpleName();

	public static final long WAIT_TIME= 250;
	public static  final int STEP = 30;

	private int imageLeft = 424;
	private int imageTop = 76;
	private int imageWidth = 512;
	private int imageHeight = 512;
	private int[][] animationPattern ={{0, 0}, {512, 0}, {0, 512}, {512, 512}};
	private int[] animation ={0,1,2,3};
	private int nowAnimation = 0;
	private int nowNo = 0;
	private boolean nowAnimate = false;
	private int[] animationAlpha;

	private int animationID;

	private int surfaceWidth;
	private int surfaceHeight;

	private int offsetLeft = 0;
	private int offsetTop = 0;

	private long startTime = -1;

	public void init(GL10 gl, Context context) {
		animationID = TextureLoader.loadTexture(gl, context, R.drawable.animation);

		// もっといい方法はないのだろうか？
		Bitmap tmpBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.animation);
		animationAlpha = new int[tmpBmp.getWidth() * tmpBmp.getHeight()];
        System.out.println("animeAlphas:" + tmpBmp.getWidth() +":"+ tmpBmp.getHeight());
		tmpBmp.getPixels(animationAlpha, 0, tmpBmp.getWidth(), 0, 0, tmpBmp.getWidth(), tmpBmp.getHeight());

		// この方法は上手く動かない…
//		animationPatternAlpha  = tmpBmp.extractAlpha();
//		animationPatternAlpha  = tmpBmp.copy(Bitmap.Config.ALPHA_8 , true);
		tmpBmp.recycle();
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
				startTime = nowTime;
				nowAnimation--;
				if (nowAnimation < 0 ){
					nowAnimation = 0;
					nowAnimate = false;
				}
			}
		}
	}

	public void draw(GL10 gl) {
		nowNo = animation[nowAnimation];
		TextureDrawer.drawTextureExt(gl, animationID, surfaceWidth, surfaceHeight, animationPattern[nowNo][0], animationPattern[nowNo][1], imageLeft - offsetLeft, imageTop - offsetTop, imageWidth, imageHeight);
	}

	public boolean isAlive() {
		return true;
	}

	public void start(){
		nowAnimate = true;
	}

	public void pick(float distance){
		nowAnimation = (int)(distance / STEP);
		if (nowAnimation > animation.length -1){
			nowAnimation = animation.length - 1;
		}
		nowAnimate = false;
	}

	public boolean isTouchable(int x, int y){
		Log.d(TAG, x + ":" + y);
		if (nowAnimate){
			return false;
		}
		int tmpLeft = imageLeft - offsetLeft;
		int tmpTop = imageTop - offsetTop;
        System.out.println("imageLeft:"+imageLeft);
        System.out.println("imageTop:"+imageTop);
        System.out.println("offsetLeft:"+offsetLeft);
        System.out.println("offsetTop:"+offsetTop);

		Log.d(TAG, tmpLeft + ":" + tmpTop + " - " +
				(tmpLeft + imageWidth - 1) + ":" + (tmpTop + imageHeight - 1));


		if (x < tmpLeft || x > (tmpLeft + imageWidth - 1) ||
				y < tmpTop || y > (tmpTop + imageHeight - 1)){
			return false;
		}

		int bmpX = x - tmpLeft + animationPattern[nowNo][0];
		int bmpY = y - tmpTop + animationPattern[nowNo][1];
		Log.d(TAG, "aXY(" + x +":"+y+")");
		Log.d(TAG, "alphaXY(" + bmpX +":"+bmpY+")");
		int color = animationAlpha[bmpY * imageWidth * 2 +  bmpX]; // アニメパターンは4枚入ってる
		int alpha = Color.alpha(color);
		Log.d(TAG, "alpha:"+alpha);
		return alpha >= 16;  // あまり薄い色もタッチ不可に
	}


	public int getNowAnimation() {
		return nowAnimation;
	}

	public void setNowAnimation(int nowAnimation) {
		this.nowAnimation = nowAnimation;
	}

	public int getNowNo() {
		return nowNo;
	}

	public void setNowNo(int nowNo) {
		this.nowNo = nowNo;
	}

	public boolean isNowAnimate() {
		return nowAnimate;
	}

	public void setNowAnimate(boolean nowAnimate) {
		this.nowAnimate = nowAnimate;
	}
}
