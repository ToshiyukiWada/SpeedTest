package android.futuresoftware.jp.speedtest.scene.stage;

import android.futuresoftware.jp.speedtest.texture.TexGame;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/04/02.
 */
public class StageRenderer extends SceneRendererBase {

	private StageScene scen;
	private StageProcess proc;
	private StageButton btn;
	private int count;

	@Override
	public void init(GL10 gl) {
		scen		= (StageScene)this.scene;
		proc        = (StageProcess)this.process;
		btn			= (StageButton)this.button;
	}

	@Override
	public void draw(GL10 gl, float frametime) {
		for (count = 0 ; count < this.btn.btnStages.length ; count++ ){
			this.drawButton(gl, this.btn.btnStages[count], false, 134 * (count % 7), 130 * (count / 7) + 14);
			this.drawAlphaNum(gl, "STAGE" + (count+1<10?"0"+(count+1):(count+1)), 15, 3, 134 * (count % 7) + 13, 130 * (count / 7) + 14 + 15);
			this.drawAlphaNum(gl, "623456", 15, 1, 134 * (count % 7) + 9, 130 * (count / 7) + 14 + 103);
		}

		this.drawTexture(gl, scen.texGame, scen.texGameIndex[TexGame.TEX.operationBack.ordinal()], false, 10, this.sakuraManager.getVirtualHeight() - 80 - 15, 80, 80);
	}

	@Override
	public void term(GL10 gl) {

	}
}
