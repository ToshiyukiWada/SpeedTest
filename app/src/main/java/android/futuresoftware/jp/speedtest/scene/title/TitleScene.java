package android.futuresoftware.jp.speedtest.scene.title;

import android.futuresoftware.jp.speedtest.scene.AppSceneBase;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class TitleScene extends AppSceneBase {

	/**
	 *
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
    public TitleScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase)
    {
        super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
    }

	/**
	 *
	 */
    @Override
    public void initCallback() {
		this.initCallbackCommon();
    }
}
