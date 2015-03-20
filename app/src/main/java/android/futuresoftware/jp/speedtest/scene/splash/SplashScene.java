package android.futuresoftware.jp.speedtest.scene.splash;

import android.futuresoftware.jp.speedtest.R;

import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class SplashScene extends SceneBase {

    public int texIDFslogo;

    public SplashScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase)
    {
        super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
    }

    @Override
    public void init() {
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.fslogo), Integer.toString(R.raw.fslogo));
    }

    @Override
    public void initCallback() {
        this.texIDFslogo	= this.sakuraManager.getTextureID(R.drawable.fslogo, R.raw.fslogo);
    }

    @Override
    public void terminate() {
    }

    @Override
    public void terminateCallback() {

    }
}
