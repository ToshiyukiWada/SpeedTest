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

	private int buttonIndex;

	// 状況フラグ
	private int width;										// 現在ボタンがレンダリングされている座標(幅)
	private int height;										// 現在ボタンがレンダリングされている座標(高さ)

	private int x;											// 現在ボタンがレンダリングされている座標(X)
	private int y;											// 現在ボタンがレンダリングされている座標(Y)
	private int x2;											// 現在ボタンがレンダリングされている座標(Xの終端)
	private int y2;											// 現在ボタンがレンダリングされている座標(Yの終端)

	private int nowX;										// 現在のタッチ座標(ドラッグ中を考慮)
	private int nowY;										// 現在のタッチ座標(ドラッグ中を考慮)
	private boolean isTouchAreaOut;

	private int touchStartX;								// タッチ開始座標
	private int touchStartY;								// タッチ開始座標

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
	public TextureButtonInformation(int textureID, int normalCharacterIndex, int touchCharacterIndex, int disableCharacterIndex, SceneButtonProcessBase sceneButtonProcessBase, int width, int height, int buttonIndex)
	{
		// Indexの保持
		this.textureID					= textureID;					// テクスチャID
		this.normalCharacterIndex		= normalCharacterIndex;			// 通常時ボタンのテクスチャIndex
		this.touchCharacterIndex		= touchCharacterIndex;			// タッチ中ボタンのテクスチャIndex
		this.disableCharacterIndex		= disableCharacterIndex;		// 無効時ボタンのテクスチャIndex
		this.sceneButtonProcessBase		= sceneButtonProcessBase;		// ボタンのイベントが記述されたメソッドを集めたクラス

		this.buttonIndex				= buttonIndex;

		this.width						= width;						// XMLで定義済みのサイズ
		this.height						= height;						// XMLで定義済みのサイズ

		// 座標系情報のクリア
		this.x							= -1;							// ボタン開始座標X
		this.y							= -1;							// ボタン開始座標Y
		this.x2							= -1;							// ボタン終了座標X
		this.y2							= -1;							// ボタン終了座標Y

		this.nowX						= -1;
		this.nowY						= -1;
		this.isTouchAreaOut				= true;							//

		this.touchStartX				= -1;
		this.touchStartY				= -1;

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
	public void setButtonPosition(int x, int y, int width, int height)
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
	 * 状況に適したボタン画像のキャラクターINDEXを返却する
	 *
	 * @return
	 */
	public int getNowCharacterIndex()
	{
		if (this.isDisabled == true){ return this.disableCharacterIndex; }		// ボタンが無効化されている場合は、無効化ボタンイメージを返却
		if (this.isTouchAreaOut == false){ return this.touchCharacterIndex; }	// タッチボタンイメージを返却
		return this.normalCharacterIndex;										// 通常ボタンイメージを返却
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onDown(int x, int y)
	{
		this.nowX	= this.touchStartX	= x;
		this.nowY	= this.touchStartY	= y;
		this.isTouchAreaOut				= false;
		return this.sceneButtonProcessBase.onDown(this.buttonIndex);
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void setNow(int x, int y)
	{
		this.nowX		= x;
		this.nowY		= y;

		if (this.isTouchAreaOut == false)
		{
			if (this.touchStartX - 20 < this.nowX && this.nowX <= this.touchStartX + 20 && this.touchStartY - 20 < this.nowY && this.nowY <= this.touchStartY + 20)	{
				// タッチ判定有効エリア内
			} else {
				// タッチ判定有効エリア外に出てしまった
				this.isTouchAreaOut = true;
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isTouchAreaOut()
	{
		return this.isTouchAreaOut;
	}

	/**
	 *
	 * @return
	 */
	public boolean onUp()
	{
		// 開始位置
		this.isTouchAreaOut = true;
		this.touchStartX	= -1;
		this.touchStartY	= -1;
		return this.sceneButtonProcessBase.onUp(this.buttonIndex);
	}

	/**
	 *
	 * @return
	 */
	public boolean onTouch()
	{
		this.isTouchAreaOut = true;
		this.touchStartX	= -1;
		this.touchStartY	= -1;
		return this.sceneButtonProcessBase.onTouch(this.buttonIndex);
	}

	/**
	 *
	 * @return
	 */
	public int getTouchStartX() {
		return this.touchStartX;
	}

	/**
	 *
	 * @return
	 */
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
