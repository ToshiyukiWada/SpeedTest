package jp.futuresoftware.android.sakura.information;

import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/03/31.
 */
public class TextureButtonInformation implements Cloneable {

	// メンバ変数定義
	private int textureID;									// テクスチャID
	private int normalCharacterIndex;						// 通常時ボタンのテクスチャIndex
	private int touchCharacterIndex;						// タッチ中ボタンのテクスチャIndex
	private int disableCharacterIndex;						// 無効時ボタンのテクスチャIndex
	private SceneButtonProcessBase sceneButtonProcessBase;	// ボタンのイベントが記述されたメソッドを集めたクラス

	// 状況フラグ
	private int width;										// 現在ボタンがレンダリングされている座標(幅)
	private int height;										// 現在ボタンがレンダリングされている座標(高さ)

	private int x;											// 現在ボタンがレンダリングされている座標(X)
	private int y;											// 現在ボタンがレンダリングされている座標(Y)
	private int x2;											// 現在ボタンがレンダリングされている座標(Xの終端)
	private int y2;											// 現在ボタンがレンダリングされている座標(Yの終端)

	private int touchStartX;								// タッチ開始座標
	private int touchStartY;								// タッチ開始座標

	private int pointer;									// タッチ中のポインタ(マルチタッチ判定用)
	private boolean isDisabled;								// そのボタンが無効中か否か

	//=====================================================================
	// Clone有効化
	//=====================================================================
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 *
	 * @param textureID
	 * @param normalCharacterIndex
	 * @param disableCharacterIndex
	 * @param touchCharacterIndex
	 */
	public TextureButtonInformation(int textureID, int normalCharacterIndex, int touchCharacterIndex, int disableCharacterIndex, SceneButtonProcessBase sceneButtonProcessBase, int width, int height)
	{
		// Indexの保持
		this.textureID					= textureID;					// テクスチャID
		this.normalCharacterIndex		= normalCharacterIndex;			// 通常時ボタンのテクスチャIndex
		this.touchCharacterIndex		= touchCharacterIndex;			// タッチ中ボタンのテクスチャIndex
		this.disableCharacterIndex		= disableCharacterIndex;		// 無効時ボタンのテクスチャIndex
		this.sceneButtonProcessBase		= sceneButtonProcessBase;		// ボタンのイベントが記述されたメソッドを集めたクラス

		this.width						= width;						// XMLで定義済みのサイズ
		this.height						= height;						// XMLで定義済みのサイズ

		// 座標系情報のクリア
		this.x							= -1;							// ボタン開始座標X
		this.y							= -1;							// ボタン開始座標Y
		this.x2							= -1;							// ボタン終了座標X
		this.y2							= -1;							// ボタン終了座標Y

		this.touchStartX				= -1;
		this.touchStartY				= -1;

		this.pointer					= -1;
		this.isDisabled					= false;
	}

	/**
	 * @return
	 */
	public int getTextureID() {
		return textureID;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setPosition(int x, int y, int width, int height)
	{
		this.x		= x;
		this.y		= y;
		this.width	= width;
		this.height	= height;
		this.x2		= x + width;
		this.y2		= y + height;
	}

	public void setDisabled(boolean isDisabled){ this.isDisabled = isDisabled; }

	/**
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return
	 */
	public int getY2() {
		return y2;
	}

	/**
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return
	 */
	public int getNowCharacterIndex()
	{
		if (this.isDisabled == true){ return this.disableCharacterIndex; }
		if (this.pointer != -1){ return this.touchCharacterIndex; }
		return this.normalCharacterIndex;
	}


	public boolean onDown(int pointer, int x, int y)
	{
		this.pointer		= pointer;
		this.touchStartX	= x;
		this.touchStartY	= y;
		return this.sceneButtonProcessBase.onDown();
	}
	public boolean onUp()
	{
		// 開始位置
		this.pointer		= -1;
		this.touchStartX	= -1;
		this.touchStartY	= -1;
		return this.sceneButtonProcessBase.onUp();
	}

	public boolean onTouch()
	{
		this.pointer		= -1;
		this.touchStartX	= -1;
		this.touchStartY	= -1;
		return this.sceneButtonProcessBase.onTouch();
	}

	public int getPointer(){
		return this.pointer;
	}

	public int getTouchStartX() {
		return this.touchStartX;
	}

	public int getTouchStartY()	{
		return this.touchStartY;
	}

	/**
	 *
	 */
	public void clear()
	{
		this.sceneButtonProcessBase		= null;
	}
}
