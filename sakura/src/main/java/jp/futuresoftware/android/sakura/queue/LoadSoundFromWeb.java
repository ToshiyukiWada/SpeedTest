package jp.futuresoftware.android.sakura.queue;

import java.io.FileInputStream;
import java.io.OutputStream;

import jp.futuresoftware.android.sakura.core.SakuraWeb;
import jp.futuresoftware.android.sakura.queue.base.LoadTextureBase;

public class LoadSoundFromWeb extends LoadTextureBase
{
	@SuppressWarnings("static-access")
	@Override
	public void main()
	{
		try
		{
			// テクスチャ名
			String soundName							= this.args.get(0);																	// 第1パラメータにテクスチャ名が格納されている
			
			// テクスチャ名が存在していなければ、該当データを取得してOpenGLに登録する
			if (!this.sakuraManager.getSounds().containsKey(soundName))
			{
				// 読み込んだoggファイルを一旦ファイルとして書き込む
				OutputStream outputStream		= null;
				String soundFileName			= soundName + ".ogg";
				byte[] soundBytes				= SakuraWeb.requestUrl(sakuraManager, this.args.get(1), null);									// 第2パラメータにテクスチャ情報のURLが格納されている
				try
				{
					outputStream				= this.sakuraManager.getContext().openFileOutput(soundFileName, this.sakuraManager.getContext().MODE_PRIVATE);
					outputStream.write(soundBytes, 0, soundBytes.length);
				}
				catch(Exception exp)
				{					
				}
				finally{ if(outputStream != null){ outputStream.close(); } }
				
				FileInputStream fileInputStream	= this.sakuraManager.getContext().openFileInput(soundFileName);
				int soundID			= this.sakuraManager.getSoundPool().load(fileInputStream.getFD(), 0, soundBytes.length, 1);
				this.sakuraManager.getSounds().put(soundName, soundID);
			}
		}
		catch(Exception exp){}
	}
}
