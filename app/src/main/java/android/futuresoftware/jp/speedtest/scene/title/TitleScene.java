package android.futuresoftware.jp.speedtest.scene.title;

import android.futuresoftware.jp.speedtest.R;

import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class TitleScene extends SceneBase {

    public int texIDHuman;

    public TitleScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase)
    {
        super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
    }

    @Override
    public void init() {
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.human), Integer.toString(R.raw.human), "20");
    }

    @Override
    public void initCallback() {
        this.texIDHuman = this.sakuraManager.getTextureID(R.drawable.human, R.raw.human);
    }

    @Override
    public void terminate() {

    }

    @Override
    public void terminateCallback() {

    }
}
