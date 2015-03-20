package android.futuresoftware.jp.speedtest.scene.test;

import javax.microedition.khronos.opengles.GL10;

import jp.futuresoftware.android.sakura.base.SceneRendererBase;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class TestRenderer extends SceneRendererBase
{
    private TestScene testScene;
    private TestProcess testProcess;
    private int count;

    @Override
    public void init(GL10 gl) {
        this.testScene			= (TestScene)this.scene;
        this.testProcess        = (TestProcess)this.process;
    }

    @Override
    public void draw(GL10 gl, float frametime) {
        // this.drawTexture(gl, this.testScene.textureAndroid, 0, true, (int)this.testProcess.androidPoint.x, (int)this.testProcess.androidPoint.y);

        for (count = 0 ; count < this.sakuraManager.getVirtualWidth() ; count++)
        {
            this.burstTexture(this.testScene.texIDStreet, 0, true, count, (this.sakuraManager.getVirtualHeight() / 2) + (int)this.testProcess.startPosY - (count / 4), 100, 5, 5);
        }
        this.burstTextureRenderer(gl, this.testScene.texIDStreet);

        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 500, 100, 100, 93, 148);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 435, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 410, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 385, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 360, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 335, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 310, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 285, 150, 100, 23,  37);
        this. burstTexture(this.testScene.texIDHuman, (int)testProcess.humanAnimation, false, 260, 150, 100, 23,  37);
        this.burstTextureRenderer(gl, this.testScene.texIDHuman);
    }

    @Override
    public void term(GL10 gl) {

    }
}
