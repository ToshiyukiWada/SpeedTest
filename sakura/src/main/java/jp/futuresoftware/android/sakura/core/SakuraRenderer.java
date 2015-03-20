package jp.futuresoftware.android.sakura.core;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.SAKURA.STATUS;
import jp.futuresoftware.android.sakura.SakuraManager;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

@SuppressWarnings("unused")
public class SakuraRenderer implements GLSurfaceView.Renderer
{
	// メンバ変数定義
	private SakuraManager sakuraManager;					// SakuraManager
	private STATUS status;									// SakuraManagerのステータスを一時的に保持ていおく領域
	private boolean isNextloopKickInit;						// このループで現在格納されているSceneのInitを実行するか否か
	private boolean isNextloopKickTerm;	
		
	private long prevMilliSecond;							// 前回のonDrawFrameのシステムミリ秒
	private long nowMilliSecond;							// 現在のonDrawFrameのシステムミリ秒
	private int fps;										// Frame/Second
	private float frametime;								// FrameTime(FPSから求めた値、この値を1秒間全てで加算すると1になる値)
	private String[] labels;				// FPS表示用ラベルの文字列を最初に定義しておく(動的に文字列を作成したくない為)
	
	private int count;										// 汎用カウンタ
	
	public SakuraRenderer(SakuraManager sakuraManager)
	{
		this.sakuraManager		= sakuraManager;
		this.isNextloopKickInit		= false;
		this.isNextloopKickTerm		= false;
		
		this.labels		= new String[200];
		for (int count = 0 ; count < 200 ; count++){ this.labels[count]	= new String(Integer.toString(count)); }
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// 初期処理
		this.sakuraManager.ready(gl);
		
		// OpenGL設定
		gl.glEnable(GL10.GL_ALPHA_TEST);															// 透明可能に
		gl.glEnable(GL10.GL_BLEND);																	// ブレンド可能に
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);								// ブレンド可能に
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// 表示設定
		gl.glViewport(0, 0, width, height);																				// 使用する画面の範囲
		GLU.gluOrtho2D(gl, 0.0f, this.sakuraManager.getVirtualWidth(), 0.0f, this.sakuraManager.getVirtualHeight());	// 上記範囲内での解像度
		gl.glClearColor(this.sakuraManager.getBackgroundColorRed(), this.sakuraManager.getBackgroundColorGreen(), this.sakuraManager.getBackgroundColorBlue(), 1.0f);																		// クリア時の背景色
		gl.glEnable(GL10.GL_TEXTURE_2D);																				// 2Dテクスチャの利用を宣言
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);															// 法線データ有効
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);																	// テクスチャ座標配列有効
		
		// Wait画面初期化
		if(this.sakuraManager.getWaitScene() != null && this.sakuraManager.isWaitSceneInitComplete() == false)
		{
			this.sakuraManager.getWaitScene().getSceneRenderer().init(gl);
		}
		
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		// Frametime算出
		this.nowMilliSecond		= System.currentTimeMillis();
		this.fps				= (int)( 1000 / (this.nowMilliSecond-this.prevMilliSecond==0?1:this.nowMilliSecond-this.prevMilliSecond));
		this.frametime			= (this.nowMilliSecond-this.prevMilliSecond) / 1000.0f; if (prevMilliSecond == 0l){ this.frametime = 0.0f; }
		this.prevMilliSecond	= this.nowMilliSecond;
		// if (this.sakuraManager.isDebug()){ if (this.fps < 200){ Log.i("text", this.labels[this.fps]); } }
		
		status		= this.sakuraManager.getStatus();
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		try
		{
			//-----------------------------------------------------------------
			// 実施すべき処理キューが存在している場合はここで処理する
			//-----------------------------------------------------------------
			if (this.sakuraManager.getRendererQueue().size() != 0)
			{
				for (count = 0 ; count < this.sakuraManager.getRendererQueue().size() ; count++)
				{
					this.sakuraManager.getRendererQueue().get(count).main(gl);
					this.sakuraManager.finishThreadQueue();
				}
				
				// キューを実行したので、キューリストをクリアする
				this.sakuraManager.clearRendererQueue();
			}
			
			//-----------------------------------------------------------------
			// 初期処理を起動する場合は初期処理を実施する
			//-----------------------------------------------------------------
			if (this.isNextloopKickInit == true)
			{
				this.sakuraManager.getNowScene().getSceneRenderer().init(gl);
				this.isNextloopKickInit = false;
			}
			
			//-----------------------------------------------------------------
			// 終了処理を起動する場合は終了処理を実施する
			//-----------------------------------------------------------------
			if (this.isNextloopKickTerm == true)
			{
				this.sakuraManager.getNowScene().getSceneRenderer().term(gl);
				this.isNextloopKickTerm = false;
			}
			
			if(status.equals(STATUS.ACTIVE))
			{
				for (int count = 0 ; count < this.sakuraManager.getTextures().size() ; count++){ this.sakuraManager.getTextures().get(count).clearBurstInformation(); }
				this.sakuraManager.getNowScene().getSceneRenderer().setFrametime(this.frametime);
				this.sakuraManager.getNowScene().getSceneRenderer().draw(gl, this.frametime);
			}
			else
			{
				this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, "A", 0, 0, 100);
			}
			
			// FSPレンダリング
			if (this.sakuraManager.isDebug())
			{
				//this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, this.labels[this.fps % 1000 / 100],0 ,0 ,100);
				//this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, this.labels[this.fps % 100 / 10],30 ,0 ,100);
				//this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, this.labels[this.fps % 10],60 ,0 ,100);
			}
		}
		catch(Exception exp)
		{
		}
	}
	
	public void runInit()
	{
		this.isNextloopKickInit		= true;
	}

	public void runTerm()
	{
		this.isNextloopKickTerm		= true;
	}

}
