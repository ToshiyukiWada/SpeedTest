package jp.futuresoftware.android.sakura.core;

import java.util.List;

import jp.futuresoftware.android.sakura.SAKURA;
import jp.futuresoftware.android.sakura.SAKURA.STATUS;
import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager.TouchEvent;
import jp.futuresoftware.android.sakura.information.TextureButtonInformation;

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
	private int textureButtonMapping[];

	public SakuraProcess(SakuraActivity sakuraActivity)
	{
		this.sakuraManager				= sakuraActivity.getSakuraManager();
		this.running					= true;
		this.textureButtonMapping		= new int[20];
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
						for (int countButtons = 0 ; countButtons < this.sakuraManager.getNowTextureButtonInformations().size() ; countButtons++)
						{
							// ボタン情報の取得
							TextureButtonInformation textureButtonInformation = this.sakuraManager.getNowTextureButtonInformations().get(countButtons);

							//-------------------------------------------------
							// タッチイベントの判定
							//-------------------------------------------------
							// タッチ状態の判定
							if (textureButtonInformation.getX() <= touchEvent.x && touchEvent.x <= textureButtonInformation.getX2() && textureButtonInformation.getY() <= touchEvent.y && touchEvent.y <= textureButtonInformation.getY2() && touchEvent.type == TouchEvent.TOUCH_DOWN)
							{
								// Down(指を画面に触れる)
								this.textureButtonMapping[touchEvent.pointer] = countButtons;																// 指番号配列に対象ボタンNoを保存
								if (textureButtonInformation.onDown(touchEvent.x, touchEvent.y) == false){ touchEvents.remove(countTouch); countTouch--; }	// 同時にDownイベントをキックする(Downイベントの戻り値がfalseの場合はProcessへの伝達を行わない)
							}
							else if (touchEvent.type == TouchEvent.TOUCH_UP && this.textureButtonMapping[touchEvent.pointer] == countButtons)
							{
								// Up(指を画面から離す)
								textureButtonInformation.setNow(touchEvent.x, touchEvent.y);																// 現在座標を再設定
								if (!textureButtonInformation.isTouchAreaOut()){																			// タッチ有効範囲内で指を離されたか否か
									if (textureButtonInformation.onTouch() == false){ touchEvents.remove(countTouch); countTouch--; }						// 有効範囲内で指を離した場合はTouchイベントを発生させる(Touchイベントの戻り値がfalseの場合はProcessへの伝達を行わない)
								} else {
									if (textureButtonInformation.onUp() == false){ touchEvents.remove(countTouch); countTouch--; }							// 有効範囲外で指を離した場合はUpイベントを発生させる(Upイベントの戻り値がfalseの場合はProcessへの伝達を行わない)
								}
								this.textureButtonMapping[touchEvent.pointer]		= -1;																	// ボタンをタッチしていた指番号のボタン情報をクリア
							}
							else if(touchEvent.type == TouchEvent.TOUCH_DRAGGED && this.textureButtonMapping[touchEvent.pointer] == countButtons)
							{
								// Dragged(タッチしたまま指をなぞる)
								textureButtonInformation.setNow(touchEvent.x, touchEvent.y);																// 現在座標を再設定
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
