package android.futuresoftware.jp.speedtest.scene.splash;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/20.
 */
public class SplashRenderer extends SceneRendererBase {

    private SplashScene scen;
    private SplashProcess proc;

    @Override
    public void init(GL10 gl) {
        scen		= (SplashScene)this.scene;
        proc        = (SplashProcess)this.process;
    }

    @Override
    public void draw(GL10 gl, float frametime) {
        if (proc.timer < 5.0f)
        {
            if (proc.timer <= 2.0f)      { this.drawTexture(gl, scen.texIDFslogo, 0, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2,       (int)(((proc.timer - 0.0f) / 2.0f) * 100.0f)); }
            else if (proc.timer >  3.0f) { this.drawTexture(gl, scen.texIDFslogo, 0, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2, 100 - (int)(((proc.timer - 3.0f) / 2.0f) * 100.0f)); }
            else                         { this.drawTexture(gl, scen.texIDFslogo, 0, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2, 100); }
        }
        else
        {
            if (proc.timer <= 7.0f)      { this.drawTexture(gl, scen.texIDFslogo, 1, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2,       (int)(((proc.timer - 5.0f) / 2.0f) * 100.0f)); }
            else if (proc.timer >  8.0f) { this.drawTexture(gl, scen.texIDFslogo, 1, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2, 100 - (int)(((proc.timer - 8.0f) / 2.0f) * 100.0f)); }
            else                         { this.drawTexture(gl, scen.texIDFslogo, 1, true, this.sakuraManager.getVirtualWidth() / 2, this.sakuraManager.getVirtualHeight() / 2, 100); }
        }
    }

    @Override
    public void term(GL10 gl) {
    }
}
