package android.futuresoftware.jp.speedtest.scene.shop;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class ShopRenderer extends SceneRendererBase {
    @Override
    public void init(GL10 gl) {

    }

    @Override
    public void draw(GL10 gl, float frametime) {
		this.drawAlphaNum(gl, "SHOP", 64, 2, 0, 0);
    }

    @Override
    public void term(GL10 gl) {

    }
}
