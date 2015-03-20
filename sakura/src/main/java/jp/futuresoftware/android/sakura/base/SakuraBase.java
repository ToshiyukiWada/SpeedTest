package jp.futuresoftware.android.sakura.base;

import jp.futuresoftware.android.sakura.SakuraManager;

public class SakuraBase
{
	protected SakuraManager sakuraManager;
	
	public void setSakuraManager(SakuraManager sakuraManager)
	{
		this.sakuraManager		= sakuraManager;
	}
	
	public SakuraManager getSakuraManager()
	{
		return (this.sakuraManager);
	}
}
