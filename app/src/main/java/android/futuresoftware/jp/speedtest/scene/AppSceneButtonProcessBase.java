package android.futuresoftware.jp.speedtest.scene;

import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/04/03.
 */
public abstract class AppSceneButtonProcessBase extends SceneButtonProcessBase {
    @Override
    public boolean onDown(int buttonIndex){
        return true;
    }
    @Override
    public boolean onUp(int buttonIndex){
        return true;
    }
}
