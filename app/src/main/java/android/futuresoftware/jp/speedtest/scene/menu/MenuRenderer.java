package android.futuresoftware.jp.speedtest.scene.menu;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/24.
 */
public class MenuRenderer extends SceneRendererBase {

    private MenuScene scen;
    private MenuProcess proc;
	private MenuButton btn;

    private int count;

    @Override
    public void init(GL10 gl) {
        scen		= (MenuScene)this.scene;
        proc        = (MenuProcess)this.process;
		btn			= (MenuButton)this.button;
    }

    @Override
    public void draw(GL10 gl, float frametime) {
        for (count = 0 ; count < btn.btnMenus.length ; count++) {
            // this.drawTexture(gl, scen.texGame, scen.texGameIndex[TexGame.TEX.menuFrame.ordinal()], false, proc.menuScrollStartPos + (count * 190), this.sakuraManager.getVirtualHeight() / 2 - 450 / 2);
			this.drawButton(gl, btn.btnMenus[count],false, proc.menuScrollStartPos + (count * 190), this.sakuraManager.getVirtualHeight() / 2 - 450 / 2);
        }
    }

    @Override
    public void term(GL10 gl) {

    }
}
