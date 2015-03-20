package jp.futuresoftware.android.sakura.core;

import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;
import android.opengl.GLSurfaceView;

public class SakuraView extends GLSurfaceView
{
	private SakuraManager sakuraManager;
	private SakuraRenderer sakuraRenderer;
	
	public SakuraView(SakuraActivity sakuraActivity)
	{
		super(sakuraActivity);
		this.sakuraManager		= sakuraActivity.getSakuraManager();
		this.sakuraRenderer		= new SakuraRenderer(this.sakuraManager);
		this.setRenderer(this.sakuraRenderer);
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	
	public SakuraRenderer getSakuraRenderer() {
		return sakuraRenderer;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView#onResume()
	 */
	@Override
    public void onResume()
	{
    	super.onResume();
    	this.onResume();
    }

    /* (non-Javadoc)
     * @see android.opengl.GLSurfaceView#onPause()
     */
    @Override
    public void onPause()
    {
    	super.onPause();
    	this.onPause();
    }
}
