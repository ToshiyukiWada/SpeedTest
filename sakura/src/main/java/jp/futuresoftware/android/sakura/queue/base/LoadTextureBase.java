package jp.futuresoftware.android.sakura.queue.base;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.texture.TextureManager;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

/**
 * wait画面を表示しながらテクスチャを読み込む処理の基底となるクラス
 * 
 * @author toshiyuki
 *
 */
public abstract class LoadTextureBase extends ThreadQueueBase
{
	/**
	 * 描画キューに取得したテクスチャ情報を反映させる処理を追加するクラス
	 * 
	 * @author toshiyuki
	 *
	 */
	public class LoadTextureAfterForRenderer extends RendererQueueBase
	{
		// メンバ変数
		private SakuraManager sakuraManager;												// Manager
		private int textureBitmapID;														// リソーステクスチャー画像ID
		private Bitmap textureBitmap;														// テクスチャ画像ファイル
		private int characterXmlStreamID;													// リソーステクスチャーキャラクター定義XMLID
		private InputStream characterXmlStream;												// キャラクター
		private int maxBurstSetCount;														// 高速描画最大数
		
		/**
		 * コンストラクタ
		 * 
		 * @param textureBitmap
		 */
		public LoadTextureAfterForRenderer(SakuraManager sakuraManager, int textureBitmapID, Bitmap textureBitmap, int characterXmlStreamID, InputStream characterXmlStream, int maxBurstSetCount)
		{
			this.sakuraManager					= sakuraManager;						// SakuraManager
			this.textureBitmapID				= textureBitmapID;						// テクスチャ画像ファイルID
			this.textureBitmap					= textureBitmap;						// テクスチャ画像ファイル
			this.characterXmlStreamID			= characterXmlStreamID;					// テクスチャ情報ファイルID
			this.characterXmlStream				= characterXmlStream;					// テクスチャ情報ファイルのInputStream
			this.maxBurstSetCount				= maxBurstSetCount;						// 高速描画最大数
		}
		
		/* (non-Javadoc)
		 * @see jp.futuresoftware.android.vajra.base.RendererQueueBase#queueMain(javax.microedition.khronos.opengles.GL10, int, int)
		 */
		@Override
		public void main(GL10 gl)
		{
			int[] tempTextureId = new int[1];														// textureIdを取得する為の領域の定義
			gl.glGenTextures(1, tempTextureId, 0);													// textureIdを取得
			gl.glBindTexture(GL10.GL_TEXTURE_2D, tempTextureId[0]);									// textureIdを有効化
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);	// 
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);	// 
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.textureBitmap, 0);						// テクスチャ画像を登録
			this.textureBitmap.recycle();															// 登録に使用したテクスチャ画像を破棄
			
			TextureManager textureManager = new TextureManager(tempTextureId[0], textureBitmapID, textureBitmap, characterXmlStreamID, characterXmlStream);
			textureManager.setMaxBurstSetCount(this.maxBurstSetCount);
			this.sakuraManager.getTextures().add(textureManager);
		}
	}
}
