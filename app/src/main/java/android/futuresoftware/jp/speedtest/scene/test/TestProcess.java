package android.futuresoftware.jp.speedtest.scene.test;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class TestProcess extends SceneProcessBase
{
    public float startPosY;
    public boolean isAddPlus;
    public float humanAnimation;

    @Override
    public void back() {

    }

    @Override
    public void init() {
        this.startPosY = 0.0f;
        this.isAddPlus = true;
        this.humanAnimation = 0.0f;
    }

    @Override
    public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {
        if (this.isAddPlus == true) {
            this.startPosY += (frametime * 100);
            if (this.startPosY > 200.0f){ this.isAddPlus = false; }
        }
        else
        {
            this.startPosY -= (frametime * 100);
            if (this.startPosY < -200.0f){ this.isAddPlus = true; }
        }

        // this.humanAnimation += (frametime * 50);
        this.humanAnimation += (frametime * 15);
        if (this.humanAnimation > 7.0f){ this.humanAnimation -= 7.0f; }
    }

    @Override
    public void term() {

    }
}
