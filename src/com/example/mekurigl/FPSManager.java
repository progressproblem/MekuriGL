package com.example.mekurigl;

import java.util.LinkedList;
import android.os.SystemClock;

/**
 * FPS算出クラス
 */
public class FPSManager
{
    // 前回calcFPS() を呼び出した際の値
    private long prevTime;
    // 前回calcFPS() を呼び出した際の差分値
    private long elapsedTime;
    // FPS値
    private float fps;
    // 経過時間の和
    private long times;
    // 毎フレームの経過時間を、サンプル数だけ保持するQueue
    private LinkedList<Long> elapsedTimeList;
    // サンプル数
    private int sampleNum;

    /**
     * コンストラクタ
     *
     * @param sample_num サンプル値
     */
    public FPSManager(int sample_num)
    {
        prevTime = 0l;
        elapsedTime = 0l;
        fps = 0.0f;
        times = 0l;
        elapsedTimeList = new LinkedList<Long>();
        for (int i = 0; i < sample_num; i++)
            elapsedTimeList.add(0l);
        sampleNum = sample_num;
    }

    /**
     * 前回のcalcFPS()呼び出し時からの差分をとってFPS値を計測
     */
    public void calcFPS()
    {
        // ブート後のミリ秒を取得
        long now_time = SystemClock.uptimeMillis();

        elapsedTime = now_time - prevTime;
        prevTime = now_time;

        // 経過時間を加算
        times += elapsedTime;
        // 経過時間リストに追加し
        elapsedTimeList.add(elapsedTime);
        // リスト内の最も古いものを削除
        times -= elapsedTimeList.poll();

        // 平均時間を計測
        long tmp = times / sampleNum;

        // FPS値を算出
        if (tmp != 0l)
            fps = 1000.0f / tmp;
        else
            fps = 0.0f;
    }

    /**
     * 現在のFPS値を返す
     *
     * @return fps値を表すfloat値
     */
    public float getFPS()
    {
        return fps;
    }

    /**
     * 前回からの経過時間を返す
     *
     * @return 経過時間(ミリ秒)を表すlong値
     */
    public long getElapsedTime()
    {
        return elapsedTime;
    }
}
