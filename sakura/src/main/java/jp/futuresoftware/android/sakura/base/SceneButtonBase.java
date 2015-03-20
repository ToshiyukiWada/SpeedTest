package jp.futuresoftware.android.sakura.base;

public abstract class SceneButtonBase extends SakuraBase
{
	protected SceneBase scene;
	protected SceneProcessBase process;

	public void setScene(SceneBase scene)				{ this.scene	= scene; }
	public void setProcess(SceneProcessBase process)	{ this.process	= process; }
	
	public abstract void doButton();
}
