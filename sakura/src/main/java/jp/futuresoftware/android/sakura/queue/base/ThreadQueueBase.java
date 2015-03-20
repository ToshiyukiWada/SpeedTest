package jp.futuresoftware.android.sakura.queue.base;

import java.util.ArrayList;

import jp.futuresoftware.android.sakura.SakuraManager;

public abstract class ThreadQueueBase
{
	//
	protected SakuraManager sakuraManager;
	protected ArrayList<String> args;
	
	public ThreadQueueBase()
	{
		this.args			= new ArrayList<String>();
	}
	
	public void setSakuraManager(SakuraManager sakuraManager)
	{
		this.sakuraManager	= sakuraManager;
	}
	
	public void addArgs(String arg)
	{
		this.args.add(arg);
	}
	
	public abstract void main();
}
