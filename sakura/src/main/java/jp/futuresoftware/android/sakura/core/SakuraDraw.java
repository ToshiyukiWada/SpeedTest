package jp.futuresoftware.android.sakura.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.base.ParticleBase;
import jp.futuresoftware.android.sakura.base.ParticleBase.ParticleBitBase;
import jp.futuresoftware.android.sakura.texture.TextureManager;
import jp.futuresoftware.android.sakura.texture.TextureManager.BurstInformation;
import jp.futuresoftware.android.sakura.texture.TextureManager.TextureButton;
import jp.futuresoftware.android.sakura.texture.TextureManager.TextureCharacter;

public class SakuraDraw
{
	// メンバ変数定義
	private SakuraManager sakuraMakager;			// SakuraManager
	
	// OpenGLで描画する為に必要な配列の定義
	private float[] position;						// 描画位置を格納する為の配列
	
	private ByteBuffer bbuv;						// テクスチャ貼り付け対象を指定する為の配列を格納するByteBuffer
	private FloatBuffer fbuv;						// テクスチャ貼り付け対象を指定する為の配列を格納するFloatBuffer
	
	private ByteBuffer bb;							// 描画位置を指定する為の配列を格納するByteBuffer
	private FloatBuffer fb;							// 描画位置を指定する為の配列を格納するFloatBuffer
		
	/**
	 * @param sakuraManager
	 */
	public SakuraDraw(SakuraManager sakuraManager)
	{
		this.sakuraMakager		= sakuraManager;
		
		position			= new float[12];
		
		this.bbuv			= ByteBuffer.allocateDirect(8 * 4);
		this.bbuv.order(ByteOrder.nativeOrder());
		this.fbuv			= this.bbuv.asFloatBuffer();
		
		this.bb				= ByteBuffer.allocateDirect(12 * 4);
		this.bb.order(ByteOrder.nativeOrder());
		this.fb				= this.bb.asFloatBuffer();
	}

	//=========================================================================
	// 
	// テクスチャの描画
	//
	// ------------------------------------------------------------------------
	// 高速なレンダリング
	//=========================================================================
	/**
	 * @param gl
	 * @param textureManager
	 * @param characterIndex
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void drawTexture(GL10 gl, TextureManager textureManager, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		//---------------------------------------------------------------------
		// 対象キャラクター情報取得
		//---------------------------------------------------------------------
		TextureCharacter textureCharacter		= textureManager.getCharacter(characterIndex);
		
		//---------------------------------------------------------------------
		// テクスチャ貼り付け(UV)位置
		//---------------------------------------------------------------------
		// 対象キャラクターの描画にVBOを使用するか否かをチェック
		if (textureCharacter.isUseVBO())
		{
			// VBOを使用する場合、VBOを指定するIDが存在しているかチェック(存在していない場合はその場で作成する)
			if (textureCharacter.getVboID() == -1)
			{
				// 現在のUVを一旦FloatBufferに格納する
				fbuv.put(textureCharacter.getUv());
				fbuv.position(0);

				// VBOIDの取得
				int[] buffers = new int[1];
				((GL11)gl).glGenBuffers(1, buffers, 0);
				textureCharacter.setVboID(buffers[0]);
				((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, textureCharacter.getVboID());
				((GL11)gl).glBufferData(GL11.GL_ARRAY_BUFFER, fbuv.capacity() * 4, fbuv, GL11.GL_STATIC_DRAW);
			}

			// VBOによるUV座標の指定
			((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, textureCharacter.getVboID());
			((GL11)gl).glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
			((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		}
		else
		{
			// VBOを使わない描画の場合は、UV座標をそのままFloatBufferに格納する
			fbuv.put(textureCharacter.getUv());
			fbuv.position(0);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, fbuv);
		}

		//---------------------------------------------------------------------
		// 拡大・縮小の指定が無い場合はキャラクターサイズをそのまま使用する
		//---------------------------------------------------------------------
		if (width == -1)	{ width		= textureCharacter.getWidth(); }
		if (height == -1)	{ height	= textureCharacter.getHeight(); }

		//---------------------------------------------------------------------
		// 表示位置
		//---------------------------------------------------------------------
		int virtualHeight	= this.sakuraMakager.getVirtualHeight();
		
		float posXS			= 0;
		float posXE			= 0;
		float posYS			= 0;
		float posYE			= 0;
		
		if (isCenter)
		{
			// 中央座標指定の場合
			float widthHalf	= (float)width / 2.0f;
			float heighthalf= (float)height / 2.0f;
			
			posXS			= (float)(x - widthHalf);
			posXE			= (float)(x + widthHalf);
			posYS			= (float)(virtualHeight - (y - heighthalf));
			posYE			= (float)(virtualHeight - (y + heighthalf));
		}
		else
		{
			// 左上座標指定の場合
			posXS			= (float)(x);
			posXE			= (float)(x + width);
			posYS			= (float)(virtualHeight - (y));
			posYE			= (float)(virtualHeight - (y + height));
		}
		
		this.position[3]  =  posXS;				this.position[4]  =  posYE;			// 左下(Z軸情報は0.0fのままの扱いとする)
		this.position[0]  =  posXS;				this.position[1]  =  posYS;			// 左上(Z軸情報は0.0fのままの扱いとする)
		this.position[9]  =  posXE;				this.position[10] =  posYE;			// 右下(Z軸情報は0.0fのままの扱いとする)
		this.position[6]  =  posXE;				this.position[7]  =  posYS;			// 右上(Z軸情報は0.0fのままの扱いとする)
		fb.put(this.position);														// 
		fb.position(0);																// 
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);								// OpenGLに転送する
		
		// VBOによる色・透明度座標の指定
		if (alpha > 100){ alpha = 100; }
		if (alpha < 0  ){ alpha =   0; }
		((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, this.sakuraMakager.getAlphaVboIDs()[alpha]);
		((GL11)gl).glColorPointer(4, GL10.GL_FLOAT, 0, 0);
		((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		//---------------------------------------------------------------------
		// 描画
		//---------------------------------------------------------------------
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureManager.getGLTextureID());		// 描画するテクスチャを選択
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);								// 描画(主処理)
	}
	
	public void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int alpha, int x, int y)
	{
		TextureManager textureManager		= this.sakuraMakager.getTextures().get(textureID);
		drawTexture(gl, textureManager, textureManager.getCharacterIndex(characterName), isCenter, x, y, alpha, -1 ,-1);
	}
	public void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		TextureManager textureManager		= this.sakuraMakager.getTextures().get(textureID);
		drawTexture(gl, textureManager, textureManager.getCharacterIndex(characterName), isCenter, x, y, alpha, width, height);
	}
	public void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha)
	{
		TextureManager textureManager		= this.sakuraMakager.getTextures().get(textureID);
		drawTexture(gl, textureManager, characterIndex, isCenter, x, y, alpha, -1 ,-1);
	}
	public void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		TextureManager textureManager		= this.sakuraMakager.getTextures().get(textureID);
		drawTexture(gl, textureManager, characterIndex, isCenter, x, y, alpha, width, height);
	}

	//=========================================================================
	// 
	// 英数字の描画
	//
	// ------------------------------------------------------------------------
	// 高速なレンダリング
	//=========================================================================
	/**
	 * @param gl
	 * @param text
	 * @param x
	 * @param y
	 */
	public void drawAlphaNum(GL10 gl, String text, int x, int y, int alpha)
	{
		drawTexture(gl, this.sakuraMakager.getSakuraTexture().getTextureManager(), this.sakuraMakager.getSakuraTexture().getTextureManager().getCharacterIndex(text), false, x, y, alpha, 32 ,32);
	}
	
	//=========================================================================
	// 
	// ボタン
	//
	// ------------------------------------------------------------------------
	// 高速なレンダリング
	//=========================================================================
	/**
	 * GLボタンの描画
	 * 
	 * @param gl
	 * @param textureName
	 * @param buttonIndex
	 * @param isCenter
	 * @param x
	 * @param y
	 * @param alpha
	 * @param width
	 * @param height
	 */
	public void drawButton(GL10 gl, int textureID, int buttonHander, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		TextureManager textureManager		= this.sakuraMakager.getTextures().get(textureID);								// テクスチャの取得
		TextureButton  textureButton		= this.sakuraMakager.getNowSceneButtons().get(buttonHander);
		TextureCharacter textureCharacter	= textureManager.getCharacter(textureButton.getNowCharacterIndex());
		if (width == -1 ){ width 			= textureCharacter.getWidth();  }
		if (height == -1){ height 			= textureCharacter.getHeight(); }
		if (isCenter)	{ textureButton.set(x - (width / 2), y - (height / 2), width, height); }								//　ボタンの座標情報を最新のものに更新する
		else			{ textureButton.set(x, y, width, height); }																// ボタンの座標情報を最新のものに更新する
		this.drawTexture(gl, textureManager, textureButton.getNowCharacterIndex(), isCenter, x, y, alpha, width, height);		// ボタンの描画
	}

	//=========================================================================
	// 
	// パーティクル
	//
	// ------------------------------------------------------------------------
	// 同一テクスチャを高速に描画する為の仕組みを提供する
	//=========================================================================
	/**
	 * パーティクルの描画処理
	 * 
	 * @param gl
	 * @param particleBase
	 * @param frametime
	 */
	public void drawParticle(GL10 gl, ParticleBase particleBase, float frametime)
	{
		// パーティクルからパーティクルビット情報配列の取得
		ParticleBitBase[] particleBits	= particleBase.getParticleBits();
		TextureManager textureManage	= this.sakuraMakager.getTextures().get(particleBase.getTextureID());

		// パーティクル処理
		particleBase.animation(frametime);
		
		// パーティクル終了チェック
		particleBase.checkStopParticle(particleBits);
		if (particleBase.isActive() == false){ return; }
		
		// 処理で利用する変数の定義
		int vertexIndex = 0;
		int colorIndex = 0;
		int texCoordIndex = 0;
		int activeParticleCount = 0;

		// パーティクルビットループ処理開始
		// for (ParticleBitBase particleBitBase: particleBits)
		ParticleBitBase particleBitBase;
		for (int count = particleBits.length - 1; count >= 0 ; count--)
		{
			particleBitBase		= particleBits[count];
			
			// パーティクルビットのライフタイム減少処理
			particleBitBase.subLifetime(frametime);
			
			// パーティクルビット終了チェック
			particleBase.checkStopParticleBit(particleBitBase);
			if (particleBitBase.isActive() == false){ continue; }

			// パーティクルアニメーション実施
			particleBitBase.animation(frametime);
			
			//頂点座標を追加します
			float centerX		= particleBitBase.getX();
			float centerY		= particleBitBase.getY();
			float sizeW			= particleBitBase.getSize() / 2.0f;
			float sizeH			= particleBitBase.getSize() / 2.0f;
			float vLeft			= centerX - sizeW;
			float vRight		= centerX + sizeW;
			float vTop			= this.sakuraMakager.getVirtualHeight() - (centerY + sizeH);
			float vBottom		= this.sakuraMakager.getVirtualHeight() - (centerY - sizeH);
 
			//ポリゴン1
			particleBase.getVertices()[vertexIndex++] = vLeft;		// C
			particleBase.getVertices()[vertexIndex++] = vBottom;	// C
			particleBase.getVertices()[vertexIndex++] = vRight;		// D
			particleBase.getVertices()[vertexIndex++] = vBottom;	// D
			particleBase.getVertices()[vertexIndex++] = vLeft;		// A
			particleBase.getVertices()[vertexIndex++] = vTop;		// A
			particleBase.getVertices()[vertexIndex++] = vRight;		// D
			particleBase.getVertices()[vertexIndex++] = vBottom;	// D
			particleBase.getVertices()[vertexIndex++] = vLeft;		// A
			particleBase.getVertices()[vertexIndex++] = vTop;		// A
			particleBase.getVertices()[vertexIndex++] = vRight;		// B
			particleBase.getVertices()[vertexIndex++] = vTop;		// B
 
			//色
			for (int j = 0; j < 6; j++) {
				particleBase.getColors()[colorIndex++] = 1.0f;
				particleBase.getColors()[colorIndex++] = 1.0f;
				particleBase.getColors()[colorIndex++] = 1.0f;
				particleBase.getColors()[colorIndex++] = particleBitBase.getAlpha() / 100.0f;
			}
 
			//マッピング座標
			//ポリゴン1
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[0];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[1];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[4];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[5];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[2];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[3];
			//ポリゴン2
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[4];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[5];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[2];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[3];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[6];
			particleBase.getCoords()[texCoordIndex++] = textureManage.getCharacter(particleBitBase.getCharacterIndex()).getUv()[7];
 
			//アクティブパーティクルの数を数えます
			activeParticleCount++;
		}
		
		// レンダリング
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureManage.getGLTextureID());
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, particleBase.getVerticesBuffer());
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, particleBase.getColorBuffer());
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, particleBase.getCoordBuffer());
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, activeParticleCount * 6);
	}
	
	//=========================================================================
	// 
	// burstTexture
	//
	// ------------------------------------------------------------------------
	// 高速なレンダリング
	//=========================================================================
	public void burstTexture(int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		try
		{
			TextureManager textureManage	= this.sakuraMakager.getTextures().get(textureID);
			textureManage.addBurstInformation(textureID, characterIndex, isCenter, x, y, alpha, width, height);
		}
		catch(Exception exp){}
	}
	
	public void burstTextureRenderer(GL10 gl, int textureID)
	{
		TextureManager textureManage	= this.sakuraMakager.getTextures().get(textureID);
		
		// 処理で利用する変数の定義
		int vertexIndex = 0;
		int colorIndex = 0;
		int texCoordIndex = 0;
		int activeParticleCount = 0;

		// パーティクルビットループ処理開始
		// for (ParticleBitBase particleBitBase: particleBits)
		BurstInformation burstInformation;
		for (int count = 0 ; count < textureManage.getBurstInformations().length ; count++)
		{
			burstInformation		= textureManage.getBurstInformations()[count];
			if (!burstInformation.isActive()){ break; }
			
			//頂点座標を追加します
			float centerX		= (float)(burstInformation.getX());
			float centerY		= (float)(burstInformation.getY());
			float sizeW			= (float)(burstInformation.getWidth()) / 2.0f;
			float sizeH			= (float)(burstInformation.getHeight()) / 2.0f;
			float vLeft			= centerX - sizeW;
			float vRight		= centerX + sizeW;
			float vTop			= this.sakuraMakager.getVirtualHeight() - (centerY + sizeH);
			float vBottom		= this.sakuraMakager.getVirtualHeight() - (centerY - sizeH);
 
			//ポリゴン1
			textureManage.getVertices()[vertexIndex++] = vLeft;		// C
			textureManage.getVertices()[vertexIndex++] = vBottom;	// C
			textureManage.getVertices()[vertexIndex++] = vRight;	// D
			textureManage.getVertices()[vertexIndex++] = vBottom;	// D
			textureManage.getVertices()[vertexIndex++] = vLeft;		// A
			textureManage.getVertices()[vertexIndex++] = vTop;		// A
			textureManage.getVertices()[vertexIndex++] = vRight;	// D
			textureManage.getVertices()[vertexIndex++] = vBottom;	// D
			textureManage.getVertices()[vertexIndex++] = vLeft;		// A
			textureManage.getVertices()[vertexIndex++] = vTop;		// A
			textureManage.getVertices()[vertexIndex++] = vRight;	// B
			textureManage.getVertices()[vertexIndex++] = vTop;		// B
 
			//色
			for (int j = 0; j < 6; j++) {
				textureManage.getColors()[colorIndex++] = 1.0f;
				textureManage.getColors()[colorIndex++] = 1.0f;
				textureManage.getColors()[colorIndex++] = 1.0f;
				textureManage.getColors()[colorIndex++] = (float)(burstInformation.getAlpha()) / 100.0f;
			}
 
			//マッピング座標
			//ポリゴン1
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[0];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[1];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[4];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[5];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[2];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[3];
			//ポリゴン2
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[4];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[5];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[2];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[3];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[6];
			textureManage.getCoords()[texCoordIndex++] = textureManage.getCharacter(burstInformation.getCharacterIndex()).getUv()[7];
 
			//アクティブパーティクルの数を数えます
			activeParticleCount++;
		}
		
		// レンダリング
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureManage.getGLTextureID());
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, textureManage.getVerticesBuffer());
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, textureManage.getColorBuffer());
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureManage.getCoordBuffer());
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, activeParticleCount * 6);
	}
}
