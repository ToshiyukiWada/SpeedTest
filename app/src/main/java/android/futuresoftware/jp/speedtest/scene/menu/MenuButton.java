package android.futuresoftware.jp.speedtest.scene.menu;

import android.futuresoftware.jp.speedtest.scene.AppSceneButtonBase;
import android.futuresoftware.jp.speedtest.texture.TexGame;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/03/30.
 */
public class MenuButton extends AppSceneButtonBase {

	// メンバ変数定義
    private MenuScene scen;     // 対象シーンクラス

	public int count;			// 汎用カウンタ
    public int btnMenus[];      // メニューボタン
    public int btnShop;         // ショップボタン
    public int btnWebsite;      // WEBサイトボタン

	/**
	 *
	 */
    @Override
    public void init() {
        this.scen           = (MenuScene)this.scene;
        this.btnMenus       = new int[20];
    }

	/**
	 *
	 */
    @Override
    public void doButton() {

		// 戻るボタン定義
		this.createBackButton("TITLE");

		// メニューボタン作成(繰り返し作成する)
		for (count = 0 ; count < this.btnMenus.length ; count++) {
			this.btnMenus[count] = this.registButton(this.scen.texGame							// テクスチャID
				,this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()]						// 標準時画像CharacterIndex
				,this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()]					// タッチ時画像CharacterIndex
				,this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()]				// 無効時画像CharacterIndex
				,new ButtonMenuProcess());														// 処理クラスインスタンス設定
		}

		// ショップボタン作成
        this.btnShop            = this.registButton( this.scen.texGame							// テクスチャID
			,this.scen.texGameIndex[TexGame.TEX.buttonFrame.ordinal()]							// 標準時画像CharacterIndex
			,this.scen.texGameIndex[TexGame.TEX.buttonFrameTouched.ordinal()]					// タッチ時画像CharacterIndex
			,this.scen.texGameIndex[TexGame.TEX.buttonFrameDisabled.ordinal()]					// 無効時画像CharacterIndex
			,new ButtonShopProcess());															// 処理クラスインスタンス設定

		// WEBサイトボタン作成
        this.btnWebsite         = this.registButton( this.scen.texGame							// テクスチャID
			,this.scen.texGameIndex[TexGame.TEX.buttonFrame.ordinal()]							// 標準時画像CharacterIndex
			,this.scen.texGameIndex[TexGame.TEX.buttonFrameTouched.ordinal()]					// タッチ時画像CharacterIndex
			,this.scen.texGameIndex[TexGame.TEX.buttonFrameDisabled.ordinal()]					// 無効時画像CharacterIndex
			,new ButtonWebsiteProcess());														// 処理クラスインスタンス設定
    }

	/**
	 * メニューボタン押下処理クラス
	 */
    private class ButtonMenuProcess extends SceneButtonProcessBase {
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
			// どのエリアを押下されたかを判定する
			int selectedArea		= -1;
			for (count = 0 ; count < btnMenus.length ; count++)	{
				if (btnMenus[count] == buttonIndex){ selectedArea = count; }
			}
			sakuraManager.setVariable("selectedArea", new Integer(selectedArea), true);

			// 画面切り替え
			sakuraManager.changeScene("STAGE");

			// 以降のイベントは伝達しない
			return false;
		}
	}

	/**
	 * ショップボタン押下処理クラス
	 */
	private class ButtonShopProcess extends SceneButtonProcessBase {
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
			sakuraManager.changeScene("SHOP");
			return false;
		}
	}

	/**
	 * WEBサイトボタン押下処理クラス
	 */
	private class ButtonWebsiteProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(int buttonIndex){
			return true;
		}
		@Override
		public boolean onUp(int buttonIndex){
			return true;
		}
		@Override
		public boolean onTouch(int buttonIndex) {
			sakuraManager.setVariable("visitFrequency", (Integer) sakuraManager.getVariable("visitFrequency", new Integer(0)) + 1, true);
			return true;
		}
	}
}
