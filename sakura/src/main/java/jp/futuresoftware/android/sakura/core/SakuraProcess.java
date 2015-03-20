package jp.futuresoftware.android.sakura.core;

import java.util.List;

import jp.futuresoftware.android.sakura.SAKURA;
import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.SAKURA.STATUS;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager.TouchEvent;
import jp.futuresoftware.android.sakura.texture.TextureManager.TextureButton;

public class SakuraProcess extends Thread
{
	private SakuraManager sakuraManager;
	private STATUS status;
	
	private boolean running;
	
	private long prevMilliSecond;			// 前回のonDrawFrameのシステムミリ秒
	private long nowMilliSecond;			// 現在のonDrawFrameのシステムミリ秒
	@SuppressWarnings("unused")
	private int fps;						// Frame/Second
	private float frametime;				// FrameTime(FPSから求めた値、この値を1秒間全てで加算すると1になる値)
	private List<TouchEvent> touchEvents;	// 
	private TouchEvent touchEvent;
	private boolean isTouch;
	
	public SakuraProcess(SakuraActivity sakuraActivity)
	{
		this.sakuraManager		= sakuraActivity.getSakuraManager();
		this.running			= true;
	}
	
	public void run()
	{
		while(this.running)
		{
			this.nowMilliSecond		= System.currentTimeMillis();
			this.fps				= (int)( 1000 / (this.nowMilliSecond-this.prevMilliSecond==0?1:this.nowMilliSecond-this.prevMilliSecond));
			this.frametime			= (this.nowMilliSecond-this.prevMilliSecond) / 1000.0f; if (prevMilliSecond == 0l){ this.frametime = 0.0f; }
			if (this.nowMilliSecond - this.prevMilliSecond < SAKURA.UPDATE_SPAN){ try{Thread.sleep(SAKURA.UPDATE_SPAN); }catch(Exception exp){} continue; }
			this.prevMilliSecond	= this.nowMilliSecond;
			
			try
			{
				this.sakuraManager.updateStatus();
				this.status			= this.sakuraManager.getStatus();
				
				//=================================================================
				// 
				// 主処理
				// 
				//=================================================================
				if(this.status.equals(STATUS.ACTIVE))
				{
					// 現在のシーンの処理をキックする
					touchEvents = this.sakuraManager.getSakuraTouchManager().getTouchEvents();

					//---------------------------------------------------------
					// ボタン定義のイベント処理
					//---------------------------------------------------------
					// まずはタッチイベントの件数でループする
					for (int countTouch = 0 ; countTouch < touchEvents.size() ; countTouch++)
					{
						// タッチイベント
						touchEvent		= touchEvents.get(countTouch);
						
						// 現在のシーンで定義されているボタン情報でループさせて、そのボタンの座標に合致しているか否かをチェックする
						for (int countButtons = 0 ; countButtons < this.sakuraManager.getNowSceneButtons().size() ; countButtons++)
						{
							// ボタン情報の取得
							TextureButton textureButton		= this.sakuraManager.getNowSceneButtons().get(countButtons);

							// タッチイベントの座標の対象か否かを先に取得しておく
							isTouch							= false;
							if (textureButton.getX() <= touchEvent.x && touchEvent.x <= textureButton.getX2() && textureButton.getY() <= touchEvent.y && touchEvent.y <= textureButton.getY2()){ isTouch = true; }
								
							// タッチアップの場合の処理
							if (touchEvent.type == TouchEvent.TOUCH_UP)
							{
								if (isTouch || textureButton.getNowPressPointer() == touchEvent.pointer)
								{
									textureButton.onUp(touchEvent.pointer, isTouch);
								}
							}
							
							// ドラッグ中の場合
							if (touchEvent.type == TouchEvent.TOUCH_DRAGGED && textureButton.getNowPressPointer() == touchEvent.pointer)
							{
								if (isTouch)	{ textureButton.restraintDown(touchEvent.pointer); }
								else			{ textureButton.releaseDown(); }
							}

							// タッチダウンされた場合の処理
							if (isTouch == true && touchEvent.type == TouchEvent.TOUCH_DOWN)
							{
								textureButton.onDown(touchEvent.pointer);
								if (textureButton.isDownSignalThrow() == false){ touchEvents.remove(countTouch); countTouch--; }
							}
						}
					}
					
					//---------------------------------------------------------
					// プロセス処理のコール
					//---------------------------------------------------------
					this.sakuraManager.getNowScene().getSceneProcess().process(this.frametime, touchEvents);
				}
				else
				{
					//---------------------------------------
					// 待ちの場合
					//---------------------------------------
					try{Thread.sleep(SAKURA.UPDATE_SPAN); }catch(Exception exp){} continue;
				}
			}
			catch(Exception exp)
			{
				
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
