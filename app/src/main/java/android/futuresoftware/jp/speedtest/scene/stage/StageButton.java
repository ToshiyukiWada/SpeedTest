package android.futuresoftware.jp.speedtest.scene.stage;

import android.futuresoftware.jp.speedtest.texture.TexGame;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/04/02.
 */
public class StageButton extends SceneButtonBase {

	// メンバ変数定義
	private StageScene scen;     // 対象シーンクラス

	public int count;			// 汎用カウンタ
	public int btnStages[];     // ステージボタン

	@Override
	public void init() {
		this.scen           = (StageScene)this.scene;
		this.btnStages      = new int[21];
	}

	@Override
	public void doButton() {
		// メニューボタン作成(繰り返し作成する)
		for (count = 0 ; count < this.btnStages.length ; count++) {
			this.btnStages[count] = this.registButton(this.scen.texGame                         // テクスチャID
					, this.scen.texGameIndex[TexGame.TEX.stageButtonFrame.ordinal()]            // 標準時画像CharacterIndex
					, this.scen.texGameIndex[TexGame.TEX.stageButtonFrameTouched.ordinal()]     // タッチ時画像CharacterIndex
					, this.scen.texGameIndex[TexGame.TEX.stageButtonFrameDisabled.ordinal()]    // 無効時画像CharacterIndex
					, new ButtonStageProcess());												// 処理クラスインスタンス設定
		}
	}

	/**
	 * ステージボタン押下処理クラス
	 */
	private class ButtonStageProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(int buttonIndex){
			return true;
		}
		@Override
		public boolean onUp(int buttonIndex){
			return true;
		}
		@Override
		public boolean onTouch(int buttonIndex){
			return true;
		}
	}
}
