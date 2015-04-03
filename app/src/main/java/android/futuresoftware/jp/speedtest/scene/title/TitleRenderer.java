package android.futuresoftware.jp.speedtest.scene.title;

import android.futuresoftware.jp.speedtest.texture.TexFslogo;
import android.futuresoftware.jp.speedtest.texture.TexGame;

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
        this.drawTexture(gl, scen.texGame, scen.texGameIndex[TexGame.TEX.title.ordinal()], true, this.sakuraManager.getVirtualWidth() / 2, (int)(this.sakuraManager.getVirtualHeight() / 3.5f));

        // キャラクターの描画
        this.burstTexture(scen.texGame, scen.texGameIndex[TexGame.TEX.human1.ordinal()] + (int) proc.humanAnimation, true, this.sakuraManager.getVirtualWidth() / 2, (int) (this.sakuraManager.getVirtualHeight() / 1.5f), 100, 93, 148);

        // 道の描画
        for(count = 0 ; count < this.sakuraManager.getVirtualWidth() ; count++) {
            this.burstTexture(scen.texGame, scen.texGameIndex[TexGame.TEX.ground.ordinal()], true, count, 433, 100, 5, 5);
        }
        this.burstTextureRenderer(gl, scen.texGame);

        // ロゴの描画
        this.drawTexture(gl, scen.texFslogo, scen.texGameIndex[TexFslogo.TEX.fslogo.ordinal()], true, this.sakuraManager.getVirtualWidth() / 2, (int)(this.sakuraManager.getVirtualHeight() / 1.07f), 210, 43);
    }

    @Override
    public void term(GL10 gl) {

    }
}
