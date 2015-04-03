package android.futuresoftware.jp.speedtest.scene.game;

import android.futuresoftware.jp.speedtest.texture.TexGame;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class GameRenderer extends SceneRendererBase {

	// メンバ変数定義
	private GameScene scen;
	private GameProcess proc;
	private GameButton btn;
	private int count;

	@Override
    public void init(GL10 gl) {
		scen		= (GameScene)this.scene;
		proc        = (GameProcess)this.process;
		btn			= (GameButton)this.button;
    }

    @Override
    public void draw(GL10 gl, float frametime) {

		// キャラクターの描画
		this.burstTexture(scen.texGame, scen.texGameIndex[TexGame.TEX.human1.ordinal()], true, this.sakuraManager.getVirtualWidth() / 2, (int) (this.sakuraManager.getVirtualHeight() / 1.5f), 100, 93, 148);

		// 道の描画
		for(count = 0 ; count < this.sakuraManager.getVirtualWidth() ; count++) {
			this.burstTexture(scen.texGame, scen.texGameIndex[TexGame.TEX.ground.ordinal()], true, count, 433, 100, 5, 5);
		}
		this.burstTextureRenderer(gl, scen.texGame);

		// エリアの表示
		this.drawAlphaNum(gl, "AREA" + (this.scen.selectedArea+1<10?"0"+(this.scen.selectedArea+1):(this.scen.selectedArea+1)), 32, 3, 375, this.sakuraManager.getVirtualHeight() - 80 - 15 + 10);
		this.drawAlphaNum(gl, "STAGE" + (this.scen.selectedStage+1<10?"0"+(this.scen.selectedStage+1):(this.scen.selectedStage+1)), 32, 3, 358, this.sakuraManager.getVirtualHeight() - 80 - 15 + 50);
    }

    @Override
    public void term(GL10 gl) {

    }
}
