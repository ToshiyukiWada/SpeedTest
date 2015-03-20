package jp.futuresoftware.android.sakura.core;

import java.lang.reflect.Constructor;

import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.base.ParticleBase;
import jp.futuresoftware.android.sakura.base.SakuraBase;

public class ParticleManager extends SakuraBase
{
	// メンバ変数定義
	private int count;						// クラス内カウンタ
	private int maxParticleCount;			// 最大パーティクル数
	private ParticleBase[] particles;		// パーティクルクラスの配列
	
	/**
	 * パーティクルマネージャーコンストラクタ
	 * 
	 * @param sakuraManager
	 * @param maxParticleCount
	 * @param partcleClassName
	 * @param textureName
	 */
	@SuppressWarnings("rawtypes")
	public ParticleManager(SakuraManager sakuraManager, int maxParticleCount, String partcleClassName, int textureID)
	{
		this.sakuraManager			= sakuraManager;							// SakuraManager
		this.maxParticleCount		= maxParticleCount;							// 最大パーティクル同時発生数の定義
		this.particles				= new ParticleBase[this.maxParticleCount];	// 最大パーティクル同時発生数分だけパーティクルのエリアを定義しておく
		
		try
		{
			Class<?> classObj			= Class.forName(partcleClassName);
			Constructor constructor		= classObj.getConstructor(SakuraManager.class, Integer.class);
			for (int count = 0 ; count < this.particles.length ; count++ )
			{
				ParticleBase particleObj	= (ParticleBase) constructor.newInstance(this.sakuraManager, textureID);
				particleObj.setActive(false);
				this.particles[count]		= particleObj;
			}
		}
		catch(Exception exception){}
	}
	
	/**
	 * 
	 * 
	 * @param sakuraManager
	 * @param maxParticleCount
	 * @param particleBase
	 */
	public ParticleManager(SakuraManager sakuraManager, int maxParticleCount, ParticleBase particleBase)
	{
		this.sakuraManager			= sakuraManager;							// SakuraManager
		this.maxParticleCount		= maxParticleCount;							// 最大パーティクル同時発生数の定義
		this.particles				= new ParticleBase[this.maxParticleCount];	// 最大パーティクル同時発生数分だけパーティクルのエリアを定義しておく
		
		try
		{
			for (int count = 0 ; count < this.particles.length ; count++ )
			{
				ParticleBase particleObj	= (ParticleBase)particleBase.clone();
				particleObj.setActive(false);
				this.particles[count]		= particleObj;
			}
		}
		catch(Exception exception){}
	}

	/**
	 * パーティクルの追加と処理開始
	 * 
	 * @param particlePositionX
	 * @param particlePositionY
	 */
	public int addParticle(int particlePositionX, int particlePositionY)
	{
		// 戻り値(パーティクルハンドラー)
		int particleHandler			= -1;
		
		// 現在非アクティブのパーティクルを検索する
		ParticleBase particleBase		= null;
		
		// パーティクル全件検索
		for (count = 0 ; count < this.particles.length ; count++)
		{
			// パーティクルが無効になってるかチェック
			if (this.particles[count].isActive() == false)
			{
				// 無効になっているものがあった場合は、それを有効にする
				particleBase		= this.particles[count];
				particleHandler		= count;
				break;
			}
		}
		
		// 非アクティブのパーティクルが存在した場合は
		if (particleBase != null)
		{
			particleBase.start(particlePositionX, particlePositionY);
		}
		
		// パーティクルハンドラー返却
		return particleHandler;
	}
	
	/**
	 * @param particleHandler
	 */
	public void removeParticle(int particleHandler)
	{
		this.particles[particleHandler].stop();
	}
	
	/**
	 * @return
	 */
	public ParticleBase[] getParticles()
	{
		return this.particles;
	}
}
