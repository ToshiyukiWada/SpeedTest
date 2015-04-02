package jp.futuresoftware.android.sakura;

/**
 * Created by toshiyuki on 2015/03/19.
 */
public class SAKURA
{
    // 定数宣言
    public static String UUID_DAT_FILENAME				= "_sakura_uuid.dat";		// このフレームワークを使うと必ず生成される端末を識別する為のIDを保持しておくファイル名
	public static String VARIABLE_DAT_FILENAME			= "_sakura_variable.dat";	// このフレームワークを使った場合の変数保持用ファイル名

    public static int TARGET_FPS						= 60;						// 目標とするFPS
    public static int UPDATE_SPAN						= 6;						// PROCESS処理が高速になり過ぎないように待機するms
    public static int TEXT_TEXTURE_DEFAULT_BUFFER_SIZE	= 30;						// テキスト表示用のバッファのデフォルト値

	// アプリケーションステータス
    public enum STATUS
    {
        INITIALIZE
        ,INITIALIZING
        ,WAIT
        ,ACTIVE
        ,TERMINATE
        ,TERMINATING
        ,EXCEPTION
    };

	// アプリケーションステータス詳細
    public static enum STATUS_DETAIL {
        WAIT												// 処理中
        ,ACTIVE												// 実行
    };

	// AdMobの表示位置定数
    public enum ADMOB_VERTICAL_POSITION
    {
        TOP
        ,BOTTOM
    };
}
