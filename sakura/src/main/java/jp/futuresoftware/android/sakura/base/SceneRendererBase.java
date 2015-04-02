package jp.futuresoftware.android.sakura.base;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.core.ParticleManager;

public abstract class SceneRendererBase extends SakuraBase
{
	protected SceneBase scene;
	protected SceneProcessBase process;
	protected SceneButtonBase button;

	protected float frametime;
	private int count;
	
	public void setScene(SceneBase scene)				{ this.scene	= scene; }
	public void setProcess(SceneProcessBase process)	{ this.process	= process; }
	public void setButton(SceneButtonBase button)		{ this.button	= button; }

	public void setFrametime(float frametime)			{ this.frametime	= frametime; }
	
	public abstract void init(GL10 gl);						// 描画スレッドでの初期化処理(基本的には使用しない)
	public abstract void draw(GL10 gl, float frametime);	// 主描画処理
	public abstract void term(GL10 gl);						// 描画スレッドでの終了処理(基本的には使用しない)

	//=========================================================================
	//
	// drawAlphaNum系
	//
	//=========================================================================
	protected void drawAlphaNum(GL10 gl, String text, int fontSize, int margin, int x, int y)
	{
		this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, text, fontSize, margin, x, y, 100);
	}

	protected void drawAlphaNum(GL10 gl, String text, int fontSize, int margin, int x, int y, int alpha)
	{
		this.sakuraManager.getSakuraDraw().drawAlphaNum(gl, text, fontSize, margin, x, y, alpha);
	}

	//=========================================================================
	//
	// drawTexture系
	//
	//=========================================================================
	protected void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterIndex, isCenter, x, y, 100, width, height);
	}

	protected void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterIndex, isCenter, x, y, alpha, width, height);
	}

	protected void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterIndex, isCenter, x, y, 100);
	}

	protected void drawTexture(GL10 gl, int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterIndex, isCenter, x, y, alpha);
	}

	protected void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int x, int y)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterName, isCenter, x, y, 100);
	}

	protected void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int x, int y, int alpha)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterName, isCenter, x, y, alpha);
	}

	protected void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int x, int y, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterName, isCenter, x, y, 100, width, height);
	}

	protected void drawTexture(GL10 gl, int textureID, String characterName, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().drawTexture(gl, textureID, characterName, isCenter, x, y, alpha, width, height);
	}
	
	//=========================================================================
	//
	// drawButto系
	//
	//=========================================================================
	protected void drawButton(GL10 gl, int buttonIndex,  boolean isCenter, int x, int y)
	{
		this.sakuraManager.getSakuraDraw().drawButton(gl, buttonIndex, isCenter, x, y, 100, -1, -1);
	}
	protected void drawButton(GL10 gl, int buttonIndex,  boolean isCenter, int x, int y, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().drawButton(gl, buttonIndex, isCenter, x, y, 100, width, height);
	}

	//=========================================================================
	//
	// drawParticle系
	//
	//=========================================================================
	protected void drawParticle(GL10 gl, ParticleManager particleManager)
	{
		for (count = 0 ; count < particleManager.getParticles().length ; count++)
		{
			if ((particleManager.getParticles()[count]).isActive() == false){ continue; }
			this.sakuraManager.getSakuraDraw().drawParticle(gl, particleManager.getParticles()[count], this.frametime);
		}
	}

	//=========================================================================
	//
	// drawTexture系
	//
	//=========================================================================
	protected void burstTexture(int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		this.sakuraManager.getSakuraDraw().burstTexture(textureID, characterIndex, isCenter, x, y, alpha, width, height);
	}
	
	protected void burstTextureRenderer(GL10 gl, int textureID)
	{
		this.sakuraManager.getSakuraDraw().burstTextureRenderer(gl, textureID);
	}
}
