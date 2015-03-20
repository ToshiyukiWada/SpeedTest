package jp.futuresoftware.android.sakura.queue;

import jp.futuresoftware.android.sakura.queue.base.DeleteTextureBase;

public class DeleteSound extends DeleteTextureBase
{
	@Override
	public void main()
	{
		try
		{
			// テクスチャ名
			String soundName							= this.args.get(0);																	// 第1パラメータにテクスチャ名が格納されている
			int soundID									= -1;
			
			// テクスチャIDを取得する
			soundID										= this.sakuraManager.getSounds().get(soundName);

			// 削除処理をRendererに依頼する
			this.sakuraManager.getSoundPool().unload(soundID);
		}
		catch(Exception exp){}
	}
}
