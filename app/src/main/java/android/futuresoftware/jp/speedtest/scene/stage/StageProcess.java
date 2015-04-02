package android.futuresoftware.jp.speedtest.scene.stage;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/04/02.
 */
public class StageProcess extends SceneProcessBase {
	@Override
	public void back() {
		this.sakuraManager.changeScene("MENU");
	}

	@Override
	public void init() {

	}

	@Override
	public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {

	}

	@Override
	public void term() {

	}
}
