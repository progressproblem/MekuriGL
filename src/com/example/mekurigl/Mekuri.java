package com.example.mekurigl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.example.android.apis.graphics.spritetext.LabelMaker;
import com.example.android.apis.graphics.spritetext.NumericSprite;
import com.example.graphic.TextureDrawer;
import com.example.graphic.TextureLoader;
import com.example.mekurigl.actor.BackgroundManager;
import com.example.mekurigl.actor.CircleManager;
import com.example.mekurigl.actor.PersonManager;
import com.example.mekurigl.actor.SkirtManager;
import com.example.sound.NoiseHandler;
import com.example.sound.Recorder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Mekuri extends GameBase implements NoiseHandler {

	private static final String TAG = Mekuri.class.getSimpleName();

	public static final int MENU_SELECT_A = 0;
	public static final int MENU_SELECT_B = 1;

    private static final float FPS_NUM = 30.0f;
	private static final int NONE = 0;
	private static final int DRAG = 1;

	// ドラッグ
	private int savedX;
	private int savedY;
	private int mode = NONE;

    // 文字列を描画するためのクラス
    private Paint labelPaint;
    private LabelMaker labels;
    // 文字列ID
    private int labelFps;
    // 数値を描画するためのクラス
    private NumericSprite numericSprite;

	// 構成要素管理クラス
	private BackgroundManager backgroundManager = new BackgroundManager();
	private PersonManager personManager = new PersonManager();
	private SkirtManager skirtManager = new SkirtManager();
	private CircleManager circleManager = new CircleManager();

	// フーフー管理
	private float soundLevel = 3000f;
	private int noiseSensitivity = 100;
	private float noiseSensitivityRetio = 1f;
	private Thread recorderThread;
	private Recorder recorderInstance;

	// タッチエフェクト
	private boolean enableTouchEffect = false;

	// FPS表示
	private boolean showFps = false;

    private int splashId = -1;
    
    private boolean initialized = false;

	public Mekuri() {
		super(FPS_NUM, true);
	}

	@Override
	public int getOriginalWidth() {
		return 1024;
	}

	@Override
	public int getOriginalHeight() {
		return 600;
	}

	/**
	 * @Override アクティビティ生成時に呼び出される
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		// 文字列をセット
		labels = null;
		numericSprite = null;
		labelPaint = new Paint();
		labelPaint.setTextSize(32);
		labelPaint.setAntiAlias(true);
		labelPaint.setARGB(0xff, 0xff, 0xff, 0xff);

		recordStart();
	}

	/**
	 * @Override 毎フレーム呼ぶ更新処理
	 */
	protected void update()
	{
        if (!initialized){
            return;
        }

		backgroundManager.update();
		personManager.update();
		skirtManager.update();
		circleManager.update();
	}

	public boolean onTouchEvent(MotionEvent event) {
        if (!initialized){
            return false;
        }
		float distance;
		double direction;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (enableTouchEffect){
					circleManager.addCircleWait(event.getX(), event.getY());
				}

				if (!skirtManager.isTouchable((int)event.getX(), (int)event.getY())){
					break;
				}
				savedX = (int)event.getX();
				savedY = (int)event.getY();

				mode = DRAG;

				Log.d(TAG, "DRAG START");
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				if (mode == DRAG){
					distance = getDistance(savedX, savedY, (int)event.getX(), (int)event.getY());

					if (distance >= SkirtManager.STEP){
						startAnimation();
					}
				}
				mode = NONE;
				Log.d(TAG,"DRAG END UP");
				break;
			case MotionEvent.ACTION_MOVE:
				if (enableTouchEffect){
					circleManager.addCircleWait(event.getX(), event.getY());
				}

				if (mode == DRAG) {
					distance = getDistance(savedX, savedY, (int)event.getX(), (int)event.getY());
					direction = getDirection(savedX, savedY, (int)event.getX(), (int)event.getY());

					if ((direction < 45 || direction > 135) && distance >= SkirtManager.STEP){
						startAnimation();
						mode = NONE;
						Log.d(TAG,"DRAG END DIRECTION");
						break;
					}

					skirtManager.pick(distance);
				}
				break;
		}
		return false;
	}

	private void startAnimation(){
		skirtManager.start();
		backgroundManager.start();
		personManager.start();
		Log.d(TAG, "ANIMATION START");
	}

	private float getDistance(int x1, int y1, int x2, int y2){
		float x = x1 - x2;
		float y = y1 - y2;
		return FloatMath.sqrt(x * x + y * y);
	}

	private double getDirection(int x1, int y1, int x2, int y2){
		double atan = Math.atan2(y2 - y1, x2 - x1);
		double d =  Math.toDegrees(-atan) + 360;// 数学的にわかりやすい値に調整
		if (d >= 360){
			d -= 360;
		}
		return d;
	}

	/**
	 * @Override 毎フレーム呼ぶ描画処理
	 */
	protected void draw(GL10 gl)
	{
		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        if (!initialized){
            TextureDrawer.drawTextureExt(gl, splashId, surfaceWidth, surfaceHeight, offsetLeft, offsetTop, 0, 0, surfaceWidth, surfaceHeight);
            return;
        }

		backgroundManager.draw(gl);
		personManager.draw(gl);
		skirtManager.draw(gl);
		circleManager.draw(gl);

		// テキストを描画
		// 文字列描画
		if (showFps){
			labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
			labels.draw(gl, 0, surfaceHeight - labels.getHeight(labelFps), labelFps);
			labels.endDrawing(gl);
			// 数値描画
			numericSprite.setValue((int) fpsManager.getFPS());
			numericSprite.draw(gl, labels.getWidth(labelFps), surfaceHeight
					- labels.getHeight(labelFps), surfaceWidth, surfaceHeight);
		}

	}

	/**
	 * @Override サーフェイスのサイズ変更時に呼ばれる
	 * @param gl
	 * @param width 変更後の幅
	 * @param height 変更後の高さ
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		Log.d(TAG, "onSurfaceChanged");

		backgroundManager.change(gl, width, height, this.offsetLeft, this.offsetTop);
		personManager.change(gl, width, height, this.offsetLeft, this.offsetTop);
		skirtManager.change(gl, width, height, this.offsetLeft, this.offsetTop);
		circleManager.change(gl, width, height, this.offsetLeft, this.offsetTop);
	}

	/**
	 * @Override サーフェイスが生成される際・または再生成される際に呼ばれる
	 */
	public void onSurfaceCreated(final GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		Log.d(TAG, "onSurfaceCreated");

        initialized = false;
        splashId = TextureLoader.loadTexture(gl, this, R.drawable.splash, false);
        getView().queueEvent(new Runnable(){
            public void run() {
                // 文字列を生成
                if (labels != null)
                {
                    labels.shutdown(gl);
                }
                else
                {
                    labels = new LabelMaker(true, 256, 128);
                }
                labels.initialize(gl);
                labels.beginAdding(gl);
                labelFps = labels.add(gl, "FPS:", labelPaint);
                labels.endAdding(gl);

                // 数値文字列を生成
                if (numericSprite != null)
                {
                    numericSprite.shutdown(gl);
                }
                else
                {
                    numericSprite = new NumericSprite();
                }
                numericSprite.initialize(gl, labelPaint);

                backgroundManager.init(gl, Mekuri.this);
                personManager.init(gl, Mekuri.this);
                skirtManager.init(gl, Mekuri.this);
                circleManager.init(gl, Mekuri.this);

               initialized = true;
            }
        });

        enableTouchEffect = Setting.enableTouchEffect(this);
        noiseSensitivity = Setting.noiseSensitivity(this);
        showFps = Setting.showFps(Mekuri.this);

        noiseSensitivityRetio = noiseSensitivity / 100f;

        recorderInstance.setBoostLevel(noiseSensitivityRetio);
  }

	/**
	 * @Override 一時停止からの再開
	 */
	protected void onResume()
	{
		Log.d(TAG, "onResume");
		super.onResume();
		recordPause(false);
	}

	/**
	 * @Override 停止状態からの再開
	 */
	protected void onRestart()
	{
		Log.d(TAG, "onRestart()");
		super.onRestart();
	}

	/**
	 * @Override アクティビティ一時停止時に呼び出される
	 */
	protected void onPause()
	{
		Log.d(TAG, "onPause()");

		super.onPause();
		recordPause(true);
	}

	/**
	 * @Override アクティビティ停止時に呼び出される
	 */
	protected void onStop()
	{
		Log.d(TAG, "onStop()");
		super.onStop();
		recordPause(true);
	}

	/**
	 * @Override アクティビティ終了時に呼び出される
	 */
	protected void onDestroy()
	{
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
		recordStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SELECT_A, 0, getText(R.string.finish).toString()).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        menu.add(0, MENU_SELECT_B, 0, getText(R.string.settings).toString()).setIcon(android.R.drawable.ic_menu_manage);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SELECT_A:
			finish();
			return true;

			case MENU_SELECT_B:
				Intent it =new Intent();
				it.setClass(Mekuri.this, Setting.class);
				startActivity(it);
				return true;
		}
		return false;
	}

	public void onStartNoise(float level) {
		Log.d(TAG, "StartNoise:" + level);
        if (!initialized){
            return;
        }
		float d = level / 100;
		if (d > SkirtManager.STEP){
			skirtManager.pick(d);
		}
	}

	public void onNoising(float level) {
		Log.d(TAG, "Noising:" + level);
        if (!initialized){
            return;
        }
//		float d = level / 100f * ((100f - (float)noiseSensitivity) / 50f);
		float d = soundLevel + ((100f - (float)noiseSensitivity) / 50f);
		Log.d(TAG, "level:" + d);

		if (d > SkirtManager.STEP){
			skirtManager.pick(d);
		}
	}

	public void onEndNoise(float level) {
		Log.d(TAG, "EndNoise:" + level);
        if (!initialized){
            return;
        }
		if (!skirtManager.isNowAnimate()){
			startAnimation();
		}
	}

	public void recordStart(){
		if (recorderThread != null || recorderInstance != null){
			Log.d(TAG, "recorder alive");
			recordStop();
		}

		float level = soundLevel * noiseSensitivity / 50;
		recorderInstance = new Recorder(level, this);
		recorderThread = new Thread(recorderInstance);
		recorderThread.start();
		recorderInstance.setRecording(true);
		Log.d(TAG, "record start!");
	}

	public void recordPause(boolean flag){
		if (recorderInstance != null){
			if (flag){
				recorderInstance.setRecording(flag);
			}
			recorderInstance.setPaused(flag);
		}
	}

	public void recordStop(){
		if (recorderInstance != null){
			recorderInstance.stop();
		}
		try {
			if (recorderThread != null){
				recorderThread.join();
			}
			recorderThread = null;
			recorderInstance = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "record stop!");
	}
}
