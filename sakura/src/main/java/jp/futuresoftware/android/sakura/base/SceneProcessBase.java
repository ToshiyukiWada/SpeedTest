package jp.futuresoftware.android.sakura.base;

import java.util.List;

import jp.futuresoftware.android.sakura.core.SakuraTouchManager;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager.TouchEvent;

/**
 * シーンの内部処理を記述するベースクラス
 * 
 * @author toshiyuki
 *
 */
public abstract class SceneProcessBase extends SakuraBase
{
	//-------------------------------------------------------------------------
	// メンバ変数定義
	//-------------------------------------------------------------------------
	protected SceneBase scene;
	protected SceneRendererBase renderer;
	protected SceneButtonBase button;

    protected int touchCount;
    protected SakuraTouchManager.TouchEvent touchEvent;

	//-------------------------------------------------------------------------
	// メソッド定義
	//-------------------------------------------------------------------------
	public void setScene(SceneBase scene)				{ this.scene	= scene; }
	public void setRenderer(SceneRendererBase renderer)	{ this.renderer = renderer; }
	public void setButton(SceneButtonBase button)		{ this.button	= button; }

	/**
	 * 戻る処理のハック
	 */
	public void onKeyBack()
	{
		this.back();
	}

	//-------------------------------------------------------------------------
	// 仮想関数定義
	//-------------------------------------------------------------------------
	/**
	 * 戻るボタンをタッチしたときの挙動を記述する
	 * 
	 * アプリケーションを終了時は、、、
	 *   this.sakuraManager.finish();
	 * とコールすることで、全てのリソースを開放し、アプリケーションを終了する。
	 * また、シーンを切り替える場合は、、、
	 *   this.sakuraManager.changeScene("シーン名");
	 * とコールすることで、自シーンの終了処理 -> シーン切り替え -> シーン初期処理 -> シーン切り替え完了＆シーン処理開始
	 * となる。
	 */
	public abstract void back();
	
	/**
	 * シーン切り替え時の初期処理を記述する
	 * 
	 * 
	 */
	public abstract void init();
	
	/**
	 * シーン処理を記述する
	 * シーンは60fps毎のコールされ、この中の処理が実行される
	 * 
	 * @param frametime FPSの基準となる値、この数値を加算していくと、1秒間に1になる数値を提供する
	 * @param touchEvents 前回の処理から今回の処理の間に発生したタッチイベントの全て
	 */
	public abstract void process(float frametime, List<TouchEvent> touchEvents);

	/**
	 * シーン切り替え時の終了処理を記述する
	 */
	public abstract void term();
}
