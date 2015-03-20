package jp.futuresoftware.android.sakura.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;

@SuppressWarnings("unused")
public class SakuraException implements UncaughtExceptionHandler
{
	private SakuraManager sakuraManager;
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	
	public SakuraException(SakuraActivity sakuraActivity)
	{
		this.sakuraManager				= sakuraActivity.getSakuraManager();
		this.uncaughtExceptionHandler	= Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		try
		{
			
			
			
			
		}
		catch(Exception exp){}
	}
}
