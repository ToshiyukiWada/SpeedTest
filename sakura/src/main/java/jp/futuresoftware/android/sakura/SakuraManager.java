package jp.futuresoftware.android.sakura;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import jp.futuresoftware.android.sakura.base.QueueBase;
import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;
import jp.futuresoftware.android.sakura.connection.bluetooth.BluetoothManager;
import jp.futuresoftware.android.sakura.core.SakuraDraw;
import jp.futuresoftware.android.sakura.core.SakuraProcess;
import jp.futuresoftware.android.sakura.core.SakuraRendererThread;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;
import jp.futuresoftware.android.sakura.core.SakuraView;
import jp.futuresoftware.android.sakura.information.TextureButtonInformation;
import jp.futuresoftware.android.sakura.queue.base.RendererQueueBase;
import jp.futuresoftware.android.sakura.texture.SakuraTexture;
import jp.futuresoftware.android.sakura.texture.TextureManager;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class SakuraManager
{
    //-------------------------------------------
    // メンバ変数定義
    //-------------------------------------------
    // アプリケーション基本情報系
    private SakuraActivity sakuraActivity;					// メインアクティビティ
    private Context context;								// メインアクティビティContext
    private String applicationName;							// アプリケーション名
    private String UUID;									// 端末を(ほぼ)一意に識別する為のUUID
    private SakuraView sakuraView;							// GLSurfaceView
    private SakuraProcess sakuraProcess;					// ProcessThread
    private SakuraRendererThread sakuraRendererThread;		// SakuraRendererThread
    private SakuraDraw sakuraDraw;							// Draw
    private SakuraTouchManager sakuraTouchManager;			// SakuraTouchManager
    private SAKURA.STATUS status;							// 現在のステータス
    private SAKURA.STATUS_DETAIL statusDetail;				// 現在のステータス(初期処理・終了処理の場合のスレッド系の状態)
    private int startQueueCounter;							// キュー未処理件数
    private int finishQueueCounter;							// キュー処理済件数
    private ArrayList<QueueBase> initQueueList;				// 初期化処理のキューリスト
    private HashMap<String, Object> saveDatas;				// アプリケーション内のデータ保持
    private ArrayList<RendererQueueBase> rendererQueueList;	// 描画処理のキューリスト
    private int targetFps;                                  // 目標FPS

    // 開発オプション系
    private boolean isDebug;								// デバッグモード起動か否か

    // 画面サイズ系
    private int virtualWidth;								// 仮想ディスプレイサイズ(幅)
    private int virtualHeight;								// 仮想ディスプレイサイズ(高さ)
    private int displayWidth;								// 実際のディスプレイサイズ(幅)
    private int displayHeight;								// 実際のディスプレイサイズ(高さ)
    private int displayDpi;									// ディスプレイのDPI
    private float displayDensity;							// ディスプレイのDPIから求めたDENSITY
    private int backgroundColor;							// 背景色
    private float backgroundColorRed;						// 背景色(R)
    private float backgroundColorGreen;						// 背景色(G)
    private float backgroundColorBlue;						// 背景色(B)
    private int oppositeBackgroundColor;					// 背景色を反転したもの(文字の描画の標準色などに利用する)
    private float oppositeBackgroundColorRed;				// 背景色を反転したもの(R)
    private float oppositeBackgroundColorGreen;				// 背景色を反転したもの(G)
    private float oppositeBackgroundColorBlue;				// 背景色を反転したもの(B)
    private int frLayoutWidth;								// glレイアウトを含むフレームレイアウトの幅
    private int frLayoutHeight;								// glレイアウトを含むフレームレイアウトの高さ
    private int glLayoutWidth;								// glレイアウトの幅
    private int glLayoutHeight;								// glレイアウトの高さ
    private int glLayoutMarginHorizontal;					// glレイアウトをフレームレイアウトの中央に配置する為の横方向のマージン
    private int glLayoutMarginVertical;						// glレイアウトをフレームレイアウトの中央に配置する為の縦方向のマージン
    private int glLayoutStartRealPositionX;					//
    private int glLayoutStartRealPositionY;					//
    private int glLayoutEndRealPositionX;					//
    private int glLayoutEndRealPositionY;					//
    private int adMobLayoutWidth;							// adMobレイアウトの幅
    private int adMobLayoutHeight;							// adMobレイアウトの高さ
    private boolean isPortrait;                             // 縦長画面か否か

    // シーン系
    private HashMap<String, SceneBase> scenes;				            		// シーン名とシーンを管理する連想配列
    private SceneBase nowScene;								            		// 現在表示中のシーン
    private String nextSceneName;							            		// 次に表示する予約が入っているシーン名
    private ArrayList<TextureButtonInformation> nowTextureButtonInformations;	// このシーンで適用されているボタン(このシーンで読み込まれているTextureManagerに属するボタン定義をまとめたもの)

    // テクスチャ管理系
    private ArrayList<TextureManager> textures;				// 全てのテクスチャを管理する
    private SakuraTexture sakuraTexture;					// フレームワークが標準で用意するテクスチャ(英字・数字・平仮名の表示ならこれを使って出力が可能)
    private String sakuraTextureFont;						// 上記フレームワークが標準で用意するテクスチャに採用するフォントファイル名(assetsディレクトリに配置)
    private int sakuraTextureFontSize;						// 上記フレームワークが標準で用意するテクスチャに採用するフォントサイズ
    private int textTextureBufferSize;						// テキストテクスチャバッファサイズ
    private int[] alphaVboIDs;								// アルファ値(0～100までのVBOのID(OpenGLが発行)を保持する配列)

    //　SoundPool(SE)
    private int maxStreams;									// SEの最大同時再生数
    private HashMap<String, Integer> sounds;				// サウンド名とIDを紐付ける
    private SoundPool soundPool;							// サウンド(SE)

    // 待ち画面
    private boolean isUseWait;								// 待ち画面を表示するか否か
    private SceneBase waitScene;							// 待ち画面が定義されたシーン(initへ記述した場合でもスレッド処理は実施されない特別なシーンとなる)
    private boolean isWaitSceneInitComplete;				// 待ち画面が外部定義されている場合に、そのinit処理を通過したか否か
    private boolean isWaitView;								// 待ち画面を表示するか否か

    // AdMob
    private boolean isUseAdMob;								// AdMobを利用するか否か
    private String adUnitID;								// AdMobのUNIT_ID
    private SAKURA.ADMOB_VERTICAL_POSITION adMobVerticalPosition;	// AdMobを表示する位置
    private int adMobWidthDp;								// AdMobの幅
    private int adMobHeightDp;								// AdMobの高さ

    // Bluetooth
    private BluetoothManager bluetoothManager;

    //=========================================================================
    //
    // コンストラクタ
    //
    //=========================================================================
    /**
     * @param sakuraActivity
     * @param applicationName
     */
    public SakuraManager(SakuraActivity sakuraActivity, String applicationName, String UUID)
    {
        this.sakuraActivity				= sakuraActivity;
        this.context					= sakuraActivity;
        this.applicationName			= applicationName;
        this.UUID						= UUID;
        this.targetFps					= -1;

        this.sakuraDraw					= new SakuraDraw(this);

        this.status						= SAKURA.STATUS.INITIALIZE;
        this.statusDetail				= SAKURA.STATUS_DETAIL.WAIT;
        this.startQueueCounter			= 0;
        this.finishQueueCounter			= 0;
        this.initQueueList				= new ArrayList<QueueBase>();
        this.saveDatas					= new HashMap<String, Object>();
        this.rendererQueueList			= new ArrayList<RendererQueueBase>();

        this.isDebug					= false;

        this.nowTextureButtonInformations	= new ArrayList<TextureButtonInformation>();

        this.textures					= new ArrayList<TextureManager>();
        this.sakuraTextureFont			= "";
        this.sakuraTextureFontSize		= 1;
        this.textTextureBufferSize		= -1;
        this.alphaVboIDs				= new int[101];

        this.maxStreams					= 1;
        this.sounds						= new HashMap<String, Integer>();
        this.soundPool					= null;

        this.bluetoothManager			= new BluetoothManager(this);

        this.isUseAdMob					= false;
        this.adMobVerticalPosition		= SAKURA.ADMOB_VERTICAL_POSITION.TOP;
        this.adMobWidthDp				= 0;
        this.adMobHeightDp				= 0;

        this.isPortrait                 = true;
        Configuration configuration     = sakuraActivity.getResources().getConfiguration();
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)        { this.isPortrait   = false;  }
        else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)   { this.isPortrait   = true; }
    }

    /**
     * @return
     */
    public SakuraActivity getSakuraActivity() {
        return sakuraActivity;
    }

    /**
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @return
     */
    public SakuraView getSakuraView() {
        return sakuraView;
    }

    /**
     * @param sakuraView
     */
    public void setSakuraView(SakuraView sakuraView) {
        this.sakuraView = sakuraView;
    }

    /**
     * @return
     */
    public SakuraProcess getSakuraProcess() {
        return sakuraProcess;
    }

    /**
     * @param sakuraProcess
     */
    public void setSakuraProcess(SakuraProcess sakuraProcess) {
        this.sakuraProcess = sakuraProcess;
    }

    /**
     * @return
     */
    public SakuraRendererThread getSakuraRendererThread() {
        return sakuraRendererThread;
    }

    /**
     * @param sakuraRendererThread
     */
    public void setSakuraRendererThread(SakuraRendererThread sakuraRendererThread) {
        this.sakuraRendererThread = sakuraRendererThread;
    }

    /**
     * @return
     */
    public SakuraDraw getSakuraDraw() {
        return sakuraDraw;
    }

    /**
     * @return
     */
    public SakuraTouchManager getSakuraTouchManager() {
        return sakuraTouchManager;
    }

    /**
     * @param sakuraTouchManager
     */
    public void setSakuraTouchManager(SakuraTouchManager sakuraTouchManager) {
        this.sakuraTouchManager = sakuraTouchManager;
    }

    //=========================================================================
    //
    // UUID
    //
    //=========================================================================
    /**
     * @return
     */
    public String getUUID() {
        return UUID;
    }

    //=========================================================================
    //
    // ステータス等
    //
    //=========================================================================
    public SAKURA.STATUS getStatus() {
        return status;
    }

    //=========================================================================
    //
    // シーン等
    //
    //=========================================================================
    /**
     * シーンの追加
     *
     * @param sceneBase
     */
    public void addScene(SceneBase sceneBase)
    {
        // 初めてのシーン追加か否か
        boolean isFirstScene		= false;
        if (this.scenes == null)
        {
            this.scenes				= new HashMap<String, SceneBase>();
            isFirstScene			= true;
        }

        // 各インスタンスにManagerを登録する
        sceneBase.setSakuraManager(this);

        if (sceneBase.getSceneRenderer() != null)
        {
            sceneBase.getSceneRenderer().setSakuraManager(this);
            sceneBase.getSceneRenderer().setScene(sceneBase);
            sceneBase.getSceneRenderer().setProcess(sceneBase.getSceneProcess());
			sceneBase.getSceneRenderer().setButton(sceneBase.getSceneButton());
        }
        if (sceneBase.getSceneProcess() != null)
        {
            sceneBase.getSceneProcess().setSakuraManager(this);
            sceneBase.getSceneProcess().setScene(sceneBase);
			sceneBase.getSceneProcess().setRenderer(sceneBase.getSceneRenderer());
			sceneBase.getSceneProcess().setButton(sceneBase.getSceneButton());
        }
        if (sceneBase.getSceneButton() != null)
        {
            sceneBase.getSceneButton().setSakuraManager(this);
            sceneBase.getSceneButton().setScene(sceneBase);
            sceneBase.getSceneButton().setProcess(sceneBase.getSceneProcess());
			sceneBase.getSceneButton().setRenderer(sceneBase.getSceneRenderer());
        }

        // シーン配列にシーン情報を登録する
        this.scenes.put(sceneBase.getSceneName(), sceneBase);

        // 最初のシーンの場合
        if (isFirstScene == true)
        {
            this.changeScene(sceneBase.getSceneName());
        }
    }

    /**
     * シーン切り替え
     *
     * @param sceneName
     */
    public void changeScene(String sceneName)
    {
        // タッチ操作を一旦クリアする
        this.sakuraTouchManager.init();

        // 次のシーンへ
        this.nextSceneName		= sceneName;
        if (this.nowScene == null){ changeSceneInner(); }
        else
        {
            this.status		= SAKURA.STATUS.TERMINATE;
        }
    }

    /**
     * シーン切り替え内部処理
     */
    private void changeSceneInner()
    {
        this.nowScene			= this.scenes.get(this.nextSceneName);
        this.nextSceneName		= "";
        this.status				= SAKURA.STATUS.INITIALIZE;
    }

    /**
     * 現在動作中のシーンの取得
     *
     * @return
     */
    public SceneBase getNowScene()
    {
        return this.nowScene;
    }

    //=========================================================================
    //
    // 待ち画面系
    //
    //=========================================================================
    public boolean isUseWait() {
        return isUseWait;
    }

    public void setUseWait(boolean isUseWait) {
        this.isUseWait = isUseWait;
    }

    public SceneBase getWaitScene() {
        return waitScene;
    }

    public void setWaitScene(SceneBase waitScene) {
        this.waitScene = waitScene;
    }

    public boolean isWaitSceneInitComplete() {
        return isWaitSceneInitComplete;
    }

    public void setWaitSceneInitComplete(boolean isWaitSceneInitComplete) {
        this.isWaitSceneInitComplete = isWaitSceneInitComplete;
    }

    public boolean isWaitView() {
        return isWaitView;
    }

    public void setWaitView(boolean isWaitView) {
        this.isWaitView = isWaitView;
    }

    //=========================================================================
    //
    // 色・背景色・レイアウト等
    //
    //=========================================================================
    public void setBackgroundColor(int backgroundColor)
    {
        // 背景色と反転色の設定
        this.backgroundColor				= backgroundColor;
        this.backgroundColorRed				= (float)((this.backgroundColor & 0x00ff0000) >> 16);																			// 現在の背景カラーコードをRGBに分割する
        this.backgroundColorGreen			= (float)((this.backgroundColor & 0x0000ff00) >> 8);																			// 現在の背景カラーコードをRGBに分割する
        this.backgroundColorBlue			= (float)((this.backgroundColor & 0x000000ff) >> 0);																			// 現在の背景カラーコードをRGBに分割する
        this.oppositeBackgroundColor		= 0;
        this.oppositeBackgroundColorRed		= 255.0f - this.backgroundColorRed;																							// 現在の背景カラーコードの真逆の色を求める
        this.oppositeBackgroundColorGreen	= 255.0f - this.backgroundColorGreen;																							// 現在の背景カラーコードの真逆の色を求める
        this.oppositeBackgroundColorBlue	= 255.0f - this.backgroundColorBlue;																							// 現在の背景カラーコードの真逆の色を求める

    }

    public float getBackgroundColorRed() {
        return backgroundColorRed;
    }

    public float getBackgroundColorGreen() {
        return backgroundColorGreen;
    }

    public float getBackgroundColorBlue() {
        return backgroundColorBlue;
    }

    public int getOppositeBackgroundColor() {
        return oppositeBackgroundColor;
    }

    public float getOppositeBackgroundColorRed() {
        return oppositeBackgroundColorRed;
    }

    public float getOppositeBackgroundColorGreen() {
        return oppositeBackgroundColorGreen;
    }

    public float getOppositeBackgroundColorBlue() {
        return oppositeBackgroundColorBlue;
    }

    public int getFrLayoutWidth() {
        return frLayoutWidth;
    }

    public void setFrLayoutWidth(int frLayoutWidth) {
        this.frLayoutWidth = frLayoutWidth;
    }

    public int getFrLayoutHeight() {
        return frLayoutHeight;
    }

    public void setFrLayoutHeight(int frLayoutHeight) {
        this.frLayoutHeight = frLayoutHeight;
    }

    public int getGlLayoutWidth() {
        return glLayoutWidth;
    }

    public void setGlLayoutWidth(int glLayoutWidth) {
        this.glLayoutWidth = glLayoutWidth;
    }

    public int getGlLayoutHeight() {
        return glLayoutHeight;
    }

    public void setGlLayoutHeight(int glLayoutHeight) {
        this.glLayoutHeight = glLayoutHeight;
    }

    public int getGlLayoutMarginHorizontal() {
        return glLayoutMarginHorizontal;
    }

    public void setGlLayoutMarginHorizontal(int glLayoutMarginHorizontal) {
        this.glLayoutMarginHorizontal = glLayoutMarginHorizontal;
    }

    public int getGlLayoutMarginVertical() {
        return glLayoutMarginVertical;
    }

    public void setGlLayoutMarginVertical(int glLayoutMarginVertical) {
        this.glLayoutMarginVertical = glLayoutMarginVertical;
    }

    public int getGlLayoutStartRealPositionX() {
        return glLayoutStartRealPositionX;
    }

    public void setGlLayoutStartRealPositionX(int glLayoutStartRealPositionX) {
        this.glLayoutStartRealPositionX = glLayoutStartRealPositionX;
    }

    public int getGlLayoutStartRealPositionY() {
        return glLayoutStartRealPositionY;
    }

    public void setGlLayoutStartRealPositionY(int glLayoutStartRealPositionY) {
        this.glLayoutStartRealPositionY = glLayoutStartRealPositionY;
    }

    public int getGlLayoutEndRealPositionX() {
        return glLayoutEndRealPositionX;
    }

    public void setGlLayoutEndRealPositionX(int glLayoutEndRealPositionX) {
        this.glLayoutEndRealPositionX = glLayoutEndRealPositionX;
    }

    public int getGlLayoutEndRealPositionY() {
        return glLayoutEndRealPositionY;
    }

    public void setGlLayoutEndRealPositionY(int glLayoutEndRealPositionY) {
        this.glLayoutEndRealPositionY = glLayoutEndRealPositionY;
    }

    public int getAdMobLayoutWidth() {
        return adMobLayoutWidth;
    }

    public void setAdMobLayoutWidth(int adMobLayoutWidth) {
        this.adMobLayoutWidth = adMobLayoutWidth;
    }

    public int getAdMobLayoutHeight() {
        return adMobLayoutHeight;
    }

    public void setAdMobLayoutHeight(int adMobLayoutHeight) {
        this.adMobLayoutHeight = adMobLayoutHeight;
    }

    public SAKURA.ADMOB_VERTICAL_POSITION getAdMobVerticalPosition() {
        return adMobVerticalPosition;
    }

    public void setAdMobVerticalPosition(
            SAKURA.ADMOB_VERTICAL_POSITION adMobVerticalPosition) {
        this.adMobVerticalPosition = adMobVerticalPosition;
    }

    public int getAdMobWidthDp() {
        return adMobWidthDp;
    }

    public void setAdMobWidthDp(int adMobWidthDp) {
        this.adMobWidthDp = adMobWidthDp;
    }

    public int getAdMobHeightDp() {
        return adMobHeightDp;
    }

    public void setAdMobHeightDp(int adMobHeightDp) {
        this.adMobHeightDp = adMobHeightDp;
    }

    public boolean isPortrait(){ return this.isPortrait; }

    //=========================================================================
    //
    // AdMob
    //
    //=========================================================================
    /**
     * @param adUnitID
     * @param adMobVerticalPosition
     * @param adMobWidthDp
     * @param adMobHeightDp
     */
    public void setAdMob(String adUnitID, SAKURA.ADMOB_VERTICAL_POSITION adMobVerticalPosition, int adMobWidthDp, int adMobHeightDp)
    {
        this.isUseAdMob						= true;
        this.adUnitID						= adUnitID;
        this.adMobVerticalPosition			= adMobVerticalPosition;
        this.adMobWidthDp					= adMobWidthDp;
        this.adMobHeightDp					= adMobHeightDp;
    }

    /**
     *
     */
    public void invalidityMob()
    {
        this.isUseAdMob						= false;
        this.adUnitID						= "";
        this.adMobVerticalPosition			= SAKURA.ADMOB_VERTICAL_POSITION.TOP;
        this.adMobWidthDp					= 0;
        this.adMobHeightDp					= 0;
    }

    public boolean isUseAdMob() {
        return isUseAdMob;
    }

    public void setUseAdMob(boolean isUseAdMob) {
        this.isUseAdMob = isUseAdMob;
    }

    public String getAdUnitID() {
        return adUnitID;
    }

    public void setAdUnitID(String adUnitID) {
        this.adUnitID = adUnitID;
    }

    //=========================================================================
    //
    // 初期化処理(GL関係だけでなく、色々と)
    //
    //=========================================================================
    public void ready(GL10 gl)
    {
        //---------------------------------------------------------------------
        // テキストテクスチャバッファサイズ
        //---------------------------------------------------------------------
        if (this.textTextureBufferSize == -1)
        {
            this.textTextureBufferSize			= SAKURA.TEXT_TEXTURE_DEFAULT_BUFFER_SIZE;
        }

        //---------------------------------------------------------------------
        // フレームワークのテクスチャを内部生成
        //---------------------------------------------------------------------
        this.sakuraTexture			= new SakuraTexture(this, gl);

        //---------------------------------------------------------------------
        // アルファブレンディング用の定義をOpenGL内に作成する
        // 100分割でアルファ値を指定できるようにしておく
        //---------------------------------------------------------------------
        // 試験的に色配列
        float[] colors = {
                1.0f, 1.0f, 1.0f, 0.00f,
                1.0f, 1.0f, 1.0f, 0.00f,
                1.0f, 1.0f, 1.0f, 0.00f,
                1.0f, 1.0f, 1.0f, 0.00f
        };

        ByteBuffer bbcl;						// 色・透明度の座標情報
        FloatBuffer fbcl;						// 色・透明度の座標情報
        bbcl	= ByteBuffer.allocateDirect(16 * 4);
        bbcl.order(ByteOrder.nativeOrder());
        fbcl	= bbcl.asFloatBuffer();

        for (int count = 0 ; count < this.alphaVboIDs.length ; count++)
        {
            colors[3]	+= (float)(1.0f / (float)this.alphaVboIDs.length);
            colors[7]	+= (float)(1.0f / (float)this.alphaVboIDs.length);
            colors[11]	+= (float)(1.0f / (float)this.alphaVboIDs.length);
            colors[15]	+= (float)(1.0f / (float)this.alphaVboIDs.length);

            fbcl.put(colors);
            fbcl.position(0);

            // VBOIDの取得
            int[] buffers = new int[1];
            ((GL11)gl).glGenBuffers(1, buffers, 0);
            alphaVboIDs[count]	= buffers[0];
            ((GL11)gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, alphaVboIDs[count]);
            ((GL11)gl).glBufferData(GL11.GL_ARRAY_BUFFER, fbcl.capacity() * 4, fbcl, GL11.GL_STATIC_DRAW);
        }

        //---------------------------------------------------------------------
        // SE同時再生数の設定とSoundPoolインスタンスの生成
        //---------------------------------------------------------------------
        this.soundPool				= new SoundPool(this.maxStreams, AudioManager.STREAM_MUSIC, 0);
    }

    /**
     * @return
     */
    public SakuraTexture getSakuraTexture() {
        return sakuraTexture;
    }

    //=========================================================================
    //
    // 処理状況更新
    //
    //=========================================================================
    /**
     *
     */
    public void updateStatus()
    {
        //---------------------------------------
        // スレッドキューが完了している否かをチェックする
        //---------------------------------------
        if (this.statusDetail.equals(SAKURA.STATUS_DETAIL.WAIT))
        {
            if (this.startQueueCounter == this.finishQueueCounter)
            {
                this.statusDetail		= SAKURA.STATUS_DETAIL.ACTIVE;
                this.startQueueCounter	= 0;
                this.finishQueueCounter = 0;
            }
        }

        // 現在の状態に合わせて、それぞれの状態遷移を判定する
        switch(this.status)
        {
            // 初期化処理を依頼されている場合
            case  INITIALIZE:
                this.status			= SAKURA.STATUS.INITIALIZING;						// 初期化処理中に状態遷移
                this.statusDetail	= SAKURA.STATUS_DETAIL.WAIT;					//
                this.nowScene.init();											// 初期処理
                break;

            // 初期化処理中
            case INITIALIZING:
                if (this.statusDetail.equals(SAKURA.STATUS_DETAIL.ACTIVE))
                {
                    // このシーンで利用(監視)するボタン一覧の削除
                    for (int countButtons = 0 ; countButtons < this.nowTextureButtonInformations.size() ; countButtons++)
                    {
                        // ボタンに紐付けられているイベントを全て削除
                        this.nowTextureButtonInformations.get(countButtons).clear();
                    }
                    this.nowTextureButtonInformations.clear();

                    // 初期化処理起動
                    this.sakuraView.getSakuraRenderer().runInit();			// Rendererの初期化処理を起動する
                    this.nowScene.getSceneProcess().init();					// Processの初期化処理を起動する
                    this.nowScene.initCallback();							// Sceneの初期化処理を起動する

                    if (this.nowScene.getSceneButton() != null)             // ボタン定義がある場合はボタンの登録処理を起動する
                    {
                        this.nowScene.getSceneButton().init();              // 初期化処理
                        this.nowScene.getSceneButton().doButton();          // ボタン登録処理
                    }

                    // 初期化処理が終わっていたら通常動作に遷移
                    this.status = SAKURA.STATUS.ACTIVE;
                }
                break;

            // 動作中にWAITになった場合
            case WAIT:
                if (this.statusDetail.equals(SAKURA.STATUS_DETAIL.ACTIVE)){ this.status = SAKURA.STATUS.ACTIVE; }	// WAIT処理が終わっていたら通常動作に遷移
                break;

            // 通常動作中
            case ACTIVE:
                if (!this.statusDetail.equals(SAKURA.STATUS_DETAIL.ACTIVE)){ this.status = SAKURA.STATUS.WAIT; }	// なんらかのwaitが発生したらwait処理に遷移
                break;

            // 終了処理を依頼されている場合
            case TERMINATE:
                this.status			= SAKURA.STATUS.TERMINATING;						// 終了処理中に状態遷移
                this.statusDetail	= SAKURA.STATUS_DETAIL.WAIT;						//
                this.nowScene.terminate();										// 描画系終了処理の呼び出し(次の描画スレッドが実施されるまでの予約)
                break;

            // 終了処理中
            case TERMINATING:
                if (this.statusDetail.equals(SAKURA.STATUS_DETAIL.ACTIVE))
                {
                    this.nowScene.terminateCallback();							//
                    changeSceneInner();											//
                }
                break;

            default: break;
        }
    }

    /**
     *
     */
    public void startThreadQueue()
    {
        startQueueCounter = startQueueCounter + 1;
    }

    /**
     *
     */
    public void finishThreadQueue()
    {
        finishQueueCounter = finishQueueCounter + 1;
    }

    //=========================================================================
    //
    // 戻るボタン制御
    //
    //=========================================================================
    /**
     *
     */
    public void onKeyBack()
    {
        if (this.nowScene != null){ this.nowScene.getSceneProcess().onKeyBack(); }
        else
        {
            finish();
        }
    }

    //=========================================================================
    //
    // アプリケーション終了
    //
    //=========================================================================
    /**
     *
     */
    public void finish()
    {
        // 現在動作中のシーンの停止処理をキックする




        // スレッドを停止する
        this.sakuraProcess.stopRunning();
        this.sakuraRendererThread.stopRunning();

        // アプリケーションを終了する
        ((android.app.Activity)this.getContext()).finish();
    }

    //=========================================================================
    //
    // スクリーン座標等
    //
    //=========================================================================
    /**
     * @return
     */
    public int getVirtualWidth() {
        return virtualWidth;
    }

    /**
     * @param virtualWidth
     */
    public void setVirtualWidth(int virtualWidth) {
        this.virtualWidth = virtualWidth;
    }

    /**
     * @return
     */
    public int getVirtualHeight() {
        return virtualHeight;
    }

    /**
     * @param virtualHeight
     */
    public void setVirtualHeight(int virtualHeight) {
        this.virtualHeight = virtualHeight;
    }

    /**
     * @return
     */
    public int getDisplayWidth() {
        return displayWidth;
    }

    /**
     * @param displayWidth
     */
    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    /**
     * @return
     */
    public int getDisplayHeight() {
        return displayHeight;
    }

    /**
     * @param displayHeight
     */
    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    /**
     * @return
     */
    public int getDisplayDpi() {
        return displayDpi;
    }

    /**
     * @param displayDpi
     */
    public void setDisplayDpi(int displayDpi) {
        this.displayDpi = displayDpi;
    }

    /**
     * @return
     */
    public float getDisplayDensity() {
        return displayDensity;
    }

    /**
     * @param displayDensity
     */
    public void setDisplayDensity(float displayDensity) {
        this.displayDensity = displayDensity;
    }

    //=========================================================================
    //
    // テクスチャ
    //
    //=========================================================================
    /**
     * @return
     */
    public ArrayList<TextureManager> getTextures() {
        return textures;
    }

    /**
     * @return
     */
    public int[] getAlphaVboIDs() {
        return alphaVboIDs;
    }

    /**
     * @param textureBitmapID
     * @param characterXmlStreamID
     * @return
     */
    public int getTextureID(int textureBitmapID, int characterXmlStreamID)
    {
        int result			= -1;
        for (int count = 0 ; count < this.textures.size() ; count++)
        {
            if (   this.textures.get(count).getTextureBitmapID() == textureBitmapID
                    && this.textures.get(count).getCharacterXmlStreamID() == characterXmlStreamID)
            {
                result			= count;
            }
        }
        return result;
    }

    //=========================================================================
    //
    // SE
    //
    //=========================================================================
    /**
     * @return
     */
    public SoundPool getSoundPool() {
        return soundPool;
    }

    /**
     * @param maxStreams
     */
    public void setMaxStreams(int maxStreams) {
        this.maxStreams = maxStreams;
    }

    /**
     * @return
     */
    public HashMap<String, Integer> getSounds() {
        return sounds;
    }

    /**
     * @param soundName
     * @return
     */
    public int getSoundID(String soundName)
    {
        if (this.sounds.containsKey(soundName))
        {
            return this.sounds.get(soundName);
        }
        else
        {
            return -1;
        }
    }

    /**
     * @param SoundName
     */
    public void playSound(String SoundName)
    {
        this.playSound(this.getSoundID(SoundName));
    }

	/**
	 *
	 * @param soundID
	 */
    public void playSound(int soundID)
    {
        try
        {
            this.soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
        }
        catch(Exception exp){}
    }

    //=========================================================================
    //
    // フォント
    //
    //=========================================================================
    /**
     * @param font
     * @param fontSize
     */
    public void setFont(String font, int fontSize)
    {
        this.sakuraTextureFont		= font;
        this.sakuraTextureFontSize	= fontSize;
    }

    /**
     * @return
     */
    public String getSakuraTextureFont() {
        return sakuraTextureFont;
    }

    /**
     * @return
     */
    public int getSakuraTextureFontSize() {
        return sakuraTextureFontSize;
    }


    /**
     * @return
     */
    public int getTextTextureBufferSize() {
        return textTextureBufferSize;
    }

    /**
     * @param textTextureBufferSize
     */
    public void setTextTextureBufferSize(int textTextureBufferSize) {
        this.textTextureBufferSize = textTextureBufferSize;
    }

    //=========================================================================
    //
    // デバッグ
    //
    //=========================================================================
    /**
     * @return
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * @param isDebug
     */
    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    //=========================================================================
    //
    // Bluetooth
    //
    //=========================================================================
    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public void setBluetoothManager(BluetoothManager bluetoothManager) {
        this.bluetoothManager = bluetoothManager;
    }

    //=========================================================================
    //
    // RendererでのQueue
    //
    //=========================================================================
    /**
     * @return
     */
    public ArrayList<RendererQueueBase> getRendererQueue()
    {
        return this.rendererQueueList;
    }

    /**
     * @param rendererQueueBase
     */
    public void addRendererQueue(RendererQueueBase rendererQueueBase)
    {
        this.startThreadQueue();
        this.rendererQueueList.add(rendererQueueBase);
    }

	/**
	 *
	 */
	public void clearRendererQueue()
	{
		this.rendererQueueList.clear();
	}

	//=========================================================================
    //
    // Buttons
    // このシーンに対して利用するボタン情報を追加し、その追加インデックスを返却する
    //
    //=========================================================================
    public int addButton(int textureID, int normalCharacterIndex, int touchCharacterIndex, int disableCharacterIndex, SceneButtonProcessBase sceneButtonProcessBase, int width, int height)
    {
		this.nowTextureButtonInformations.add(new TextureButtonInformation(textureID, normalCharacterIndex, touchCharacterIndex, disableCharacterIndex, sceneButtonProcessBase, width, height));
		return this.nowTextureButtonInformations.size() - 1;
    }

    public TextureButtonInformation getTextureButtonInformation(int buttonIndex)
    {
		return this.nowTextureButtonInformations.get(buttonIndex);
    }

    /**
     * @return
     */
    public ArrayList<TextureButtonInformation> getNowTextureButtonInformations() {
        return this.nowTextureButtonInformations;
    }
}
