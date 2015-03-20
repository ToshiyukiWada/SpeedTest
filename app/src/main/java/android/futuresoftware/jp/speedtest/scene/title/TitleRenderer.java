package android.futuresoftware.jp.speedtest.scene.title;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class TitleRenderer extends SceneRendererBase {

    private TitleScene scen;
    private TitleProcess proc;

    @Override
    public void init(GL10 gl) {
        scen		= (TitleScene)this.scene;
        proc        = (TitleProcess)this.process;
    }

    @Override
    public void draw(GL10 gl, float frametime) {
        // キャラクターの描画
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, true, this.sakuraManager.getVirtualWidth() / 2, (int)(this.sakuraManager.getVirtualHeight() / 1.5f), 100, 93, 148);
        this.burstTextureRenderer(gl, scen.texIDHuman);
    }

    @Override
    public void term(GL10 gl) {

    }
}
