package android.futuresoftware.jp.speedtest.scene.shop;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class ShopProcess extends SceneProcessBase {
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
