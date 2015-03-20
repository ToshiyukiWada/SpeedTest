package jp.futuresoftware.android.sakura.core;

import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.queue.base.ThreadQueueBase;

public class ThreadQueueThread extends Thread
{
	private SakuraManager sakuraManager;
	private ThreadQueueBase threadQueueBase;
	
	
	public ThreadQueueThread(SakuraManager sakuraManager, ThreadQueueBase threadQueueBase)
	{
		this.sakuraManager			= sakuraManager;
		this.threadQueueBase		= threadQueueBase;
	}
	
	public void run()
	{
		try
		{
			this.threadQueueBase.main();
		}
		catch(Exception exp){}
		this.sakuraManager.finishThreadQueue();
	}
}
