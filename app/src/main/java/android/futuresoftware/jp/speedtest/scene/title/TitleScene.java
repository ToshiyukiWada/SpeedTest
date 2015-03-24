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

    public int texIDStreet;
    public int texIDHuman;
    public int texIDFslogo;

    public TitleScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase)
    {
        super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
    }

    @Override
    public void init() {
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.street), Integer.toString(R.raw.street), "2000");
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.human), Integer.toString(R.raw.human), "20");
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.fslogo), Integer.toString(R.raw.fslogo));
    }

    @Override
    public void initCallback() {
        this.texIDStreet	= this.sakuraManager.getTextureID(R.drawable.street, R.raw.street);
        this.texIDHuman     = this.sakuraManager.getTextureID(R.drawable.human, R.raw.human);
        this.texIDFslogo	= this.sakuraManager.getTextureID(R.drawable.fslogo, R.raw.fslogo);
    }

    @Override
    public void terminate() {

    }

    @Override
    public void terminateCallback() {

    }
}
