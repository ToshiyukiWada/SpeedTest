package jp.futuresoftware.android.sakura.texture;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import jp.futuresoftware.android.sakura.base.EventInterface;
import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.common.RecursiveNode;

/**
 * @author toshiyuki
 *
 */
public class TextureManager
{
	/**
	 * リフレクションを使った、int配列を使ったメンバ変数へのTextureIndexの格納
	 * 
	 * @param sceneBase
	 * @param textureName
	 * @param textureIndexMemberName
	 * @param es
	 */
	public static void characterName2Index(SceneBase sceneBase, int textureID, String textureIndexMemberName, EnumSet<?> es)
	{
		SceneProcessBase sceneProcessBase		= sceneBase.getSceneProcess();							// 対象のプロセスを取得
		Object value							= (Object)new int[es.size()];							// Indexを格納するエリアのインスタンスを生成
		Class<?> targetClass					= null;													// SceneBaseかSceneProcessBaseを検索するので、そのどちらに該当したメンバが存在していたかを格納する
		Object targetBase						= null;
		
		try
		{
			sceneProcessBase.getClass().getField(textureIndexMemberName);
			targetClass							= sceneProcessBase.getClass();
			targetBase							= (Object)sceneProcessBase;
		}
		catch(NoSuchFieldException nsfe)
		{
			targetClass							= null;
			targetBase							= null;

			try
			{
				sceneBase.getClass().getField(textureIndexMemberName);
				targetClass						= sceneBase.getClass();
				targetBase						= (Object)sceneBase;
			}
			catch(NoSuchFieldException nsfe2)
			{
				targetClass						= null;
				targetBase						= null;
			}
		}
			
		if (targetClass != null && targetBase != null)
		{
			try
			{
				targetClass.getField(textureIndexMemberName).set(targetBase, value);	// 指定されたメンバ変数に格納する
				int count = 0;																					// カウンターの準備
				for (Enum<?> e : es)
				{
					// Enumの名称をキーとして、対象テクスチャの名前に紐付くIndexを取得し、上記で作成したばかりのint配列に格納していく
					((int [])targetClass.getField(textureIndexMemberName).get(targetBase))[count] = sceneBase.getSakuraManager().getTextures().get(textureID).getCharacterIndex(e.name());
					count++;
				}
			}
			catch (Exception exp){}
		}
	}
	
	// メンバ変数
	private int glTextureID;							// OpenGLと結び付けられたテクスチャID
	private int textureBitmapID;						// リソースのテクスチャ画像ID
	private int characterXmlStreamID;					// リソースのテクスチャキャラクタ定義XMLID
	private int textureBitmapWidth;						//　テクスチャ元画像の幅(高さと一緒なはず)
	private int textureBitmapHeight;					// テクスチャ元画像の高さ(幅と一緒なはず)
	private ArrayList<TextureCharacter> characters;		// このテクスチャに定義されているキャラクター一覧(すばやくアクセスできる為に配列に格納)
	private Map<String, Integer> charactersMap;			// 上記配列に対してキャラクター名で問合せできるように連想配列でインデックスを保持する
	private ArrayList<TextureButton> buttons;			// このテクスチャに定義されているキャラクターボタン一覧(すばやくアクセスできる為に配列に格納)
	private Map<String, Integer> buttonsMap;			// 上記配列に対してキャラクターボタン名で問合せできるように連想配列でインデックスを保持する
	
	private int maxBurstSetCount;						// 高速描画で保持できる描画指示の最大数
	private int burstInformationCount;					//　現在格納中の高速描画のインデックス 
	private BurstInformation[] burstInformations;		// 高速描画の座標等の情報を保持しておく配列
	private ByteBuffer bb;								// ByteBuffer(エンディアン確認用)
	private float[] vertices;							// 頂点座標(最大パーティクル数にあわせた配列として生成される)
	private float[] colors;								// 色座標(最大パーティクル数にあわせた配列として生成される)
	private float[] coords;								// テクスチャ座標(最大パーティクル数にあわせた配列として生成される)
	private FloatBuffer verticesBuffer;					// 頂点座標をOpenGLに転送する形式にしたもの
	private FloatBuffer colorBuffer;					// 色座標をOpenGLに転送する形式にしたもの
	private FloatBuffer coordBuffer;					// テクスチャ座標をOpenGLに転送する形式にしたもの

	/**
	 * コンストラクタ
	 * 
	 * @param textureName
	 * @param textureID
	 * @param textureBitmap
	 * @param characterXmlStream
	 */
	public TextureManager(int glTextureID, int textureBitmapID, Bitmap textureBitmap, int characterXmlStreamID, InputStream characterXmlStream)
	{
		constructor(glTextureID, textureBitmapID, textureBitmap.getWidth(), textureBitmap.getHeight(), characterXmlStreamID, characterXmlStream);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param textureName
	 * @param textureID
	 */
	public TextureManager(int glTextureID, int textureBitmapID, int textureBitmapWidth, int textureBitmapHeight, int characterXmlStreamID, InputStream characterXmlStream)
	{
		constructor(glTextureID, textureBitmapID, textureBitmapWidth, textureBitmapHeight, characterXmlStreamID, characterXmlStream);
	}

	/**
	 * コンストラクタ(共通)
	 * 
	 * @param textureName
	 * @param textureID
	 * @param textureBitmapWidth
	 * @param textureBitmapHeight
	 * @param characterXmlStream
	 */
	private void constructor(int glTextureID, int textureBitmapID, int textureBitmapWidth, int textureBitmapHeight, int characterXmlStreamID, InputStream characterXmlStream)
	{
		// メンバ変数初期化
		this.glTextureID			= glTextureID;
		this.textureBitmapID		= textureBitmapID;
		this.characterXmlStreamID	= characterXmlStreamID;
		this.textureBitmapWidth		= textureBitmapWidth;
		this.textureBitmapHeight	= textureBitmapHeight;
		this.characters				= new ArrayList<TextureCharacter>();
		this.charactersMap			= new HashMap<String, Integer>();
		this.buttons				= new ArrayList<TextureButton>();
		this.buttonsMap				= new HashMap<String, Integer>();
		this.maxBurstSetCount		= 0;

		// キャラクター情報を生成する
		if (characterXmlStream != null)
		{
			RecursiveNode characterXml				= null;
			try
			{
				characterXml			= RecursiveNode.parse(characterXmlStream);
				RecursiveNode rootNode	= characterXml.n("texture");
				for (int count = 0 ; count < rootNode.count("char") ; count++)
				{
					RecursiveNode charNode		= rootNode.n("char", count);
					String name					= charNode.getAttributes().getNamedItem("name").getTextContent();
					String x					= charNode.getAttributes().getNamedItem("x").getTextContent();
					String y					= charNode.getAttributes().getNamedItem("y").getTextContent();
					String w					= charNode.getAttributes().getNamedItem("w").getTextContent();
					String h					= charNode.getAttributes().getNamedItem("h").getTextContent();
					
					try
					{
						this.addCharacter(name, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(w), Integer.parseInt(h));
					}
					catch(NumberFormatException nfe){}
				}
				if (rootNode.count("buttons") == 1)
				{
					RecursiveNode buttonsNode	= rootNode.n("buttons", 0);
					for (int count = 0 ; count < buttonsNode.count("button") ; count++)
					{
						RecursiveNode buttonNode	= buttonsNode.n("button", count);
						String name					= buttonNode.getAttributes().getNamedItem("name").getTextContent();
						
						String normalChar			= "";
						String disableChar			= "";
						String touchChar			= "";
						try { normalChar = buttonNode.getAttributes().getNamedItem("normal").getTextContent(); } catch(Exception exp){ normalChar = ""; } 
						try { disableChar = buttonNode.getAttributes().getNamedItem("disable").getTextContent(); } catch(Exception exp){ disableChar = ""; }
						try { touchChar = buttonNode.getAttributes().getNamedItem("touch").getTextContent(); } catch(Exception exp){ touchChar = ""; }
						
						addButton(name, normalChar, disableChar, touchChar);
					}
				}
	
			}
			catch(Exception exp){}
		}
		
		// 高速描画情報を配列0で作成しておく
		this.burstInformations		= new BurstInformation[this.maxBurstSetCount];
	}
	
	public void setMaxBurstSetCount(int maxBurstSetCount)
	{
		// メンバ変数に値を格納する
		this.maxBurstSetCount		= maxBurstSetCount;

		// Burst用領域の作成
		if (this.maxBurstSetCount == -1){ this.maxBurstSetCount = 0; } 
		this.burstInformations		= new BurstInformation[this.maxBurstSetCount];
		for (int count = 0 ; count < this.maxBurstSetCount ; count++)
		{
			this.burstInformations[count]		= new BurstInformation();
		}
		
		this.vertices				= new float[6 * 2 * this.maxBurstSetCount];
		this.colors					= new float[6 * 4 * this.maxBurstSetCount];
		this.coords					= new float[6 * 2 * this.maxBurstSetCount];
		
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
	
	/**
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void addCharacter(String name, int x, int y, int width, int height)
	{
		TextureCharacter textureCharacter		= new TextureCharacter(this, x, y, width, height);
		this.characters.add(textureCharacter);
		this.charactersMap.put(name, this.characters.size() - 1);
	}

	/**
	 * @param name
	 * @param normalCharacterName
	 * @param disableCharacterName
	 * @param touchCharacterName
	 */
	public void addButton(String name, String normalCharacterName, String disableCharacterName, String touchCharacterName)
	{
		TextureButton textureButton				= new TextureButton(this, normalCharacterName, disableCharacterName, touchCharacterName);
		this.buttons.add(textureButton);
		this.buttonsMap.put(name, this.buttons.size() - 1);
	}

	/**
	 * @return
	 */
	public int getGLTextureID()
	{
		return this.glTextureID;
	}
	
	/**
	 * @return
	 */
	public int getTextureBitmapID() {
		return textureBitmapID;
	}

	/**
	 * @return
	 */
	public int getCharacterXmlStreamID() {
		return characterXmlStreamID;
	}

	/**
	 * @param index
	 * @return
	 */
	public TextureCharacter getCharacter(int index)
	{
		if (index == -1){ return null; }
		return this.characters.get(index);
	}
	
	/**
	 * @param characterName
	 * @return
	 */
	public TextureCharacter getCharacter(String characterName)
	{
		if ((this.charactersMap.get(characterName) == null?-1:this.charactersMap.get(characterName).intValue()) == -1){ return null; }
		return this.characters.get(this.charactersMap.get(characterName));
	}
	
	/**
	 * @param characterName
	 * @return
	 */
	public int getCharacterIndex(String characterName)
	{
		return (this.charactersMap.get(characterName) == null?-1:this.charactersMap.get(characterName).intValue());
	}

	/**
	 * @param index
	 * @return
	 */
	public TextureButton getButton(int index)
	{
		if (index == -1){ return null; }
		return this.buttons.get(index);
	}

	/**
	 * @param buttonName
	 * @return
	 */
	public TextureButton getButton(String buttonName)
	{
		if ((this.buttonsMap.get(buttonName) == null?-1:this.buttonsMap.get(buttonName).intValue()) == -1){ return null; }
		return this.buttons.get(this.buttonsMap.get(buttonName));
	}
	
	/**
	 * @param buttonName
	 * @return
	 */
	public int getButtonIndex(String buttonName)
	{
		return (this.buttonsMap.get(buttonName) == null?-1:this.buttonsMap.get(buttonName).intValue());
	}
	
	public ArrayList<TextureButton> getButtons() {
		return buttons;
	}

	/**
	 * @return
	 */
	public int getTextureBitmapWidth() {
		return textureBitmapWidth;
	}

	/**
	 * @return
	 */
	public int getTextureBitmapHeight() {
		return textureBitmapHeight;
	}

	/**
	 * 
	 */
	public void clearBurstInformation()
	{
		for(int count = 0 ; count < this.maxBurstSetCount ; count++)
		{
			this.burstInformations[count].clear();
		}
		this.burstInformationCount		= 0;
	}
	
	/**
	 * @param burstInformation
	 */
	public void addBurstInformation(int textureID, int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
	{
		try
		{
			this.burstInformations[this.burstInformationCount].set(characterIndex, isCenter, x, y, alpha, width, height);
			this.burstInformationCount++;
		}
		catch(Exception exp){}
	}

	/**
	 * @return
	 */
	public BurstInformation[] getBurstInformations() {
		return burstInformations;
	}

	/**
	 * @return
	 */
	public float[] getVertices() {
		return vertices;
	}

	/**
	 * @return
	 */
	public float[] getColors() {
		return colors;
	}

	/**
	 * @return
	 */
	public float[] getCoords() {
		return coords;
	}
	
	/**
	 * @return
	 */
	public FloatBuffer getVerticesBuffer() {
		verticesBuffer.clear();
		verticesBuffer.put(this.vertices);
		verticesBuffer.position(0);
		return verticesBuffer;
	}

	/**
	 * @return
	 */
	public FloatBuffer getColorBuffer() {
		colorBuffer.clear();
		colorBuffer.put(this.colors);
		colorBuffer.position(0);
		return colorBuffer;
	}

	/**
	 * @return
	 */
	public FloatBuffer getCoordBuffer() {
		coordBuffer.clear();
		coordBuffer.put(this.coords);
		coordBuffer.position(0);
		return coordBuffer;
	}

	/**
	 * @author toshiyuki
	 *
	 */
	public class TextureCharacter
	{
		@SuppressWarnings("unused")
		private TextureManager textureManager;
		@SuppressWarnings("unused")
		private int x, y, width, height;		// 座標情報
		private float[] uv;						// テクスチャ貼り付け情報
		private boolean isUseVBO;				// VBOを使って描画するか否か
		private int vboID;						// VBOを使っている場合GPUに登録したID
		
		public TextureCharacter(TextureManager textureManager, int x, int y, int width, int height)
		{
			// メンバ変数に基本値を格納
			this.textureManager	= textureManager;			// 親インスタンス
			this.x				= x;						// キャラクター座標
			this.y				= y;						// キャラクター座標
			this.width			= width;					// キャラクターの幅
			this.height			= height;					// キャラクターの高さ
			this.isUseVBO		= true;						// VBOを使ったレンダリングをするか否か
			this.vboID			= -1;
			
			// UVの作成
			this.uv				= new float[8];
			float widthRate		= 1.0f / (float)textureManager.getTextureBitmapWidth();
			float heightRate	= 1.0f / (float)textureManager.getTextureBitmapHeight();
			this.uv[0]	= x * widthRate;						this.uv[1]	= y * heightRate;
			this.uv[2]	= x * widthRate;						this.uv[3]	= y * heightRate + height * heightRate;
			this.uv[4]	= x * widthRate + width * widthRate;	this.uv[5]	= y * heightRate;
			this.uv[6]	= x * widthRate + width * widthRate;	this.uv[7]	= y * heightRate + height * heightRate;
		}

		public float[] getUv() {
			return uv;
		}

		public boolean isUseVBO() {
			return isUseVBO;
		}

		public void setUseVBO(boolean isUseVBO) {
			this.isUseVBO = isUseVBO;
		}

		public int getVboID() {
			return vboID;
		}

		public void setVboID(int vboID) {
			this.vboID = vboID;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
	
	/**
	 * テクスチャを利用したボタンの表示で利用するボタン１つの情報
	 * 
	 * @author toshiyuki
	 *
	 */
	public class TextureButton implements Cloneable
	{
		// メンバ変数定義
		private int normalCharacterIndex;		// 通常時ボタンのテクスチャIndex
		private int disableCharacterIndex;		// 無効時ボタンのテクスチャIndex
		private int touchCharacterIndex;		// タッチ中ボタンのテクスチャIndex

		// イベント処理
		private boolean isDownSignalThrow;		// ダウン時の初期をProcessにも伝達するか否か
		private EventInterface downEvent;		// ダウン時の処理内容
		
		private boolean isUpSignalThrow;		// アップ時の初期をProcessにも伝達するか否か
		private EventInterface upEvent;			// アップ時の処理内容
		
		private boolean isTouchSignalThrow;		// タッチ(ダウン後に同じボタンエリアでアップ)時の初期をProcessにも伝達するか否か
		private EventInterface touchEvent;		// タッチ(ダウン後に同じボタンエリアでアップ)時の処理内容

		// 状況フラグ
		private int x;							// 現在ボタンがレンダリングされている座標(X)
		private int y;							// 現在ボタンがレンダリングされている座標(Y)
		private int width;						// 現在ボタンがレンダリングされている座標(幅)
		private int height;						// 現在ボタンがレンダリングされている座標(高さ)
		private int x2;							// 現在ボタンがレンダリングされている座標(Xの終端)
		private int y2;							// 現在ボタンがレンダリングされている座標(Yの終端)
		private int nowCharacterIndex;			// 現在の状況に合わせたボタン状況のインデックス(TextureNameとこれをDrawTextureで表示すれば現在のボタンの表示が行える)

		private int nowPressPointer;			// タッチ中のポインタ(マルチタッチ判定用)
		
		//=====================================================================
		// Clone有効化
		//=====================================================================
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
		
		/**
		 * コンストラクタ
		 * 
		 * @param textureManager
		 * @param normalCharacterName
		 * @param disableCharacterName
		 * @param touchCharacterName
		 */
		public TextureButton(TextureManager textureManager, String normalCharacterName, String disableCharacterName, String touchCharacterName)
		{
			// Indexに変換する
			this.normalCharacterIndex		= textureManager.getCharacterIndex(normalCharacterName);		// 通常時ボタンのテクスチャIndex
			this.disableCharacterIndex		= textureManager.getCharacterIndex(disableCharacterName);		// 無効時ボタンのテクスチャIndex
			this.touchCharacterIndex		= textureManager.getCharacterIndex(touchCharacterName);			// タッチ中ボタンのテクスチャIndex
			
			// イベント内容等をクリアする
			this.clear();
			
			// 座標系情報のクリア
			this.x							= -1;
			this.y							= -1;
			this.width						= -1;
			this.height						= -1;
			this.x2							= -1;
			this.y2							= -1;
			this.nowCharacterIndex			= this.normalCharacterIndex;
		}

		/**
		 * @return
		 */
		public int getNormalCharacterIndex() {
			return normalCharacterIndex;
		}

		/**
		 * @return
		 */
		public int getDisableCharacterIndex() {
			return disableCharacterIndex;
		}

		/**
		 * @return
		 */
		public int getTouchCharacterIndex() {
			return touchCharacterIndex;
		}
		
		/**
		 * @param isDownSignalThrow
		 * @param downEvent
		 */
		public void registDownEvent(boolean isDownSignalThrow, EventInterface downEvent)
		{
			this.isDownSignalThrow		= isDownSignalThrow;
			this.downEvent				= downEvent;
		}
	
		/**
		 * @param isUpSignalThrow
		 * @param upEvent
		 */
		public void registUpEvent(boolean isUpSignalThrow, EventInterface upEvent)
		{
			this.isUpSignalThrow		= isUpSignalThrow;
			this.upEvent				= upEvent;
		}
		
		/**
		 * @param isTouchSignalThrow
		 * @param touchEvent
		 */
		public void registTouchEvent(boolean isTouchSignalThrow, EventInterface touchEvent)
		{
			this.isTouchSignalThrow		= isTouchSignalThrow;
			this.touchEvent				= touchEvent;
		}
		
		/**
		 * @param pointer
		 */
		public void onDown(int pointer)
		{
			// 既に異なる指でタッチされている場合は無視する
			if (this.nowPressPointer != -1){ return; }
			
			// タッチしている指を保存してイベントをキックする
			this.restraintDown(pointer);
			if(this.downEvent != null){ this.downEvent.event(); }
		}
		
		/**
		 * @param pointer
		 */
		public void onUp(int pointer, boolean isTouch)
		{
			// タッチしている指を解除する
			if (isTouch){ this.onTouch(); }
			this.releaseDown();
			this.nowPressPointer		= -1;
			if (this.upEvent != null){ this.upEvent.event(); }
		}
		
		/**
		 * 
		 */
		public void onTouch()
		{
			if (this.touchEvent != null){ this.touchEvent.event(); }
		}

		/**
		 * @param pointer
		 */
		public void restraintDown(int pointer)
		{
			this.nowCharacterIndex		= this.touchCharacterIndex;
			this.nowPressPointer		= pointer;
		}
		
		/**
		 * 
		 */
		public void releaseDown()
		{
			this.nowCharacterIndex		= this.normalCharacterIndex;
		}

		/**
		 * @return
		 */
		public boolean isDownSignalThrow() {
			return isDownSignalThrow;
		}

		/**
		 * @return
		 */
		public EventInterface getDownEvent() {
			return downEvent;
		}

		/**
		 * @return
		 */
		public boolean isUpSignalThrow() {
			return isUpSignalThrow;
		}

		/**
		 * @return
		 */
		public EventInterface getUpEvent() {
			return upEvent;
		}

		/**
		 * @return
		 */
		public boolean isTouchSignalThrow() {
			return isTouchSignalThrow;
		}

		/**
		 * @return
		 */
		public EventInterface getTouchEvent() {
			return touchEvent;
		}
		
		public void set(int x, int y, int width, int height)
		{
			this.x		= x;
			this.y		= y;
			this.width	= width;
			this.height	= height;
			this.x2		= x + width;
			this.y2		= y + height;
		}
		
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
		public int getNowCharacterIndex() {
			return nowCharacterIndex;
		}

		/**
		 * @return
		 */
		public int getNowPressPointer() {
			return nowPressPointer;
		}

		/**
		 * 
		 */
		public void clear()
		{
			this.isDownSignalThrow		= true;
			this.downEvent				= null;
			this.isUpSignalThrow		= true;
			this.upEvent				= null;
			this.isTouchSignalThrow		= true;
			this.touchEvent				= null;
		}
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	public class BurstInformation
	{
		private boolean isActive;
		private int characterIndex;
		private boolean isCenter;
		private int x;
		private int y;
		private int alpha;
		private int width;
		private int height;
		
		public BurstInformation()
		{
			this.isActive			= false;
		}
		
		public void clear()
		{
			this.isActive			= false;
		}
		
		public void set(int characterIndex, boolean isCenter, int x, int y, int alpha, int width, int height)
		{
			this.characterIndex			= characterIndex;
			this.isCenter				= isCenter;
			this.x						= x;
			this.y						= y;
			this.alpha					= alpha;
			this.width					= width;
			this.height					= height;
			
			this.isActive				= true;
		}

		public boolean isActive() {
			return isActive;
		}

		public int getCharacterIndex() {
			return characterIndex;
		}

		public boolean isCenter() {
			return isCenter;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getAlpha() {
			return alpha;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
