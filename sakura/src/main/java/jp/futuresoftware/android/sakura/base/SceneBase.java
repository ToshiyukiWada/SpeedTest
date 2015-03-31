package jp.futuresoftware.android.sakura.base;

import java.lang.reflect.Constructor;
import java.util.UUID;

import jp.futuresoftware.android.sakura.core.ThreadQueueThread;
import jp.futuresoftware.android.sakura.queue.base.ThreadQueueBase;

public abstract class SceneBase extends SakuraBase
{
	private String sceneID;									// シーンID
	private String sceneName;								// シーン名
	
	private SceneRendererBase sceneRendererBase;			// レンダラー
	private SceneProcessBase sceneProcessBase;				// プロセス
	private SceneButtonBase sceneButtonBase;				// ボタンイベント

	/**
	 *
	 * @param sceneName
	 * @param sceneRendererBase
	 * @param sceneProcessBase
	 * @param sceneButtonBase
	 */
	public SceneBase(String sceneName, SceneRendererBase sceneRendererBase, SceneProcessBase sceneProcessBase, SceneButtonBase sceneButtonBase)
	{
		this.sceneID			= UUID.randomUUID().toString();
		this.sceneName			= sceneName;
		this.sceneRendererBase	= sceneRendererBase;
		this.sceneProcessBase	= sceneProcessBase;
		this.sceneButtonBase	= sceneButtonBase;
	}

	/**
	 * @return
	 */
	public String getSceneID() {
		return sceneID;
	}

	/**
	 * @return
	 */
	public String getSceneName() {
		return sceneName;
	}

	/**
	 * @return
	 */
	public SceneRendererBase getSceneRenderer() {
		return sceneRendererBase;
	}

	/**
	 * @return
	 */
	public SceneProcessBase getSceneProcess() {
		return sceneProcessBase;
	}

	/**
	 * @return
	 */
	public SceneButtonBase getSceneButton() {
		return sceneButtonBase;
	}
	
	public abstract void init();				// シーン開始時にコールされる処理
	public abstract void initCallback();		// シーン開始時にコールされる処理のコールバック
	
	public abstract void terminate();			// シーン終了時にコールされる処理
	public abstract void terminateCallback();	// シーン終了時にコールされる処理のコールバック
	
	protected void addQueue(String queueClassName, String... args)
	{
		try
		{
			// WaitBaseクラスを取得
			Class<?> waitClass = null;
			try{ waitClass = Class.forName("jp.futuresoftware.android.sakura.queue." + queueClassName); }
			catch(Exception exp){ waitClass = null;}
			if (waitClass == null){ waitClass = Class.forName(queueClassName); }
			
			// WaitBaseクラスのインスタンスを生成する
			if (waitClass != null)
			{
				@SuppressWarnings("unchecked")
				Constructor<ThreadQueueBase> cunstructor	= (Constructor<ThreadQueueBase>) waitClass.getConstructor();
				ThreadQueueBase threadQueueBase					= (ThreadQueueBase) cunstructor.newInstance();
				threadQueueBase.setSakuraManager(this.sakuraManager);
				for (int count = 0 ; count < args.length ; count++)
				{
					threadQueueBase.addArgs(args[count]);
				}

				this.sakuraManager.startThreadQueue();
		
				// Threadを作成し、処理を実行する
				ThreadQueueThread threadQueueThread		= new ThreadQueueThread(this.sakuraManager, threadQueueBase);
				threadQueueThread.start();
			}
		}
		catch(Exception exp)
		{
		}
	}
}
