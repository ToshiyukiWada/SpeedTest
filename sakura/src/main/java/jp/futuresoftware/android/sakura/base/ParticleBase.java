package jp.futuresoftware.android.sakura.base;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jp.futuresoftware.android.sakura.SakuraManager;

/**
 * @author toshiyuki
 *
 */
public abstract class ParticleBase extends SakuraBase implements Cloneable
{
	// メンバ変数の宣言
	private float transitTime;													// このパーティクルの開始からの経過秒(delayを判定したりDurationを求める為)
	private float prevDurationTime;												// 直前のduration秒
	private int count;															// 汎用カウンタ
	private int createCount;													// パーティクルビット生成カウンタ
	private boolean isCreateParticle;
	
	private int textureID;														// このパーティクルで使用するテクスチャ
	private boolean isActive;													// このパーティクルが有効か否か
	private ParticleBitBase[] particleBits;										// パーティクルビットを保持する配列
	private int maxParticleBitCount;											// このパーティクルで使用する最大パーティクル数(パーティクルは１度のみ作成される配列で管理される為、その配列数として使用する)
	private ParticleCheckInactiveInterface checkInactiveParticleFunction;		// パーティクル自身の終了条件関数
	private int particlePositionX;
	private int particlePositionY;
	
	// パーティクル設定
	private float delay;														// 開始から何秒遅らせてパーティクル処理を開始するか
	private float duration;														// 何秒間隔でパーティクルを作成するか？
	private int createParticleBitCount;											// 上記durationの間隔で１回に作成するパーティクルビットの数
	private int reuseParticleBitCount;											// パーティクルビットの再利用数
	private ParticleBitCheckInactiveInterface checkInactiveParticleBitFunction;	// パーティクル内のビットの終了条件関数

	// 内部的に使用するメンバ変数(パーティクル描画関連)
	private ByteBuffer bb;														// ByteBuffer(エンディアン確認用)
	private float[] vertices;													// 頂点座標(最大パーティクル数にあわせた配列として生成される)
	private float[] colors;														// 色座標(最大パーティクル数にあわせた配列として生成される)
	private float[] coords;														// テクスチャ座標(最大パーティクル数にあわせた配列として生成される)
	private FloatBuffer verticesBuffer;											// 頂点座標をOpenGLに転送する形式にしたもの
	private FloatBuffer colorBuffer;											// 色座標をOpenGLに転送する形式にしたもの
	private FloatBuffer coordBuffer;											// テクスチャ座標をOpenGLに転送する形式にしたもの

	/**
	 * コンストラクタ
	 * 
	 * @param sakuraManager
	 * @param textureName
	 * @param characterIndex
	 */
	public ParticleBase(SakuraManager sakuraManager, Integer textureID)
	{
		// SakuraManagerの登録
		this.setSakuraManager(sakuraManager);

		// 利用するテクスチャIDの登録
		this.textureID								= textureID;
		
		// 初期状態は非アクティブとする
		this.isActive								= false;

		// 最大パーティクル数の受け取り
		this.maxParticleBitCount					= this.defineMaxParticleBitCount();
		
		// 最大パーティクルに合わせてメモリを確保する
		this.particleBits							= new ParticleBitBase[this.maxParticleBitCount];
		for (int countParticleBit = 0 ; countParticleBit < this.maxParticleBitCount ; countParticleBit++ )
		{
			this.particleBits[countParticleBit]				= this.createParticleBitBase();
			this.particleBits[countParticleBit].setParticlesIndex(countParticleBit);
		}
		
		// パーティクル生成パターンの取得
		this.delay									= this.defineDelay();
		this.duration								= this.defineDuration();
		this.createParticleBitCount					= this.defineCreateParticleBitCount();
		this.reuseParticleBitCount					= this.defineReuseParticleBitCount();
		
		// パーティクル・パーティクルビットの終了条件ロジックの受け取り(NULLを受け取っても良い)
		this.checkInactiveParticleBitFunction		= this.defineParticleBitCheckInactiveInterface();
		this.checkInactiveParticleFunction			= this.defineParticleCheckInactiveInterface();

		// 内部初期化処理を実施する
		this.innerInit(this.maxParticleBitCount);
	}

	/**
	 * 初期化処理
	 */
	public void start(int particlePositionX, int particlePositionY)
	{
		this.isActive			= false;
		
		this.particlePositionX	= particlePositionX;
		this.particlePositionY	= particlePositionY;
		
		for (int countParticleBit = 0 ; countParticleBit < this.maxParticleBitCount ; countParticleBit++ )
		{
			this.particleBits[countParticleBit].init(particlePositionX, particlePositionY);
			this.particleBits[countParticleBit].setActive(false);
			this.particleBits[countParticleBit].initReuseCount();
		}

		this.transitTime		= 0;
		this.prevDurationTime	= 0.0f;
		this.isActive			= true;
	}
	
	/**
	 * 
	 */
	public void stop()
	{
		this.isActive		= false;
		this.transitTime	= -1l;
	}

	/**
	 * 
	 */
	public void animation(float frametime)
	{
		if (this.isActive == true)
		{
			// 経過時間をインクリメントしていく
			this.transitTime	+= frametime;
			
			// パーティクル生成チェック
			isCreateParticle = false;
			if (this.prevDurationTime == 0.0f)
			{
				// delayチェック
				if (this.transitTime >= this.delay){ isCreateParticle = true; }
			}
			else
			{
				// durationチェック
				if ((this.prevDurationTime + this.duration) <= this.transitTime){ isCreateParticle = true; }
			}
			
			// パーティクル生成処理
			if (isCreateParticle)
			{
				this.prevDurationTime = this.transitTime;
				
				// パーティクルの作成開始
				createCount = 0;
				for (count = 0 ; count < this.particleBits.length ; count++)
				{
					// 非アクティブのパーティクルビットを見つけた場合は、createParticleBitCountの数だけ有効にする
					if (this.particleBits[count].isActive() == false)
					{
						if (this.reuseParticleBitCount != -1)
						{
							// アクティブ化しようとしているパーティクルビットの再利用回数が上限を超えていないことを確認する
							if (this.particleBits[count].getReuseCount() < this.reuseParticleBitCount)
							{
								// 上限を超えていないパーティクルビットを有効化する(有効化と同時にアニメーション処理が走るようになる)
								this.particleBits[count].init(this.particlePositionX, this.particlePositionY);
								this.particleBits[count].isActive = true;
								this.particleBits[count].addReuseCount();		// 有効化したので、再利用カウンタをインクリメントする
								createCount++;
							}
						}
						else
						{
							// 無限に作る
							this.particleBits[count].init(this.particlePositionX, this.particlePositionY);
							this.particleBits[count].isActive = true;
							createCount++;
						}
					}
					
					// 規定数のパーティクルを作り終えた場合はそこでループを終了する
					if (createCount >= createParticleBitCount){ break; }
				}
			}
		}
	}
	
	//-------------------------------------------------------------------------
	// 仮想メソッドの定義
	//-------------------------------------------------------------------------
	public abstract int defineMaxParticleBitCount();													// 最大パーティクルビット数の定義
	public abstract float defineDelay();																// パーティクル表示遅延秒の設定
	public abstract float defineDuration();																// パーティクル生成間隔秒の設定
	public abstract int defineCreateParticleBitCount();													// 一度のdurationで作成するパーティクルビット数の設定
	public abstract int defineReuseParticleBitCount();													// パーティクルビットを再利用する回数のせってい　
	public abstract ParticleBitBase						createParticleBitBase();						// パーティクルビットインスタンス返却処理の定義
	public abstract ParticleBitCheckInactiveInterface	defineParticleBitCheckInactiveInterface();		// パーティクルビット終了条件処理の定義
	public abstract ParticleCheckInactiveInterface		defineParticleCheckInactiveInterface();			// パーティクル終了条件処理の定義
	
	/**
	 * lifetimeが指定されていない場合のパーティクル粒子の終了条件関数を実行する
	 * 
	 * @param particleBit
	 */
	public void checkStopParticleBit(ParticleBitBase particleBitBase)
	{
		// ライフタイムが設定されていない場合は、パーティクルの終了条件チェックが定義されているか否かをチェックする
		if (this.checkInactiveParticleBitFunction != null)
		{
			// パーティクル終了条件チェックを実施し、その結果終了条件に合致した場合はパーティクルを非アクティブにする
			if (this.checkInactiveParticleBitFunction.checkInactive(particleBitBase) == true)
			{
				particleBitBase.setActive(false);
			}
		}
	}
	
	/**
	 * パーティクル自身の終了条件関数を実行する
	 * 
	 * @param particleBits
	 */
	public void checkStopParticle(ParticleBitBase[] particleBitBases)
	{
		if (this.checkInactiveParticleFunction != null)
		{
			if (this.checkInactiveParticleFunction.checkInactive(particleBitBases) == true)
			{
				// ここにパーティクルの終了処理を記述
				this.isActive = false;
			}
		}
	}

	/**
	 * 初期化処理
	 * ここでは、最大パーティクル数を受けとり、必要な配列を作成する
	 * 
	 * @param maxParticleCount
	 */
	private void innerInit(int maxParticleCount)
	{
		this.maxParticleBitCount	= maxParticleCount;
		this.vertices				= new float[6 * 2 * this.maxParticleBitCount];
		this.colors					= new float[6 * 4 * this.maxParticleBitCount];
		this.coords					= new float[6 * 2 * this.maxParticleBitCount];
		
		this.bb						= ByteBuffer.allocateDirect(this.vertices.length * 4);
		this.bb.order(ByteOrder.nativeOrder());
		this.verticesBuffer			= bb.asFloatBuffer();
		this.verticesBuffer.position(0);
		
		this.bb						= ByteBuffer.allocateDirect(this.colors.length * 4);
		this.bb.order(ByteOrder.nativeOrder());
		this.colorBuffer			= bb.asFloatBuffer();
		this.colorBuffer.position(0);
		
		this.bb						= ByteBuffer.allocateDirect(this.coords.length * 4);
		this.bb.order(ByteOrder.nativeOrder());
		this.coordBuffer			= bb.asFloatBuffer();
		this.coordBuffer.position(0);
	}
	
	public ParticleBitBase[] getParticleBits() {
		return particleBits;
	}

	public int getTextureID() {
		return textureID;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getColors() {
		return colors;
	}

	public float[] getCoords() {
		return coords;
	}
	
	public FloatBuffer getVerticesBuffer() {
		verticesBuffer.clear();
		verticesBuffer.put(this.vertices);
		verticesBuffer.position(0);
		return verticesBuffer;
	}

	public FloatBuffer getColorBuffer() {
		colorBuffer.clear();
		colorBuffer.put(this.colors);
		colorBuffer.position(0);
		return colorBuffer;
	}

	public FloatBuffer getCoordBuffer() {
		coordBuffer.clear();
		coordBuffer.put(this.coords);
		coordBuffer.position(0);
		return coordBuffer;
	}

	@Override
	public Object clone() {	//throwsを無くす
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	/**
	 * パーティクルクラス
	 * 
	 * @author toshiyuki
	 *
	 */
	public abstract class ParticleBitBase
	{
		protected int characterIndex;			// テクスチャ画像内のキャラクターインデックス
		protected float x;						// レンダリング座標
		protected float y;						// レンダリング座標
		protected float size;					// パーティクルサイズ
		protected float alpha;					// パーティクル透明度
		protected float lifetime;				// パーティクルの寿命
		protected int particlesIndex;			// パーティクル配列の何番目に定義されているか
		
		private boolean isActive;				// 生存しているか否か
		private long createMillis;				// パーティクル作成ミリ秒
		private int reuseCount;					// (再)利用回数

		/**
		 * パーティクルの初期位置の設定
		 * (メンバ変数の初期値をコントロールすれば良い)
		 */
		public abstract void init(int particlePositionX, int particlePositionY);
		
		/**
		 * パーティクルの動きを定義する仮想メソッド
		 * 
		 * @param frametime
		 */
		public abstract void animation(float frametime);

		/**
		 * @return
		 */
		public int getCharacterIndex() {
			return characterIndex;
		}
		/**
		 * @return
		 */
		public float getX() {
			return x;
		}
		/**
		 * @return
		 */
		public float getY() {
			return y;
		}
		/**
		 * @return
		 */
		public float getSize() {
			return size;
		}
		/**
		 * @return
		 */
		public float getAlpha() {
			return alpha;
		}
		/**
		 * @return
		 */
		public boolean isActive() {
			return isActive;
		}
		/**
		 * @return
		 */
		public long getCreateMillis() {
			return createMillis;
		}

		/**
		 * @param frametime
		 */
		public void subLifetime(float frametime)
		{
			this.lifetime -= frametime;
			if (this.lifetime <= 0){ this.isActive = false; }
		}
		
		/**
		 * 
		 */
		public void initReuseCount()
		{
			this.reuseCount = 0;
		}
		
		/**
		 * 
		 */
		public void addReuseCount()
		{
			this.reuseCount++;
		}
		
		/**
		 * @return
		 */
		public int getReuseCount()
		{
			return (this.reuseCount);
		}
		
		/**
		 * @param isActive
		 */
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}

		/**
		 * @param particlesIndex
		 */
		public void setParticlesIndex(int particlesIndex) {
			this.particlesIndex = particlesIndex;
		}
	}
}
