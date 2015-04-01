package jp.futuresoftware.android.sakura.texture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.SakuraManager;

/**
 * @author toshiyuki
 *
 */
public class SakuraTexture
{
	// メンバ変数定義
	private TextureManager textureManager;
	private String[] characters		= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"		// 英字
									  ,"0","1","2","3","4","5","6","7","8","9"																		// 数字
	//								  ,"あ","い","う","え","お"																						// 平仮名(あ行)
	//								  ,"か","き","く","け","こ"																						// 平仮名(か行)
	//								  ,"さ","し","す","せ","そ"																						// 平仮名(さ行)
	//								  ,"た","ち","つ","て","と"																						// 平仮名(た行)
	//								  ,"な","に","ぬ","ね","の"																						// 平仮名(な行)
	//								  ,"は","ひ","ふ","へ","ほ"																						// 平仮名(は行)
	//								  ,"ま","み","む","め","も"																						// 平仮名(ま行)
	//								  ,"や","ゆ","よ"																								// 平仮名(や行)
	//								  ,"ら","り","る","れ","ろ"																						// 平仮名(ら行)
	//								  ,"わ","を","ん"																								// 平仮名(わ行)
	//								  ,"が","ぎ","ぐ","げ","ご"																						// 平仮名(が行)
	//								  ,"ざ","じ","ず","ぜ","ぞ"																						// 平仮名(ざ行)
	//								  ,"だ","ぢ","づ","で","ど"																						// 平仮名(だ行)
	//								  ,"ば","び","ぶ","べ","ぼ"																						// 平仮名(ば行)
	//								  ,"ぱ","ぴ","ぷ","ぺ","ぽ"																						// 平仮名(ぱ行)
	//								  ,"ぁ","ぃ","ぅ","ぇ","ぉ"																						// 平仮名(小さい文字)
	//								  ,"っ","ゃ","ゅ","ょ"																							// 平仮名(小さい文字)
	};

	public int alphaCharacterCodeIndex;
	public int numberCharacterCodeIndex;
	
	/**
	 * コンストラクタ
	 * 
	 * @param sakuraManager
	 * @param gl
	 */
	public SakuraTexture(SakuraManager sakuraManager, GL10 gl)
	{
		// テキストテクスチャの適切なサイズを求める
		int textTextureMinWidthHeight	= sakuraManager.getSakuraTextureFontSize() * (int)Math.sqrt(characters.length);
		int textTextureWidthHeight		= 1;
		while(textTextureWidthHeight < textTextureMinWidthHeight){ textTextureWidthHeight *= 2; }

		// 文字コードの基準を確保
		this.alphaCharacterCodeIndex		= -1;
		this.alphaCharacterCodeIndex		= -1;

		// SakuraTexture用のTextureIDを取得する
		int[] sakuraTextureIDs		= new int[1];
		gl.glGenTextures(1, sakuraTextureIDs, 0);
		this.textureManager			= new TextureManager(sakuraTextureIDs[0], -1, textTextureWidthHeight, textTextureWidthHeight, -1, null);
		
		// 文字列を描画するビットマップの準備
		Paint paint					= new Paint();
		Bitmap bitmap				= Bitmap.createBitmap(textTextureWidthHeight, textTextureWidthHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas				= new Canvas(bitmap);
		paint.setColor(Color.argb(255, (int)sakuraManager.getFontColorRed(), (int)sakuraManager.getFontColorGreen(), (int)sakuraManager.getFontColorBlue()));
		Typeface typeface = null;
		if (!sakuraManager.getSakuraTextureFont().equals(""))
		{
			typeface = Typeface.createFromAsset(sakuraManager.getFontAssetsManager(), sakuraManager.getSakuraTextureFont());
		}
		paint.setTextSize(sakuraManager.getSakuraTextureFontSize());
		if (typeface != null){ paint.setTypeface(typeface); }
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		
		// 文字列の解析とテクスチャの作成
		int characterWidth;																// ここに文字の幅が格納される
		int baseLineTop		= (int) Math.ceil(Math.abs(paint.getFontMetrics().top));	// ここにベースラインからのTOP座標が格納される
		int baseLineBottom	= (int) Math.ceil(Math.abs(paint.getFontMetrics().bottom));	// ここにベースラインからのBOTTOM座標が格納される
		int characterHeight	= (int) Math.ceil(baseLineTop + baseLineBottom); 			// ここに文字の高さが格納される
		Point drawPoint		= null;														// 現在の描画開始ポイント

		//-----------------------------------------------------------
		// 全ての文字
		//-----------------------------------------------------------
		drawPoint				= new Point(0, 0);
		for (int count = 0 ; count < this.characters.length ; count++ )
		{
			if      (this.characters[count] == "A"){ this.alphaCharacterCodeIndex  = count; }
			else if (this.characters[count] == "0"){ this.numberCharacterCodeIndex = count; }

			// 文字の幅を求めながら、Bitmpaに文字を描画していく
			characterWidth		= (int) Math.ceil(paint.measureText(this.characters[count]));
			canvas.drawText(this.characters[count], drawPoint.x, drawPoint.y + baseLineTop, paint);

			// 文字キャラクター情報を同時に作成する
			this.textureManager.addCharacter(this.characters[count], drawPoint.x, drawPoint.y, characterWidth, characterHeight);
			
			// 描画開始ポイントのインクリメントと改行処理
			boolean isLineBreak = false;				// 改行するか否か
			drawPoint.x += characterWidth;				// X軸にインクリメント
			if (count < this.characters.length)			// 次の文字が存在しているか
			{
				if (drawPoint.x + ((int) Math.ceil(paint.measureText(this.characters[count]))) > textTextureWidthHeight)
				{
					isLineBreak = true;					// 改行の必要あり
				}
			}
			
			// 次の文字に向けて改行
			if (isLineBreak)
			{
				drawPoint.y += characterHeight;			// 文字の高さ分だけ加算する
				drawPoint.x = 0;						// X軸は元に戻す
			}
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textureManager.getGLTextureID());				// フレームワークテクスチャIndexを有効にする
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);	// 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);	// 
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);									// フレームワークテクスチャを読み込む
		bitmap.recycle();																		// 生成したBitmapは破棄する
	}
	
	
	/**
	 * @return
	 */
	public TextureManager getTextureManager() {
		return textureManager;
	}
}
