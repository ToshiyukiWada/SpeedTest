package jp.futuresoftware.android.sakura.base;

public abstract class SceneButtonBase extends SakuraBase
{
	protected SceneBase scene;                      // 親シーン
    protected SceneRendererBase renderer;           // 対象レンダラー
	protected SceneProcessBase process;             // 対象プロセス

	public void setScene(SceneBase scene)				    { this.scene	= scene; }      // シーンの設定
    public void setRenderer(SceneRendererBase renderere)    { this.renderer = renderer;}    // レンダラーの設定
	public void setProcess(SceneProcessBase process)	    { this.process	= process; }    // プロセスの設定

    public abstract void init();
	public abstract void doButton();

	/**
	 *
	 * @param textureID
	 * @param normalCharacterIndex
	 * @param touchCharacterIndex
	 * @param disableCharacterIndex
	 * @param sceneButtonProcessBase
	 * @return
	 */
	protected int registButton(int textureID, int normalCharacterIndex, int touchCharacterIndex, int disableCharacterIndex, SceneButtonProcessBase sceneButtonProcessBase)
	{
		return this.sakuraManager.addButton(textureID, normalCharacterIndex, touchCharacterIndex, disableCharacterIndex, sceneButtonProcessBase, this.sakuraManager.getTextures().get(textureID).getCharacter(normalCharacterIndex).getWidth(), this.sakuraManager.getTextures().get(textureID).getCharacter(normalCharacterIndex).getHeight());
	}
}
