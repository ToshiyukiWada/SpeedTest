package jp.futuresoftware.android.sakura;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import jp.futuresoftware.android.sakura.connection.bluetooth.BluetoothCommunicationManager;
import jp.futuresoftware.android.sakura.core.SakuraProcess;
import jp.futuresoftware.android.sakura.core.SakuraRendererThread;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;
import jp.futuresoftware.android.sakura.core.SakuraView;
import jp.futuresoftware.android.sakura.exception.SakuraException;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class SakuraActivity extends Activity
{
    protected SakuraManager sakuraManager;
    private AdView adView;

    /**
     * コンストラクタ
     */
    public SakuraActivity()
    {
        this.adView			= null;
    }

    /**
     * 初期化処理
     *
     * @param applicationName
     */
    public void init(String applicationName)
    {
        this.init(applicationName, 60.0f);
    }

    /**
     * 初期化処理
     *
     * @param applicationName
     * @param targetFps
     */
    public void init(String applicationName, float targetFps)
    {
        //---------------------------------------------------------------------
        // エラーハンドラ
        //---------------------------------------------------------------------
        SakuraException sakuraException		= new SakuraException(this);
        Thread.setDefaultUncaughtExceptionHandler(sakuraException);

        //---------------------------------------------------------------------
        // 機器UUIDの作成と確保
        //---------------------------------------------------------------------
        String uuid;
        try
        {
            FileInputStream fis		= openFileInput(SAKURA.UUID_DAT_FILENAME);
            ObjectInputStream ois	= new ObjectInputStream(fis);
            uuid					= (String)ois.readObject();
        }
        catch(Exception exp){ uuid	= null; }
        if (uuid == null)
        {
            uuid					= UUID.randomUUID().toString();
            try
            {
                FileOutputStream fos = openFileOutput(SAKURA.UUID_DAT_FILENAME, MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(uuid);
                oos.close();
            }
            catch(Exception exp){}
        }

        //---------------------------------------------------------------------
        // SakuraManagerインスタンスの生成
        //---------------------------------------------------------------------
        this.sakuraManager			= new SakuraManager(this, applicationName, uuid);

        //---------------------------------------------------------------------
        // SakuraView/SakuraProcessインスタンスの生成しManagerに登録
        //---------------------------------------------------------------------
        this.sakuraManager.setSakuraView(new SakuraView(this));
        this.sakuraManager.setSakuraProcess(new SakuraProcess(this));
        this.sakuraManager.setSakuraRendererThread(new SakuraRendererThread(this, targetFps));
        this.sakuraManager.setSakuraTouchManager(new SakuraTouchManager(this));

        //---------------------------------------------------------------------
        // Process処理開始
        //---------------------------------------------------------------------
        this.sakuraManager.getSakuraProcess().start();
        this.sakuraManager.getSakuraRendererThread().start();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    public void onPause()
    {
        if (this.adView != null)
        {
            adView.pause();
        }

        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    public void onResume()
    {
        super.onResume();

        if (this.adView != null)
        {
            adView.resume();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    public void onDestroy()
    {
        //---------------------------------------------------------------------
        // adMobの停止
        //---------------------------------------------------------------------
        if (this.adView != null)
        {
            this.adView.destroy();
        }

        //---------------------------------------------------------------------
        // 各予約処理系の停止
        //---------------------------------------------------------------------
        // BluetoothManager
        this.sakuraManager.getBluetoothManager().destroy();

        // 親停止処理
        super.onDestroy();

        //---------------------------------------------------------------------
        // 各予約処理系の停止
        //---------------------------------------------------------------------
        // スレッドの停止
        this.sakuraManager.getSakuraProcess().stopRunning();
        this.sakuraManager.getSakuraProcess().interrupt();
    }

    /**
     * @return
     */
    public SakuraManager getSakuraManager()
    {
        return this.sakuraManager;
    }

    /**
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public LinearLayout getContentView()
    {
        //---------------------------------------------------------------------
        // 画面解像度の取得と格納
        //---------------------------------------------------------------------
        WindowManager windowManager	= (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display				= windowManager.getDefaultDisplay();
        Point displaySize			= new Point();
        try
        {
            display.getSize(displaySize);
            this.sakuraManager.setDisplayWidth(displaySize.x);
            this.sakuraManager.setDisplayHeight(displaySize.y);
        }
        catch (java.lang.NoSuchMethodError ignore)
        {
            this.sakuraManager.setDisplayWidth(display.getWidth());
            this.sakuraManager.setDisplayHeight(display.getHeight());

            // Tablet用OSの場合はシステムバーの領域を考慮する
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR2)
            {
                this.sakuraManager.setDisplayHeight(this.sakuraManager.getDisplayHeight() - 48);
            }
        }

        //---------------------------------------------------------------------
        // DPIの取得と格納
        //---------------------------------------------------------------------
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.sakuraManager.setDisplayDpi(metrics.densityDpi);
        this.sakuraManager.setDisplayDensity(metrics.density);

        //---------------------------------------------------------------------
        // 画面サイズ算出
        //---------------------------------------------------------------------
        // 仮想画面サイズの縦横比を算出する
        float virtualScreenRatio			= (float)this.sakuraManager.getVirtualWidth() / (float)this.sakuraManager.getVirtualHeight();
        this.sakuraManager.setFrLayoutWidth(this.sakuraManager.getDisplayWidth());
        this.sakuraManager.setFrLayoutHeight((int)(this.sakuraManager.getDisplayHeight() - (this.sakuraManager.isUseAdMob()?(this.sakuraManager.getAdMobHeightDp() * this.sakuraManager.getDisplayDensity()):0)));
        if (this.sakuraManager.isUseAdMob())
        {
            this.sakuraManager.setAdMobLayoutWidth(this.sakuraManager.getDisplayWidth());
            this.sakuraManager.setAdMobLayoutHeight((int)(this.sakuraManager.getAdMobHeightDp() * this.sakuraManager.getDisplayDensity()));
        }
        else
        {
            this.sakuraManager.setAdMobLayoutWidth(0);
            this.sakuraManager.setAdMobLayoutHeight(0);
        }

        // 一旦横幅を基準に高さを求める
        this.sakuraManager.setGlLayoutWidth(this.sakuraManager.getFrLayoutWidth());
        this.sakuraManager.setGlLayoutHeight((int)(this.sakuraManager.getGlLayoutWidth() / virtualScreenRatio));
        if (this.sakuraManager.getFrLayoutHeight() < this.sakuraManager.getGlLayoutHeight())
        {
            this.sakuraManager.setGlLayoutHeight(this.sakuraManager.getFrLayoutHeight());
            this.sakuraManager.setGlLayoutWidth((int)(this.sakuraManager.getGlLayoutHeight() * virtualScreenRatio));
        }

        // マージンを求める
        this.sakuraManager.setGlLayoutMarginHorizontal((this.sakuraManager.getFrLayoutWidth() - this.sakuraManager.getGlLayoutWidth()) / 2);
        this.sakuraManager.setGlLayoutMarginVertical((this.sakuraManager.getFrLayoutHeight() - this.sakuraManager.getGlLayoutHeight()) / 2);

        //---------------------------------------------------------------------
        // 大枠のレイアウトの生成
        //---------------------------------------------------------------------
        LinearLayout mainLayout			= new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //---------------------------------------------------------------------
        // GLフレームレイアウトとGLレイアウトの生成
        //---------------------------------------------------------------------
        LinearLayout frLayout						= new LinearLayout(this);
        LinearLayout.LayoutParams frViewParams		= null;
        frLayout.setBackgroundColor(Color.rgb((int) this.sakuraManager.getBackgroundColorRed(), (int) this.sakuraManager.getBackgroundColorGreen(), (int) this.sakuraManager.getBackgroundColorBlue()));

        LinearLayout glLayout						= new LinearLayout(this);
        LinearLayout.LayoutParams glViewParams		= null;
        glLayout.setBackgroundColor(Color.rgb((int)this.sakuraManager.getBackgroundColorRed(), (int)this.sakuraManager.getBackgroundColorGreen(), (int)this.sakuraManager.getBackgroundColorBlue()));

        LinearLayout adLayout 						= null;
        LinearLayout.LayoutParams adViewParams		= null;

        //---------------------------------------------------------------------
        // レイアウトパラメータの設定
        //---------------------------------------------------------------------
        frViewParams								= new LinearLayout.LayoutParams(this.sakuraManager.getFrLayoutWidth(), this.sakuraManager.getFrLayoutHeight());
        glViewParams								= new LinearLayout.LayoutParams(this.sakuraManager.getGlLayoutWidth(), this.sakuraManager.getGlLayoutHeight());
        glViewParams.topMargin						= this.sakuraManager.getGlLayoutMarginVertical();
        glViewParams.leftMargin						= this.sakuraManager.getGlLayoutMarginHorizontal();
        if (this.sakuraManager.isUseAdMob())
        {
            adViewParams							= new LinearLayout.LayoutParams(this.sakuraManager.getAdMobLayoutWidth(), this.sakuraManager.getAdMobLayoutHeight());
        }
        else
        {
            adViewParams							= null;
        }

        //---------------------------------------------------------------------
        // adMobの基本定義
        //---------------------------------------------------------------------
        if (this.sakuraManager.isUseAdMob())
        {
            // AdMob用のレイアウト定義
            adLayout = new LinearLayout(this);
            adLayout.setOrientation(LinearLayout.VERTICAL);
            adLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            adLayout.setBackgroundColor(Color.rgb(0, 0, 0));
            adLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            adLayout.setGravity(Gravity.CENTER_VERTICAL);

            // AdMobをAdMobレイアウトに追加する
            this.adView = new AdView(this);
            this.adView.setAdUnitId(this.sakuraManager.getAdUnitID());
            this.adView.setAdSize(AdSize.BANNER);
            adLayout.addView(this.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("D563BA4747D62DC4928B7BB60E17488F").build();
            this.adView.loadAd(adRequest);
        }

        //---------------------------------------------------------------------
        // GLフレームレイアウトにGLレイアウトを追加
        //---------------------------------------------------------------------
        glLayout.addView(this.sakuraManager.getSakuraView());
        frLayout.addView(glLayout, glViewParams);

        //---------------------------------------------------------------------
        // メインレイアウトにGLフレームレイアウトとadMobレイアウトを追加
        //---------------------------------------------------------------------
        if (this.sakuraManager.isUseAdMob() && this.sakuraManager.getAdMobVerticalPosition().equals(SAKURA.ADMOB_VERTICAL_POSITION.TOP))	{ mainLayout.addView(adLayout, adViewParams); }
        mainLayout.addView(frLayout, frViewParams);
        if (this.sakuraManager.isUseAdMob() && this.sakuraManager.getAdMobVerticalPosition().equals(SAKURA.ADMOB_VERTICAL_POSITION.BOTTOM)){ mainLayout.addView(adLayout, adViewParams); }

        //---------------------------------------------------------------------
        // オフセット等を求める
        //---------------------------------------------------------------------
        this.sakuraManager.setGlLayoutStartRealPositionX(this.sakuraManager.getGlLayoutMarginHorizontal());
        this.sakuraManager.setGlLayoutStartRealPositionY(this.sakuraManager.getGlLayoutMarginVertical() + (this.sakuraManager.isUseAdMob()&&this.sakuraManager.getAdMobVerticalPosition().equals(SAKURA.ADMOB_VERTICAL_POSITION.TOP)?this.sakuraManager.getAdMobLayoutHeight():1));
        this.sakuraManager.setGlLayoutEndRealPositionX(this.sakuraManager.getGlLayoutStartRealPositionX() + this.sakuraManager.getGlLayoutWidth());
        this.sakuraManager.setGlLayoutEndRealPositionY(this.sakuraManager.getGlLayoutStartRealPositionY() + this.sakuraManager.getGlLayoutHeight());

        //---------------------------------------------------------------------
        // タッチマネージャーにオフセットとスケールを登録
        //---------------------------------------------------------------------
        this.sakuraManager.getSakuraTouchManager().setScale((float)((float)this.sakuraManager.getVirtualWidth()  / (float)this.sakuraManager.getGlLayoutWidth())
                ,(float)((float)this.sakuraManager.getVirtualHeight() / (float)this.sakuraManager.getGlLayoutHeight()));

        // 作成した規定レイアウトを返却
        return mainLayout;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // 戻るキーの制御のみ各シーンのbackメソッドをコールする
        if(keyCode != KeyEvent.KEYCODE_BACK)
        {
            // 戻るキー以外の入力はそのままスルーする
            return super.onKeyDown(keyCode, event);
        }
        else
        {
            // 戻るキーを押下された場合
            if (this.sakuraManager != null)
            {
                // FS2GOManagerが定義されている場合はFS2GOManager経由で現在表示しているシーンのbackメソッドをコールする
                this.sakuraManager.onKeyBack();								// FS2GOManager経由でbackメソッドのコール
                return false;											// キー入力情報をスルーしない
            }
            else
            {
                // FS2GOManagerが定義されていない場合は通常動作とする
                return super.onKeyDown(keyCode, event);
            }
        }
    }

    /**
     * @param intent
     * @param requestCode
     */
    public void setStartActivityForResult(Intent intent, int requestCode)
    {
        startActivityForResult(intent, requestCode);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            //-----------------------------------------------------------------
            //
            // Bluetooth系
            //
            //-----------------------------------------------------------------
            // Bluetooth機器の利用の許可に入力があった場合
            case BluetoothCommunicationManager.REQUEST_ENABLE_BT:

                // Managerが存在していない場合は何もしない
                if (this.sakuraManager == null){ return; }

                // Bluetooth機器を有効にしたか否かの判定
                if (resultCode == Activity.RESULT_OK)
                {
                    // 初期化処理をキックする
                    this.sakuraManager.getBluetoothManager().innerInit();

                    // 有効にした場合、有効時のイベントが定義されている場合は、対象のイベントを起動する
                    if (this.sakuraManager.getBluetoothManager().getEnableBluetoothSuccessEvent() == null){ return; }
                    this.sakuraManager.getBluetoothManager().getEnableBluetoothSuccessEvent().event();
                }
                else
                {
                    // 無効にした場合、無効時のイベントが定義されている場合は、対象のイベントを起動する
                    if (this.sakuraManager.getBluetoothManager().getEnableBluetoothFailureEvent() == null){ return; }
                    this.sakuraManager.getBluetoothManager().getEnableBluetoothFailureEvent().event();
                }
                break;
        }
    }
}
