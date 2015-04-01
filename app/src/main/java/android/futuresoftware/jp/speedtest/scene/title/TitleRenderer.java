package android.futuresoftware.jp.speedtest.scene.title;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class TitleRenderer extends SceneRendererBase {

    private TitleScene scen;
    private TitleProcess proc;
    private int count;

    @Override
    public void init(GL10 gl) {
        scen		= (TitleScene)this.scene;
        proc        = (TitleProcess)this.process;
    }

    @Override
    public void draw(GL10 gl, float frametime) {
        // タイトル
        this.drawTexture(gl, scen.texIDHuman, 8, true, this.sakuraManager.getVirtualWidth() / 2, (int)(this.sakuraManager.getVirtualHeight() / 3.5f));

        // キャラクターの描画
        this.burstTexture(scen.texIDHuman, (int) proc.humanAnimation, true, this.sakuraManager.getVirtualWidth() / 2, (int) (this.sakuraManager.getVirtualHeight() / 1.5f), 100, 93, 148);
        this.burstTextureRenderer(gl, scen.texIDHuman);

        // 道の描画
        for(count = 0 ; count < this.sakuraManager.getVirtualWidth() ; count++) {
            this.burstTexture(scen.texIDStreet, 0, true, count, 433, 100, 5, 5);
        }
        this.burstTextureRenderer(gl, scen.texIDStreet);

        // ロゴの描画
        this.drawTexture(gl, scen.texIDFslogo, 0, true, this.sakuraManager.getVirtualWidth() / 2, (int)(this.sakuraManager.getVirtualHeight() / 1.07f), 210, 43);
    }

    @Override
    public void term(GL10 gl) {

    }
}
