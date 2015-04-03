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

		// メニューボタンの描画
		for (count = 0 ; count < btn.btnMenus.length ; count++) {
			this.drawButton(gl, btn.btnMenus[count], false, proc.menuScrollStartPos + (count * 190), 5);
			this.drawAlphaNum(gl, this.btn.btnMenuLabels[count], 25, 1, proc.menuScrollStartPos + (count * 190) + 26, 60);
		}

		// ショップボタンの描画
		this.drawButton(gl, btn.btnShop, false, 320, 425);
		this.drawAlphaNum(gl, this.btn.btnShopLabel, 35, 2, 325, 440);

		// WEBSITEボタンの描画
		this.drawButton(gl,btn.btnWebsite, false, 640, 425);
		this.drawAlphaNum(gl, this.btn.btnWebsiteLabel, 35, 2, 665, 440);

		// 戻るボタンの描画
		this.drawButton(gl, this.btn.btnBack, false, 10, this.sakuraManager.getVirtualHeight() - 80 - 15, 80, 80);
	}

    @Override
    public void term(GL10 gl) {

    }
}
