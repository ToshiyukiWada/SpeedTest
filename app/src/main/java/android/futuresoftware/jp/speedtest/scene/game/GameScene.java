package android.futuresoftware.jp.speedtest.scene.game;

import android.futuresoftware.jp.speedtest.scene.AppSceneBase;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class GameScene extends AppSceneBase {

	// メンバ変数定義
	public int selectedArea;
	public String selectedAreaLabel;

	public int selectedStage;
	public String selectedStageLabel;

	/**
	 *
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
	public GameScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase) {
		super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
	}

	/**
	 *
	 */
	@Override
	public void initCallback() {
		this.initCallbackCommon();
		this.selectedArea			= (Integer) this.sakuraManager.getVariable("selectedArea", new Integer(1));
		this.selectedStage			= (Integer) this.sakuraManager.getVariable("selectedStage", new Integer(1));
		this.selectedAreaLabel		= "AREA" + (this.selectedArea<10?"0"+(this.selectedArea):(this.selectedArea));
		this.selectedStageLabel		= "STAGE" + (this.selectedStage<10?"0"+(this.selectedStage):(this.selectedStage));
	}
}
