package android.futuresoftware.jp.speedtest.scene.test;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class TestRenderer extends SceneRendererBase
{
    private TestScene scen;
    private TestProcess proc;

    private int count;

    @Override
    public void init(GL10 gl) {
        scen		= (TestScene)this.scene;
        proc        = (TestProcess)this.process;
    }

    @Override
    public void draw(GL10 gl, float frametime) {

        // this.drawTexture(gl, this.testScene.textureAndroid, 0, true, (int)this.testProcess.androidPoint.x, (int)this.testProcess.androidPoint.y);

        // コースの描画
        for (count = 0 ; count < this.sakuraManager.getVirtualWidth() ; count++)
        {
            this.burstTexture(scen.texIDStreet, 0, true, count, (this.sakuraManager.getVirtualHeight() / 2) + (int)proc.startPosY - (count / 4), 100, 5, 5);
        }
        this.burstTextureRenderer(gl, scen.texIDStreet);

        // キャラクターの描画
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 500, 100, 100, 93, 148);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 435, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 410, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 385, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 360, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 335, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 310, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 285, 150, 100, 23,  37);
        this. burstTexture(scen.texIDHuman, (int)proc.humanAnimation, false, 260, 150, 100, 23,  37);
        this.burstTextureRenderer(gl, scen.texIDHuman);
    }

    @Override
    public void term(GL10 gl) {

    }
}
