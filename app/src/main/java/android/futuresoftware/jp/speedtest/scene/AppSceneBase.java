package android.futuresoftware.jp.speedtest.scene;

import android.futuresoftware.jp.speedtest.R;
import android.futuresoftware.jp.speedtest.texture.TexFslogo;
import android.futuresoftware.jp.speedtest.texture.TexGame;

import java.util.EnumSet;

import jp.futuresoftware.android.sakura.base.SceneBase;
import jp.futuresoftware.android.sakura.base.SceneButtonBase;
import jp.futuresoftware.android.sakura.base.SceneProcessBase;
import jp.futuresoftware.android.sakura.base.SceneRendererBase;
import jp.futuresoftware.android.sakura.texture.TextureManager;

/**
 * Created by toshiyuki on 2015/04/03.
 */
public abstract class AppSceneBase extends SceneBase {

	public int texFslogo;
	public int[] texFslogoIndex;

	public int texGame;
	public int[] texGameIndex;

	/**
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
	public AppSceneBase(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase) {
		super(sceneName, sceneRendererBase, sceneProcessBase, sceneButtonBase);
	}

	/**
	 *
	 */
	@Override
	public void init() {
		addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.fslogo), Integer.toString(R.raw.fslogo));
		addQueue("LoadTextureFromResource" ,Integer.toString(R.drawable.game), Integer.toString(R.raw.game), "2000");
	}

	protected void initCallbackCommon(){
		this.texFslogo	= this.sakuraManager.getTextureID(R.drawable.fslogo, R.raw.fslogo);
		TextureManager.characterName2Index(this, this.texFslogo, "texFslogoIndex", EnumSet.allOf(TexFslogo.TEX.class));

		this.texGame	= this.sakuraManager.getTextureID(R.drawable.game, R.raw.game);
		TextureManager.characterName2Index(this, this.texGame  , "texGameIndex"  , EnumSet.allOf(TexGame.TEX.class));
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
