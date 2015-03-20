package jp.futuresoftware.android.sakura.queue.base;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.SakuraManager;

/**
 * wait画面を表示しながらテクスチャを読み込む処理の基底となるクラス
 * 
 * @author toshiyuki
 *
 */
public abstract class DeleteTextureBase extends ThreadQueueBase
{
	/**
	 * 描画キューに取得したテクスチャ情報を反映させる処理を追加するクラス
	 * 
	 * @author toshiyuki
	 *
	 */
	public class DeleteTextureAfterForRenderer extends RendererQueueBase
	{
		// メンバ変数
		private SakuraManager sakuraManager;												// Manager
		private int textureID;																// テクスチャID
		
		/**
		 * コンストラクタ
		 * 
		 * @param textureBitmap
		 */
		public DeleteTextureAfterForRenderer(SakuraManager sakuraManager, int textureID)
		{
			this.sakuraManager				= sakuraManager;						// SakuraManager
			this.textureID					= textureID;							// テクスチャID
		}
		
		/* (non-Javadoc)
		 * @see jp.futuresoftware.android.vajra.base.RendererQueueBase#queueMain(javax.microedition.khronos.opengles.GL10, int, int)
		 */
		@Override
		public void main(GL10 gl)
		{
			// 削除するGLTextureIDを格納する変数を準備する
			int deleteGLTextureID		= this.sakuraManager.getTextures().get(this.textureID).getGLTextureID();
System.out.println("DELETE!!!" + deleteGLTextureID);
			// OpenGLから対象のテクスチャ情報を削除する
			int[] tempGLTextureId = { deleteGLTextureID };
			gl.glDeleteTextures(1, tempGLTextureId, 0);
			
			// SakuraManagerのテクスチャ情報から削除する
			this.sakuraManager.getTextures().set(this.textureID, null);
		}
	}
}
