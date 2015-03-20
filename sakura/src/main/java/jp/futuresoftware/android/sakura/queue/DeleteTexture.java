package jp.futuresoftware.android.sakura.queue;

import jp.futuresoftware.android.sakura.queue.base.DeleteTextureBase;

public class DeleteTexture extends DeleteTextureBase
{
	@Override
	public void main()
	{
		try
		{
			// パラメータの取得
			int textureID							= Integer.parseInt(this.args.get(0));		// テクスチャ画像のIDを取得

			// 削除処理をRendererに依頼する
			this.sakuraManager.addRendererQueue(new DeleteTextureAfterForRenderer(this.sakuraManager, textureID));
		}
		catch(Exception exp){}
	}
}
