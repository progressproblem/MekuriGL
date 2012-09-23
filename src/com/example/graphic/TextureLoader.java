package com.example.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;

/**
 * テクスチャ読み込みを行うクラス
 */
public class TextureLoader
{
	static {
		System.loadLibrary("NDKImage");
	}

	native private static void texImage2DNonPremultipliedAlpha(int[] pixels, int width, int height);


    public static int loadTexture(GL10 gl, Context context, int resource_id){
        return loadTexture(gl, context, resource_id, true);
    }

   /**
     * テクスチャを読み込む
     *
     * @param gl
     * @param context アクティビティを渡す
     * @param resource_id 読み込むリソースのIDを渡す
     * @param userAlpha アルファ値を使うか？
     * @return 生成したテクスチャのIDを返す
     */
    public static int loadTexture(GL10 gl, Context context, int resource_id, boolean userAlpha)
    {
        int[] textures = new int[1];
        // テクスチャを作成するための固有名を1つ作成
        gl.glGenTextures(1, textures, 0);

        // 作成されたテクスチャのIDをセット
        int texture_id = textures[0];
        // 指定した固有名を持つテクスチャを作成
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_id);

        // バインドされているテクスチャに、テクスチャの拡大・縮小方法を指定。
        // 縮小処理には高速化のためにニアレストネイバー法を用いる
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        // 拡大は線形補完とする。重いようならニアレストネイバー法(GL_NEAREST)へ変更すること
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        // バインドされているテクスチャに、繰り返し方法を指定
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        // バインドされているテクスチャに、テクスチャの色が下地の色を置き換えるよう指定(GL_REPLACE)
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);

        // リソースをオープン
        InputStream is = context.getResources().openRawResource(resource_id);
        Bitmap bitmap;
        try
        {
            // リソースからビットマップデータを作成
            bitmap = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                // 例外処理
            }
        }

        // ビットマップからテクスチャを作成する
//        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		texImage(bitmap, userAlpha);
        // 不要になったビットマップデータを開放する
        bitmap.recycle();

        // テクスチャにアクセスするためのIDを返す
        return texture_id;
    }

	public static void texImage(Bitmap bmp, boolean flag){
		int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
      if (flag){
          bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
          texImage2DNonPremultipliedAlpha(pixels, bmp.getWidth(), bmp.getHeight());
        } else {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        }
	}


}
