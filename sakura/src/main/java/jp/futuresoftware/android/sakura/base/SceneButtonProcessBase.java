package jp.futuresoftware.android.sakura.base;

/**
 * Created by toshiyuki on 2015/03/31.
 */
public abstract class SceneButtonProcessBase {
	public abstract boolean onTouch(int buttonIndex);			// タッチ(同一座標でDown＆Up)時のイベント
	public abstract boolean onDown(int buttonIndex);			// Down時のイベント
	public abstract boolean onUp(int buttonIndex);				// Up時のイベント
}
