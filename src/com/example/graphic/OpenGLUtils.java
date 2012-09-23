package com.example.graphic;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OpenGLUtils {

    //円の描画
    public static void drawCircle(GL10 gl, float x,float y, float radius, int divides, int size, int r, int g, int b, int a) {

		gl.glLineWidth( size );
		float[] vertexs=new float[divides*3];//頂点
		float[] colors =new float[divides*4];//色

        //頂点配列情報
        for (int i=0;i<divides;i++) {
            float angle=(float)(2*Math.PI*i/divides);
            vertexs[i*3  ]=(float)( x+Math.cos(angle) * radius);
            vertexs[i*3+1]=(float)(-y+Math.sin(angle) * radius);
            vertexs[i*3+2]=0;
        }

        //カラー配列情報
        for (int i=0;i<divides;i++) {
            colors[i*4  ]=r;
            colors[i*4+1]=g;
            colors[i*4+2]=b;
            colors[i*4+3]=a;
        }
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );	//　頂点配列の許可
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );	//　色情報配列の許可

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0, FloatBuffer.wrap(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,FloatBuffer.wrap(colors));
        gl.glPushMatrix();
        gl.glDrawArrays(GL10.GL_LINE_LOOP,0,divides);
        gl.glDrawArrays(GL10.GL_POINTS,0,divides);
        gl.glPopMatrix();

		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );	//　頂点配列の無効化
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );	//　色情報配列の無効化
    }

}
