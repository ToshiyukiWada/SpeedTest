package android.futuresoftware.jp.speedtest;

import android.futuresoftware.jp.speedtest.scene.game.GameButton;
import android.futuresoftware.jp.speedtest.scene.game.GameProcess;
import android.futuresoftware.jp.speedtest.scene.game.GameRenderer;
import android.futuresoftware.jp.speedtest.scene.game.GameScene;
import android.futuresoftware.jp.speedtest.scene.menu.MenuButton;
import android.futuresoftware.jp.speedtest.scene.menu.MenuProcess;
import android.futuresoftware.jp.speedtest.scene.menu.MenuRenderer;
import android.futuresoftware.jp.speedtest.scene.menu.MenuScene;
import android.futuresoftware.jp.speedtest.scene.score.ScoreProcess;
import android.futuresoftware.jp.speedtest.scene.score.ScoreRenderer;
import android.futuresoftware.jp.speedtest.scene.score.ScoreScene;
import android.futuresoftware.jp.speedtest.scene.shop.ShopProcess;
import android.futuresoftware.jp.speedtest.scene.shop.ShopRenderer;
import android.futuresoftware.jp.speedtest.scene.shop.ShopScene;
import android.futuresoftware.jp.speedtest.scene.splash.SplashProcess;
import android.futuresoftware.jp.speedtest.scene.splash.SplashRenderer;
import android.futuresoftware.jp.speedtest.scene.splash.SplashScene;
import android.futuresoftware.jp.speedtest.scene.stage.StageButton;
import android.futuresoftware.jp.speedtest.scene.stage.StageProcess;
import android.futuresoftware.jp.speedtest.scene.stage.StageRenderer;
import android.futuresoftware.jp.speedtest.scene.stage.StageScene;
import android.futuresoftware.jp.speedtest.scene.title.TitleProcess;
import android.futuresoftware.jp.speedtest.scene.title.TitleRenderer;
import android.futuresoftware.jp.speedtest.scene.title.TitleScene;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import jp.futuresoftware.android.sakura.SAKURA;
import jp.futuresoftware.android.sakura.SakuraActivity;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class MainActivity extends SakuraActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
		//---------------------------------------------------------------------
        // 基底クラスコール
		//---------------------------------------------------------------------
        super.onCreate(savedInstanceState);

		//---------------------------------------------------------------------
        // 初期設定
		//---------------------------------------------------------------------
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);						// フルスクリーンでの呼び出し
        requestWindowFeature(Window.FEATURE_NO_TITLE);											// タイトルの非表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);					// 省電力モードOFF
        setVolumeControlStream(AudioManager.STREAM_MUSIC);                      				// ボリュームはボリュームボタンを有効にする

		//---------------------------------------------------------------------
        // アプリケーション定義
		//---------------------------------------------------------------------
        this.init("UITest");																	// アプリケーション初期化
        this.sakuraManager.setVirtualWidth(960);												// 仮想画面サイズ設定
        this.sakuraManager.setVirtualHeight(540);												// 仮想画面サイズ設定
        this.sakuraManager.setBackgroundColor(Color.parseColor("#ffffff"));						// 背景色指定
        this.sakuraManager.setTextTextureBufferSize(20);										// テキストテクスチャバッファーサイズの指定
        this.sakuraManager.setDebug(false);														// デバッグモード
		this.sakuraManager.setFont("Boxy-Bold.ttf", Color.parseColor("#000000"));				// フォント関連の設定

		//---------------------------------------------------------------------
        // AdMobの有効化
		//---------------------------------------------------------------------
        this.sakuraManager.setAdMob("ca-app-pub-2487912231582475/9419668346", SAKURA.ADMOB_VERTICAL_POSITION.BOTTOM, 320, 50);

		//---------------------------------------------------------------------
        // シーン追加・定義
		//---------------------------------------------------------------------
        this.sakuraManager.addScene(new SplashScene ("SPLASH"   ,  new SplashRenderer()     , new SplashProcess()   , null));
        this.sakuraManager.addScene(new TitleScene  ("TITLE"    ,  new TitleRenderer()      , new TitleProcess()    , null));
        this.sakuraManager.addScene(new MenuScene   ("MENU"     ,  new MenuRenderer()       , new MenuProcess()     , new MenuButton()));
		this.sakuraManager.addScene(new StageScene  ("STAGE"    ,  new StageRenderer()      , new StageProcess()    , new StageButton()));
		this.sakuraManager.addScene(new GameScene   ("GAME"     ,  new GameRenderer()       , new GameProcess()     , new GameButton()));
        this.sakuraManager.addScene(new ScoreScene  ("SCORE"    ,  new ScoreRenderer()      , new ScoreProcess()    , null));
        this.sakuraManager.addScene(new ShopScene   ("SHOP"     ,  new ShopRenderer()       , new ShopProcess()     , null));

		//---------------------------------------------------------------------
        // 描画開始
        //---------------------------------------------------------------------
        setContentView(getContentView());
    }
}