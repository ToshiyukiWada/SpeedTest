package android.futuresoftware.jp.speedtest.scene.menu;

import android.graphics.Point;

import java.util.List;

import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.core.SakuraTouchManager;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class MenuProcess extends SceneProcessBase {

    public Point touchStartPos;
    public int menuScrollStartPos;

    @Override
    public void back() {
        this.sakuraManager.changeScene("TITLE");
    }

    @Override
    public void init() {
        this.menuScrollStartPos = 0;
        this.touchStartPos      = new Point(-1, -1);
    }

    @Override
    public void process(float frametime, List<SakuraTouchManager.TouchEvent> touchEvents) {

		//---------------------------------------------------------------------
		// タッチ処理解析
		//---------------------------------------------------------------------
        for (touchCount = 0 ; touchCount < touchEvents.size() ; touchCount++){

            // メニュー横スクロール処理
			if (touchEvents.get(touchCount).pointer == 0) {
				if (touchEvents.get(touchCount).type == SakuraTouchManager.TouchEvent.TOUCH_DOWN) {
					this.touchStartPos.set(touchEvents.get(touchCount).x, touchEvents.get(touchCount).y);
				} else if (touchEvents.get(touchCount).type == SakuraTouchManager.TouchEvent.TOUCH_DRAGGED) {
					if (this.touchStartPos.x != -1 && this.touchStartPos.y != -1) {
						this.menuScrollStartPos -= this.touchStartPos.x - touchEvents.get(touchCount).x;
						this.touchStartPos.set(touchEvents.get(touchCount).x, touchEvents.get(touchCount).y);
					}
				} else if (touchEvents.get(touchCount).type == SakuraTouchManager.TouchEvent.TOUCH_UP) {
					this.touchStartPos.set(-1, -1);
				}
			}
        }


    }

    @Override
    public void term() {

    }
}
