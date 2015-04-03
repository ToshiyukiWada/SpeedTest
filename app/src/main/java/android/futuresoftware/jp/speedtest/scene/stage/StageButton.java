package android.futuresoftware.jp.speedtest.scene.stage;

import android.futuresoftware.jp.speedtest.scene.AppSceneButtonBase;
import android.futuresoftware.jp.speedtest.scene.AppSceneButtonProcessBase;
import android.futuresoftware.jp.speedtest.texture.TexGame;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/04/02.
 */
public class StageButton extends AppSceneButtonBase {

	// メンバ変数定義
	private StageScene scen;    	// 対象シーンクラス

	public int count;				// 汎用カウンタ

	public int[] btnStages;     	// ステージボタン
	public String[] btnStageLabels;	// ステージボタンラベル
	public String[] btnStageScores;	// ステージボタンスコアラベル

	/**
	 *
	 */
	@Override
	public void init() {
		this.scen           = (StageScene)this.scene;
		this.btnStages      = new int[21];
		this.btnStageLabels	= new String[this.btnStages.length];
		this.btnStageScores = new String[this.btnStages.length];

		for (count = 0 ; count < this.btnStages.length ; count++)
		{
			this.btnStageLabels[count]		= "AREA" + (count+1<10?"0"+(count+1):(count+1));
			this.btnStageScores[count]		= "123456";
		}
	}

	/**
	 *
	 */
	@Override
	public void doButton() {

		// 戻るボタン定義
		this.createBackButton("MENU");

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
	private class ButtonStageProcess extends AppSceneButtonProcessBase {

		@Override
		public boolean onTouch(int buttonIndex){

			// どのステージを押下されたかを判定する
			int selectedStage		= -1;
			for (count = 0 ; count < btnStages.length ; count++)	{
				if (btnStages[count] == buttonIndex){ selectedStage = count; }
			}
			sakuraManager.setVariable("selectedStage", new Integer(selectedStage), true);

			// 画面切り替え
			sakuraManager.changeScene("GAME");

			// 以降のイベントは伝達しない
			return false;
		}
	}
}
