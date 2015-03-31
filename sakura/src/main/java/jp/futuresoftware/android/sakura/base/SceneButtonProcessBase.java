package jp.futuresoftware.android.sakura.base;

/**
 * Created by toshiyuki on 2015/03/31.
 */
public abstract class SceneButtonProcessBase {
	public abstract boolean onTouch();			// タッチ(同一座標でDown＆Up)時のイベント
	public abstract boolean onDown();			// Down時のイベント
	public abstract boolean onUp();				// Up時のイベント
}
