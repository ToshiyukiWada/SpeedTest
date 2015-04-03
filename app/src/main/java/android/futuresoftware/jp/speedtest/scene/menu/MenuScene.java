package android.futuresoftware.jp.speedtest.scene.menu;

import android.futuresoftware.jp.speedtest.scene.AppSceneBase;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class MenuScene extends AppSceneBase {

	/**
	 *
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
    public MenuScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase) {
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
