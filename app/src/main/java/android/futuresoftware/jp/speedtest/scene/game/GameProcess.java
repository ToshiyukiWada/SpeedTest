package android.futuresoftware.jp.speedtest.scene.game;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class GameProcess extends SceneProcessBase {

    public float time;
    public float playerSpeed;
    public float playerAnimationCounter;

    @Override
    public void back() {
		this.sakuraManager.changeScene("STAGE");
    }

    @Override
    public void init() {
        this.time               = 0.0f;
        this.playerSpeed        = 1.0f;
    }

    @Override
    public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {
        for (touchCount = 0 ; touchCount < touchEvents.size() ; touchCount++){
            if (touchEvents.get(touchCount).x <= this.sakuraManager.getVirtualWidth() / 2)  {this.playerSpeed -= 0.1;}
            else                                                                            {this.playerSpeed += 0.1;}
        }

        // タイム加算
        this.time += frametime;

        // プレイヤーアニメーション
        this.playerAnimationCounter += (frametime * this.playerSpeed);
        if (this.playerAnimationCounter > 7.0f){ this.playerAnimationCounter -= 7.0f; }
    }

    @Override
    public void term() {
    }
}
