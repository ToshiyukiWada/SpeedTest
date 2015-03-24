package android.futuresoftware.jp.speedtest.scene.title;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class TitleProcess extends SceneProcessBase {

    public float transitTime;
    public float humanAnimation;

    @Override
    public void back() {
        this.sakuraManager.finish();
    }

    @Override
    public void init() {
        this.transitTime = 0.0f;
        this.humanAnimation = 0.0f;
    }

    @Override
    public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {

        this.transitTime    += frametime;
        this.humanAnimation += (frametime * 12);
        if (this.humanAnimation > 7.0f){ this.humanAnimation -= 7.0f; }
    }

    @Override
    public void term() {

    }
}
