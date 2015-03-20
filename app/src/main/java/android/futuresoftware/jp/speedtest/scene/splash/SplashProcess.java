package android.futuresoftware.jp.speedtest.scene.splash;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class SplashProcess extends SceneProcessBase {

    public float timer      = 0.0f;

    @Override
    public void back() {
        this.sakuraManager.finish();
    }

    @Override
    public void init() {

    }

    @Override
    public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {

        for(SakuraTouchManager.TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type == SakuraTouchManager.TouchEvent.TOUCH_DOWN)
            {
                if (timer < 5.0){ timer = 5.0f; }
                else            { timer = 10.0f; }
            }
        }

        timer       += frametime;
        if (timer > 10.0f){ this.sakuraManager.changeScene("TITLE"); }
    }

    @Override
    public void term() {

    }
}
