package android.futuresoftware.jp.speedtest.scene;

import android.futuresoftware.jp.speedtest.texture.TexGame;

import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneButtonProcessBase;

/**
 * Created by toshiyuki on 2015/04/03.
 */
public abstract class AppSceneButtonBase extends SceneButtonBase {

    public int btnBack;

    protected void createBackButton(String backSceneName) {
        // 戻るボタンの定義
        this.btnBack	= this.registButton( ((AppSceneBase)this.scene).texGame						// テクスチャID
            ,((AppSceneBase)this.scene).texGameIndex[TexGame.TEX.operationBack.ordinal()]			// 標準時画像CharacterIndex
            ,((AppSceneBase)this.scene).texGameIndex[TexGame.TEX.operationBackTouched.ordinal()]	// タッチ時画像CharacterIndex
            ,-1																					    // 無効時画像は指定しない
            ,new ButtonBackProcess(backSceneName));												    // 処理クラスインスタンス設定
    }

    /**
     *
     */
    protected class ButtonBackProcess extends SceneButtonProcessBase {

        private String backSceneName;

        public ButtonBackProcess(String backSceneName){
            this.backSceneName      = backSceneName;
        }

        @Override
        public boolean onDown(int buttonIndex){	return true; }
        @Override
        public boolean onUp(int buttonIndex){ return true; }
        @Override
        public boolean onTouch(int buttonIndex){
            sakuraManager.changeScene(this.backSceneName);
            return false;
        }
    }
}
