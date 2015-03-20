package jp.futuresoftware.android.sakura.core;

import java.util.concurrent.TimeUnit;

import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;

public class SakuraRendererThread extends Thread
{
	// メンバ変数
	private SakuraManager sakuraManager;			// SakuraManager
	private float targetFps;						// 目標とするFPS

	private boolean running;						// スレッド内ループを実行するか否か
	
	/**
	 * コンストラクタ
	 * 
	 * @param sakuraActivity
	 * @param targetFps
	 */
	public SakuraRendererThread(SakuraActivity sakuraActivity, float targetFps)
	{
		this.sakuraManager			= sakuraActivity.getSakuraManager();
		this.running				= true;
		this.targetFps				= targetFps;
	}
	
	/* (non-Javadoc)
	 * スレッド処理
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		long lastTime				= System.nanoTime();
		long lastTimeBySeconds		= lastTime;
		long frequency = (long)(Math.floor((double)TimeUnit.SECONDS.toNanos(1L) / this.targetFps));

		long currentTime;
		long elapsedTime;
		long elapsedTimeBySeconds;
		long intervalTimeNanoseconds;
		
		while(this.running)
		{
			currentTime		= System.nanoTime();
			elapsedTime		= currentTime - lastTime;
			
			if (elapsedTime > frequency)
			{
				lastTime = currentTime;
				elapsedTimeBySeconds = currentTime - lastTimeBySeconds;
				if (elapsedTimeBySeconds >= TimeUnit.SECONDS.toNanos(1L))
				{
					lastTimeBySeconds	= lastTime;
				}
				{
					// Redererのコール
					this.sakuraManager.getSakuraView().requestRender();
				}
			}
			else
			{
				intervalTimeNanoseconds = frequency - elapsedTime;
				try{TimeUnit.NANOSECONDS.sleep(intervalTimeNanoseconds);}catch(Exception exp){}
			}
		}
	}
	
	/**
	 * 
	 */
	public void stopRunning()
	{
		this.running			= false;
	}
}
