package jp.futuresoftware.android.sakura.queue;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import jp.futuresoftware.android.sakura.queue.base.LoadTextureBase;

public class LoadTextureFromResource extends LoadTextureBase
{
	@Override
	public void main()
	{
		try
		{
			//-----------------------------------------------------------------
			// パラメータの取得
			//-----------------------------------------------------------------
			int textureBitmapID						= Integer.parseInt(this.args.get(0));		// テクスチャ画像のIDを取得
			int characterXmlStreamID				= Integer.parseInt(this.args.get(1));		// キャラクター座標指定XMLIDを取得
			int maxBurstSetCount					= -1;

			if (this.args.size() >= 3)
			{
				try					{ maxBurstSetCount = Integer.parseInt(this.args.get(2)); }
				catch(Exception exp){ maxBurstSetCount = -1; }
			}

			//-----------------------------------------------------------------
			// 重複チェック
			//-----------------------------------------------------------------
			boolean isDuplicate						= false;									// 重複チェック用のフラグを定義
			
			// 管理しているテクスチャ一覧から、同一のテクスチャ画像IDとテクスチャキャラクタ定義XMLIDを持つものが無いかをチェックする
			for (int count = 0 ; count < this.sakuraManager.getTextures().size() ; count++)
			{
				// 削除済みのテクスチャ情報にはnullが格納されているので、nullを判定する必要あり
				if (this.sakuraManager.getTextures().get(count) != null)
				{
					// 同一のテクスチャ画像IDとテクスチャキャラクタ定義XMLIDを持つテクスチャが存在した場合、重複チェック用フラグを立てる
					if (this.sakuraManager.getTextures().get(count).getTextureBitmapID() == textureBitmapID
					    && this.sakuraManager.getTextures().get(count).getCharacterXmlStreamID() == characterXmlStreamID)
					{
						isDuplicate						= true;									// 重複チェック用のフラグを立てる
						break;																	// 重複チェック処理はここで完了
					}
				}
			}
			
			//-----------------------------------------------------------------
			// 重複テクスチャが存在していなければ、該当データを取得してOpenGLに登録する
			//-----------------------------------------------------------------
			if (!isDuplicate)
			{
				// テクスチャ画像の読込とManagerへの登録
				BitmapFactory.Options options			= new BitmapFactory.Options();																									// 
				options.inScaled						= false;																														// 
				Bitmap textureBitmap					= BitmapFactory.decodeResource(this.sakuraManager.getContext().getResources(), textureBitmapID, options);	// 

				// テクスチャ定義の読込とManagerへの登録
				InputStream characterXmlStream			= this.sakuraManager.getContext().getResources().openRawResource(characterXmlStreamID);
				
				// 読み込んだ画像をOpenGLに登録する
				this.sakuraManager.addRendererQueue(new LoadTextureAfterForRenderer(this.sakuraManager, textureBitmapID, textureBitmap, characterXmlStreamID, characterXmlStream, maxBurstSetCount));
			}
		}
		catch(Exception exp){}
	}
}
