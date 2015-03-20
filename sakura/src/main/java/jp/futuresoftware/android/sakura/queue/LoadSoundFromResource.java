package jp.futuresoftware.android.sakura.queue;

import jp.futuresoftware.android.sakura.queue.base.LoadTextureBase;

public class LoadSoundFromResource extends LoadTextureBase
{
	@Override
	public void main()
	{
		try
		{
			// テクスチャ名
			String soundName							= this.args.get(0);																	// 第1パラメータにテクスチャ名が格納されている
			
			// テクスチャ名が存在していなければ、該当データを取得してOpenGLに登録する
			if (!this.sakuraManager.getSounds().containsKey(soundName))
			{
				// 読み込んだ画像をOpenGLに登録する
				int soundID			= this.sakuraManager.getSoundPool().load(this.sakuraManager.getContext(), Integer.parseInt(this.args.get(1)), 1);
				this.sakuraManager.getSounds().put(soundName, soundID);
			}
		}
		catch(Exception exp){}
	}
}
