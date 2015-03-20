package jp.futuresoftware.android.sakura.core;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import jp.futuresoftware.android.sakura.SakuraManager;

public class SakuraWeb
{
	/**
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static byte[] requestUrl(SakuraManager sakuraManager, String url, Map<String, String> params) throws Exception
	{
		URL urlObject;									// URL
		HttpURLConnection httpUrlConnection;		// HTTPコネクション
		InputStream inputStream;					// ファイル読込ストリーム
		ArrayList<Byte> bytesBuffer;				// ファイル動的格納用配列の定義
		byte[] byteBuffer;							// ファイル読込バッファ
		int readSize;								// 上記読込で一度に読み込んだバッファサイズ
		
		// WEBから素材をダウンロードする
		urlObject				= new URL(url);
		httpUrlConnection			= (HttpURLConnection)urlObject.openConnection();					// textureURLに指定されたURLをオープン
		httpUrlConnection.setRequestMethod("POST");												// URLから取得
		StringBuffer paramBuffer		= new StringBuffer();											// POSTパラメータを作成するのに必要な文字バッファの作成
		paramBuffer.append("sakuraUUID=" + URLEncoder.encode(sakuraManager.getUUID(), "UTF-8"));	// 必ずUUIDは送信してあげる
		if (params != null)																				// paramsが宣言されている場合
		{
			Iterator<String> paramsIterator	= params.keySet().iterator();								// キーの取得の為にイテレータを取得
			while(paramsIterator.hasNext())																// パラメータの連結
			{
				String key	= paramsIterator.next();													// キーの取得
				paramBuffer.append("&" + key + "=" + URLEncoder.encode(params.get(key), "UTF-8"));		// POSTパラメータの生成
			}
		}
		PrintWriter printWriter		= new PrintWriter(httpUrlConnection.getOutputStream());		// 
		printWriter.print(paramBuffer.toString());														// 
		printWriter.close();																			// 
		httpUrlConnection.connect();																// URLから取得
		inputStream = httpUrlConnection.getInputStream();										// 読込開始
		
		// InputStream読込
		bytesBuffer				= new ArrayList<Byte>();
		byteBuffer				= new byte[1024];
		readSize				= 0;
		while(true)
		{
			readSize = inputStream.read(byteBuffer);								// バッファにデータを格納
			if(readSize <= 0){ break; }												// 終了判定
			for (int count = 0 ; count < readSize ; count++)
			{
				bytesBuffer.add(byteBuffer[count]);									// 配列に結果を追加
			}
		}
		
		byte[] loadBytes		= new byte[bytesBuffer.size()];						// 上記で読み込んだバイト配列サイズで配列を生成
		for (int count = 0 ; count < bytesBuffer.size() ; count++)						// 配列を格納
		{
			loadBytes[count] = bytesBuffer.get(count);
		}
		
		// コネクション切断
		httpUrlConnection.disconnect();
		
		// 結果登録
		return loadBytes;
	}
}
