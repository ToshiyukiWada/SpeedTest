package android.futuresoftware.jp.speedtest;

import android.futuresoftware.jp.speedtest.scene.splash.SplashProcess;
import android.futuresoftware.jp.speedtest.scene.splash.SplashRenderer;
import android.futuresoftware.jp.speedtest.scene.test.TestProcess;
import android.futuresoftware.jp.speedtest.scene.test.TestRenderer;
import android.futuresoftware.jp.speedtest.scene.test.TestScene;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import jp.futuresoftware.android.sakura.SakuraActivity;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class MainActivity extends SakuraActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        // 基底クラスコール
        super.onCreate(savedInstanceState);

        // 初期設定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);		// フルスクリーンでの呼び出し
        requestWindowFeature(Window.FEATURE_NO_TITLE);							// タイトルの非表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	// 省電力モードOFF
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // アプリケーション定義
        this.init("UITest");
        this.sakuraManager.setVirtualWidth(960);								// 仮想画面サイズ設定
        this.sakuraManager.setVirtualHeight(540);								// 仮想画面サイズ設定
        this.sakuraManager.setBackgroundColor(Color.parseColor("#FFFFFF"));		// 背景色指定
        this.sakuraManager.setTextTextureBufferSize(20);						// テキストテクスチャバッファーサイズの指定
        this.sakuraManager.setDebug(true);										// デバッグモード

        // シーン追加・定義
        this.sakuraManager.addScene(new TestScene("SPLASH",  new SplashRenderer() , new SplashProcess(), null));
        this.sakuraManager.addScene(new TestScene("TEST",  new TestRenderer() , new TestProcess(), null));

        //---------------------------------------------------------------------
        // 描画開始
        //---------------------------------------------------------------------
        setContentView(getContentView());
    }
}