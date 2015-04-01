package android.futuresoftware.jp.speedtest.scene.menu;

import android.futuresoftware.jp.speedtest.texture.TexGame;
import android.util.Log;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/03/30.
 */
public class MenuButton extends SceneButtonBase {

	// メンバ変数定義
    private MenuScene scen;     // 対象シーンクラス

	public int count;
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
		for (count = 0 ; count < this.btnMenus.length ; count++) {
			this.btnMenus[count] = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());
		}

        this.btnShop            = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.buttonFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.buttonFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.buttonFrameDisabled.ordinal()], new ButtonShopProcess());       // ショップボタン
        this.btnWebsite         = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.buttonFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.buttonFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.buttonFrameDisabled.ordinal()], new ButtonWebsiteProcess());	// WEBサイトへの誘導ボタン
    }

	/**
	 *
	 */
    private class ButtonMenuProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(int buttonIndex){
			Log.i("TOUCH!", "ButtonMenuProcess::onDown[" + buttonIndex + "]");
			return true;
		}
		@Override
		public boolean onUp(int buttonIndex){
			Log.i("TOUCH!", "ButtonMenuProcess::onUp[" + buttonIndex + "]");
			return true;
		}
		@Override
		public boolean onTouch(int buttonIndex) {
			// どのボタンが押下されたかを判断
			Log.i("TOUCH!", "ButtonMenuProcess::onTouch[" + buttonIndex + "]");
			return true;
		}
	}

	/**
	 *
	 */
	private class ButtonShopProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(int buttonIndex){
			Log.i("TOUCH!", "ButtonShopProcess::onDown[" + buttonIndex + "]");
			return true;
		}
		@Override
		public boolean onUp(int buttonIndex){
			Log.i("TOUCH!", "ButtonShopProcess::onUp[" + buttonIndex + "]");
			return true;
		}
		@Override
		public boolean onTouch(int buttonIndex){
			Log.i("TOUCH!", "ButtonShopProcess::onTouch[" + buttonIndex + "]");
			return true;
		}
	}

	/**
	 *
	 */
	private class ButtonWebsiteProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(int buttonIndex) { return true; }
		@Override
		public boolean onUp(int buttonIndex) { return true; }
		@Override
		public boolean onTouch(int buttonIndex) {
			return true;
		}
	}
}
