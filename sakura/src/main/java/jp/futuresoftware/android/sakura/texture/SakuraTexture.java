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
	};

	// 文字コードからCharacterIndexの開始位置を求める
	public int alphaCharacterCodeIndex;
	public int numberCharacterCodeIndex;

	// デフォルトフォントサイズ
	public static int FONT_SIZE = 32;

	// フォントサイズと、デフォルトフォントサイズの比率をフォントサイズ0～100までの分だけ定義しておく
	public float fontSizeScale[];

	/**
	 * コンストラクタ
	 * 
	 * @param sakuraManager
	 * @param gl
	 */
	public SakuraTexture(SakuraManager sakuraManager, GL10 gl)
	{
		// テキストテクスチャの適切なサイズを求める
		int textTextureMinWidthHeight	= FONT_SIZE * (int)Math.sqrt(characters.length);
		int textTextureWidthHeight		= 1;
		while(textTextureWidthHeight < textTextureMinWidthHeight){ textTextureWidthHeight *= 2; }

		// 文字コードの基準を確保
		this.alphaCharacterCodeIndex		= -1;
		this.alphaCharacterCodeIndex		= -1;

		// フォントサイズ比率算出
		this.fontSizeScale					= new float[101];
		for (int count = 0 ; count < this.fontSizeScale.length ; count++ ){
			this.fontSizeScale[count]		= (float)count / (float)FONT_SIZE;
		}

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
			typeface = Typeface.createFromAsset(sakuraManager.getContext().getAssets(), sakuraManager.getSakuraTextureFont());
		}
		paint.setTextSize(FONT_SIZE);
		if (typeface != null){ paint.setTypeface(typeface); }
		// paint.setTypeface(Typeface.DEFAULT_BOLD);
		
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
	
	public float getFontSizeScale(int fontSize)
	{
		if (0 <= fontSize && fontSize <= 100){ return this.fontSizeScale[fontSize]; }
		return 0;
	}

	/**
	 * @return
	 */
	public TextureManager getTextureManager() {
		return textureManager;
	}
}
