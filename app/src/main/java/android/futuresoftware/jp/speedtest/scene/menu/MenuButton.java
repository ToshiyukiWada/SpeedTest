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

    public int btnMenus[];      // メニューボタン
    public int btnShop;         // ショップボタン
    public int btnWebsite;      // WEBサイトボタン

	/**
	 *
	 */
    @Override
    public void init() {
        this.scen           = (MenuScene)this.scene;
        this.btnMenus       = new int[5];
    }

	/**
	 *
	 */
    @Override
    public void doButton() {
        this.btnMenus[0]        = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());       // Menu1つ目のボタン
        this.btnMenus[1]        = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());       // Menu2つ目のボタン
        this.btnMenus[2]        = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());       // Menu3つ目のボタン
        this.btnMenus[3]        = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());       // Menu4つ目のボタン
        this.btnMenus[4]        = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameTouched.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrameDisabled.ordinal()], new ButtonMenuProcess());       // Menu5つ目のボタン

        this.btnShop            = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], new ButtonShopProcess());       // ショップボタン
        this.btnWebsite         = this.registButton(this.scen.texGame, this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], this.scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], new ButtonWebsiteProcess());	// WEBサイトへの誘導ボタン
    }

	/**
	 *
	 */
    private class ButtonMenuProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(){
			Log.i("TOUCH!", "ButtonMenuProcess::onDown");
			return true;
		}
		@Override
		public boolean onUp(){
			Log.i("TOUCH!", "ButtonMenuProcess::onUp");
			return true;
		}
		@Override
		public boolean onTouch() {
			// どのボタンが押下されたかを判断
			Log.i("TOUCH!", "ButtonMenuProcess::onTouch");
			return true;
		}
	}

	/**
	 *
	 */
	private class ButtonShopProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown(){
			Log.i("TOUCH!", "ButtonShopProcess::onDown");
			return true;
		}
		@Override
		public boolean onUp(){
			Log.i("TOUCH!", "ButtonShopProcess::onUp");
			return true;
		}
		@Override
		public boolean onTouch(){
			Log.i("TOUCH!", "ButtonShopProcess::onTouch");
			return true;
		}
	}

	/**
	 *
	 */
	private class ButtonWebsiteProcess extends SceneButtonProcessBase {
		@Override
		public boolean onDown() { return true; }
		@Override
		public boolean onUp() { return true; }
		@Override
		public boolean onTouch() {
			return true;
		}
	}
}
