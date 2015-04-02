package android.futuresoftware.jp.speedtest.scene.menu;

import android.futuresoftware.jp.speedtest.R;
import android.futuresoftware.jp.speedtest.texture.TexGame;

import java.util.EnumSet;

import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;
import jp.futuresoftware.android.sakura.texture.TextureManager;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class MenuScene extends SceneBase {

	// メンバ変数定義
    public int texGame;						// テクスチャーハンドラー
    public int[] texGameIndex;				// テクスチャーインデックス

	/**
	 *
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
    public MenuScene(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase) {
        super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
    }

	/**
	 *
	 */
    @Override
    public void init() {
        addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.game), Integer.toString(R.raw.game));
    }

	/**
	 *
	 */
    @Override
    public void initCallback() {
        this.texGame  = this.sakuraManager.getTextureID(R.drawable.game, R.raw.game);
        TextureManager.characterName2Index(this,this.texGame,"texGameIndex", EnumSet.allOf(TexGame.TEX.class));
    }

	/**
	 *
	 */
    @Override
    public void terminate() {

    }

	/**
	 *
	 */
    @Override
    public void terminateCallback() {

    }
}
