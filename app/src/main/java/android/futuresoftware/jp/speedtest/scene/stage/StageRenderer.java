package android.futuresoftware.jp.speedtest.scene.stage;

import android.futuresoftware.jp.speedtest.texture.TexGame;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/04/02.
 */
public class StageRenderer extends SceneRendererBase {

	// メンバ変数定義
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

		// 面選択ボタンの描画
		for (count = 0 ; count < this.btn.btnStages.length ; count++ ){
			this.drawButton(gl, this.btn.btnStages[count], false, 134 * (count % 7), 130 * (count / 7) + 14);																					// 面選択ボタンの表示
			this.drawAlphaNum(gl, this.btn.btnStageLabels[count], 15, 3, 134 * (count % 7) + 13, 130 * (count / 7) + 14 + 10);																	// 面名の表示
			this.drawAlphaNum(gl, this.btn.btnStageScores[count], 15, 1, 134 * (count % 7) + 9, 130 * (count / 7) + 14 + 103);																	// 得点の表示
			this.drawTexture(gl, this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.medalGold.ordinal()], true, 134 * (count % 7) + (128 / 2), 130 * (count / 7) + (128 / 2) + 14, 60, 60);	// ランクの表示
		}

		// エリアの表示
		this.drawAlphaNum(gl, this.scen.selectedAreaLabel, 32, 3, 375, this.sakuraManager.getVirtualHeight() - 80 - 15 + 10);

		// 戻るボタンの描画
		this.drawButton(gl, this.btn.btnBack, false, 10, this.sakuraManager.getVirtualHeight() - 80 - 15, 80, 80);
	}

	@Override
	public void term(GL10 gl) {

	}
}
