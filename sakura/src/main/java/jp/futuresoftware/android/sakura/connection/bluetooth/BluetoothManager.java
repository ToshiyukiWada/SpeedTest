package jp.futuresoftware.android.sakura.connection.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import jp.futuresoftware.android.sakura.SakuraManager;
import jp.futuresoftware.android.sakura.base.CallbackEventInterface;
import jp.futuresoftware.android.sakura.connection.bluetooth.base.BluetoothCommunicationConnectionEventInterface;
import jp.futuresoftware.android.sakura.connection.bluetooth.base.BluetoothCommunicationReadEventInterface;
import jp.futuresoftware.android.sakura.connection.bluetooth.base.BluetoothCommunicationStateChangeEventInterface;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

/**
 * Bluetooth管理クラス
 * 
 * @author toshiyuki
 *
 */
public class BluetoothManager
{
	// 定数宣言
	public static final String DEVICE_NAME		= "device_name";
	public static final String TOAST			= "toast";
	public static final UUID MY_UUID			= UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	//public static final UUID MY_UUID			= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String END_CODE			= "[%!END=CODE%!]";
	
	/**
	 * @author toshiyuki
	 *
	 */
	public static enum BLUETOOTH_MESSAGE
	{
		 STATE_CHANGE
		,RECEIVE
		,SEND
		,CONNECTION
		,CONNECTION_FAILED
		,CONNECTION_LOST
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	public static enum BLUETOOTH_STATE
	{
		 NONE
		,LISTEN
		,CONNECTING
		,CONNECTED
	}
	
	public static enum MESSAGE_TYPE
	{
		 SEND
		,RETURN
	}
	
	// メンバ変数の定義
	private boolean isEnable;
	private SakuraManager sakuraManager;																					// 親クラス
	private BluetoothCommunicationManager bluetoothCommunicationManager;										// Bluetooth通信メッセージ処理クラス
	private BluetoothCommunicationHandler bluetoothCommunicationHandler;										// Bluetooth通信メッセージ処理クラス
	private boolean isRunningRetryCheck;
	private RetryCheckThread retryCheckThread;
	private boolean isInit;																						// 一度でも初期処理を実施したか否か
	private BluetoothAdapter bluetoothAdapter;																	// Bluetoothアダプター(Bluetoothの根底部分を制御するインスタンス)
	private CallbackEventInterface enableBluetoothSuccessEvent;													// Bluetoothアダプターを利用可能にできた(または、元々利用可能だった)場合の処理イベント
	private CallbackEventInterface enableBluetoothFailureEvent;													// Bluetoothアダプターを利用可能にできなかった場合のイベント
		
	private ArrayList<BluetoothDevice> pairingDevices;															// 既にペアリングした事があるbluetooth機器一覧
	
	private ArrayList<BluetoothDevice> newDevices;																// 検索して該当したbluetooth機器一覧
	private NewDiviceFilterReceiver newDiviceFilterReceiver;													// Bluetooth機器検索時の処理クラス
	private boolean isNewDeviceSearching;																		// Bluetooth検索処理を実施中か否か
	private CallbackEventInterface newDeviceSearchingEndEvent;													// Bluetooth検索処理が終わった時の処理イベント

	private BluetoothCommunicationConnectionEventInterface bluetoothCommunicationConnectionEventInterface;		// 
	private BluetoothCommunicationStateChangeEventInterface bluetoothCommunicationStateChangeEventInterface;	// 
	private CallbackEventInterface bluetoothCommunicationConnectionFailedEventInterface;						// 
	private CallbackEventInterface bluetoothCommunicationConnectionLostEventInterface;							// 
	
	private HashMap<String, BluetoothCommunicationReadEventInterface> receiveEvents;							// 
	private HashMap<String, BluetoothCommunicationReadEventInterface> returnEvents;								// 
	private HashMap<String, String> retryReadEvents;
	
	/**
	 * @param manager
	 */
	public BluetoothManager(SakuraManager sakuraManager)
	{
		this.isEnable												= false;
		this.sakuraManager											= sakuraManager;
		this.bluetoothCommunicationManager							= null;
		this.bluetoothCommunicationHandler							= new BluetoothCommunicationHandler(this);
		this.retryCheckThread										= new RetryCheckThread(this);
		this.isInit													= false;
		this.bluetoothAdapter										= null;
		this.enableBluetoothSuccessEvent							= null;
		this.enableBluetoothFailureEvent							= null;

		this.pairingDevices											= new ArrayList<BluetoothDevice>();
		
        this.newDevices												= new ArrayList<BluetoothDevice>();
		this.newDiviceFilterReceiver								= null;
		this.isNewDeviceSearching									= false;
		this.newDeviceSearchingEndEvent								= null;
		
		this.bluetoothCommunicationConnectionEventInterface			= null;
		this.bluetoothCommunicationStateChangeEventInterface		= null;
		this.bluetoothCommunicationConnectionFailedEventInterface	= null;
		this.bluetoothCommunicationConnectionLostEventInterface		= null;
		
		this.receiveEvents											= new HashMap<String, BluetoothCommunicationReadEventInterface>();
		this.returnEvents											= new HashMap<String, BluetoothCommunicationReadEventInterface>();
		this.retryReadEvents										= new HashMap<String, String>();
		
		this.isRunningRetryCheck									= false;
	}
	
	/**
	 * Bluetooth通信を利用可能状態とする
	 * 
	 * @param successEvent
	 * @param failureEvent
	 */
	public void enableBluetoothCommunication(CallbackEventInterface successEvent ,CallbackEventInterface failureEvent)
	{
		if (this.isEnable == false)
		{
			this.isEnable					= true;
			
			// Bluetoothアダプタの取得
			this.bluetoothAdapter			= BluetoothAdapter.getDefaultAdapter();
			if (this.bluetoothAdapter == null)
			{
				// Bluetoothアダプタを取得できない場合は失敗関数の起動
				failureEvent.event();
				return;
			}
			
			// リトライ監視スレッドの起動
			if (this.isRunningRetryCheck == false)
			{
				this.isRunningRetryCheck		= true;
				this.retryCheckThread.start();
			}
			
			// Bluetoothアダプターが見つかった場合
			if (!this.bluetoothAdapter.isEnabled())
			{
				this.enableBluetoothSuccessEvent		= successEvent;
				this.enableBluetoothFailureEvent		= failureEvent;
				
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				sakuraManager.getSakuraActivity().setStartActivityForResult(enableIntent, BluetoothCommunicationManager.REQUEST_ENABLE_BT);
				return;
			}
			else
			{
				// 内部初期処理を起動する
				innerInit();
	
				// 既にBluetoothデバイスが利用可能な場合は利用可能イベントをコールする
				successEvent.event();
				return;
			}
		}
	}
	
	/**
	 * 
	 */
	public void disableBluetoothCommunication()
	{
		if (this.isEnable == true)
		{
			this.disconnect();
			this.isEnable		= false;
		}
	}
	
	/**
	 * 内部初期処理
	 * この処理はuseBluetoothCommunicationを呼び出された時に自動的に呼び出される
	 */
	public void innerInit()
	{
		// 今までに一度も初期処理が起動していない場合のみ、インスタンスの生成等を実施する
		if (this.isInit == false)
		{
			this.isInit							= true;
			
			this.bluetoothCommunicationManager	= new BluetoothCommunicationManager(this);
			this.bluetoothCommunicationManager.start();
			
			this.newDiviceFilterReceiver		= new NewDiviceFilterReceiver();
	        
	        this.pairingDevices					= new ArrayList<BluetoothDevice>();
	        this.newDevices						= new ArrayList<BluetoothDevice>();
	        
	        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	        sakuraManager.getSakuraActivity().registerReceiver(this.newDiviceFilterReceiver, filter);
	        
	        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	        sakuraManager.getSakuraActivity().registerReceiver(this.newDiviceFilterReceiver, filter);
		}
		this.pairingDevices.clear();
		this.newDevices.clear();
	}
	
	/**
	 * 終了処理
	 * この処理はActivityの終了時に自動的に呼び出される
	 */
	public void destroy()
	{
		if (this.isRunningRetryCheck == true)
		{
			this.isRunningRetryCheck		= false;
			try{this.retryCheckThread.join();}catch(Exception exp){}
		}
		
		if (this.isInit == true)
		{
			this.bluetoothCommunicationManager.stop();
			sakuraManager.getSakuraActivity().unregisterReceiver(this.newDiviceFilterReceiver);
		}
	}
	
	/**
	 * @return
	 */
	public SakuraManager getSakuraManager() {
		return sakuraManager;
	}

	/**
	 * この処理で利用しているBluetoothアダプタを返却する
	 * 
	 * @return
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}


	/**
	 * Bluetooth有効成功時のコールバック関数を返却する
	 * 
	 * @return
	 */
	public CallbackEventInterface getEnableBluetoothSuccessEvent() {
		return enableBluetoothSuccessEvent;
	}

	/**
	 * Bluetooth有効失敗時のコールバック関数を返却する
	 * 
	 * @return
	 */
	public CallbackEventInterface getEnableBluetoothFailureEvent() {
		return enableBluetoothFailureEvent;
	}
	
	/**
	 * ペアリング済みBluetooth機器の情報を最新に更新する
	 */
	public void updatePairingDevices()
	{
		this.pairingDevices.clear();
		this.pairingDevices.addAll(this.bluetoothAdapter.getBondedDevices());
	}

	/**
	 * ペアリング済みBluetooth機器の一覧を取得する
	 * 
	 * @return
	 */
	public ArrayList<BluetoothDevice> getPairingDevices(boolean isUpdate)
	{
		if (isUpdate){ updatePairingDevices(); }
		return this.pairingDevices;
	}
	
	/**
	 * 検索Bluetooth機器の情報を最新に更新する
	 */
	public void updateNewDevices()
	{	
		updateNewDevices(null);
	}

	/**
	 * @param newDeviceSearchingEndEvent
	 */
	public void updateNewDevices(CallbackEventInterface newDeviceSearchingEndEvent)
	{
		// 検索終了時のイベントを設定
		this.newDeviceSearchingEndEvent		= newDeviceSearchingEndEvent;
		
		// 検索中なら一旦停止
		if (this.bluetoothAdapter.isDiscovering()) {
			this.bluetoothAdapter.cancelDiscovery();
        }

		// 検索開始
		this.newDiviceFilterReceiver.start();
		this.bluetoothAdapter.startDiscovery();
	}
	
	/**
	 * 検索Bluetooth機器の一覧を取得する
	 * 
	 * @return
	 */
	public ArrayList<BluetoothDevice> getNewDevices(boolean isUpdate)
	{
		if (isUpdate){ updateNewDevices(); }
		return this.newDevices;
	}

	/**
	 * 現在接続履歴の無いBluetooth機器を検索中か否かを返却する
	 * 
	 * @return
	 */
	public boolean isNewDeviceSearching() {
		return isNewDeviceSearching;
	}

	/**
	 * 引数で指定したデバイスとコネクション接続を実施する
	 * 
	 * @param bluetoothDevice
	 */
	public void connet(BluetoothDevice bluetoothDevice)
	{
		this.bluetoothCommunicationManager.connect(bluetoothDevice);
	}
	
	/**
	 * 現在接続しているコネクションを切断する
	 */
	public void disconnect()
	{
		this.bluetoothCommunicationConnectionEventInterface				= null;
		this.bluetoothCommunicationStateChangeEventInterface			= null;
		this.bluetoothCommunicationConnectionFailedEventInterface		= null;
		this.bluetoothCommunicationConnectionLostEventInterface			= null;
		
		this.bluetoothCommunicationManager.stop();
	}
	
	/**
	 * 接続相手にメッセージを送信する
	 */
	private void send(String tag, MESSAGE_TYPE messageType, String message)
	{
		// コネクション済みの場合のみ相手に送信する
		if (this.bluetoothCommunicationManager.getState().equals(BLUETOOTH_STATE.CONNECTED))
		{
			// 1行目にタグ
			// 2行目に送信か返信か
			// 3行目以降にメッセージ
			this.bluetoothCommunicationManager.send((tag + "\n" + Integer.toString(messageType.ordinal()) + "\n" + message + BluetoothManager.END_CODE).getBytes());
		}
	}
	
	/**
	 * @return
	 */
	public BluetoothCommunicationHandler getBluetoothCommunicationHandler() {
		return bluetoothCommunicationHandler;
	}

	/**
	 * Bluetooth機器検索時の処理クラス
	 * 
	 * @author toshiyuki
	 *
	 */
	private class NewDiviceFilterReceiver extends BroadcastReceiver
	{
		/**
		 * 
		 */
		public void start()
		{
			isNewDeviceSearching				= true;
			newDevices.clear();
		}
		
		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// この受信処理が呼び出されたActionを取得する
			String action = intent.getAction();
			
			//--------------------------
			// Bluetoothデバイス発見時
			//--------------------------
			if (BluetoothDevice.ACTION_FOUND.equals(action))
			{
				// Bluetoothデバイス情報を取得
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				// 接続履歴があるデバイスは新規デバイスではないので、今回は対象外とする
				// なお、getBondStateの戻り値は以下のパターンが存在する
				// BluetoothDevice.BOND_BONDING		: デバイスは接続中
				// BluetoothDevice.BOND_BONDED		: デバイスは接続履歴あり
				// BluetoothDevice.BOND_NONE		: デバイスは接続履歴なし
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					newDevices.add(device);
				}
			}
			
			//--------------------------
			// Bluetoothデバイス検索終了時
			//--------------------------
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				// 探索終了(ここで、上記ACTION_FOUNDの確定処理を行う)
				isNewDeviceSearching			= false;
				
				// コールバック関数が定義されている場合は、それをコールして終わる
				if (newDeviceSearchingEndEvent != null)
				{
					newDeviceSearchingEndEvent.event();
				}
			}
		}
	}

	/**
	 * @return
	 */
	public HashMap<String, BluetoothCommunicationReadEventInterface> getReceiveEvents() {
		return receiveEvents;
	}

	/**
	 * @return
	 */
	public HashMap<String, BluetoothCommunicationReadEventInterface> getReturnEvents() {
		return returnEvents;
	}
	
	/**
	 * @return
	 */
	public HashMap<String, String> getRetryReadEvents() {
		return retryReadEvents;
	}

	public boolean isRunningRetryCheck() {
		return isRunningRetryCheck;
	}

	/**
	 * コネクト確立時のイベントを登録する
	 * 
	 * @param bluetoothCommunicationConnectionEventInterface
	 */
	public void setBluetoothCommunicationConnectionEventInterface(BluetoothCommunicationConnectionEventInterface bluetoothCommunicationConnectionEventInterface) {
		this.bluetoothCommunicationConnectionEventInterface = bluetoothCommunicationConnectionEventInterface;
	}

	/**
	 * 状況変化時のイベントを登録する
	 * 
	 * @param bluetoothCommunicationStateChangeEventInterface
	 */
	public void setBluetoothCommunicationStateChangeEventInterface(BluetoothCommunicationStateChangeEventInterface bluetoothCommunicationStateChangeEventInterface) {
		this.bluetoothCommunicationStateChangeEventInterface = bluetoothCommunicationStateChangeEventInterface;
	}

	/**
	 * コネクション失敗時のイベントを登録する
	 * 
	 * @param bluetoothCommunicationConnectionFailedEventInterface
	 */
	public void setBluetoothCommunicationConnectionFailedEventInterface(CallbackEventInterface bluetoothCommunicationConnectionFailedEventInterface) {
		this.bluetoothCommunicationConnectionFailedEventInterface = bluetoothCommunicationConnectionFailedEventInterface;
	}

	/**
	 * コネクション消失時のイベントを登録する
	 * 
	 * @param bluetoothCommunicationConnectionLostEventInterface
	 */
	public void setBluetoothCommunicationConnectionLostEventInterface(CallbackEventInterface bluetoothCommunicationConnectionLostEventInterface) {
		this.bluetoothCommunicationConnectionLostEventInterface = bluetoothCommunicationConnectionLostEventInterface;
	};
	
	/**
	 * Bluetooth経由で相手にデータを送信する
	 * tagはデータの種類を示し、messageはメッセージやjson形式のデータを表す
	 * 
	 * @param tag
	 * @param message
	 * @param callbackEvent
	 */
	public void send(String tag, String message, final CallbackEventInterface callbackEvent)
	{
		// このメッセージを送信し、相手から受信確認を受け取った場合の為にイベントを登録しておく
		this.returnEvents.put(tag, new BluetoothCommunicationReadEventInterface()
		{
			@Override
			public void event(String message)
			{
				if (callbackEvent != null){ callbackEvent.event(); }
			}
		});

		// メッセージ送信
		this.send(tag, MESSAGE_TYPE.SEND, message);
	}

	/**
	 * @param tag
	 * @param readEvent
	 */
	public void registReceiveEvent(String tag, BluetoothCommunicationReadEventInterface readEvent)
	{
		if (readEvent != null)
		{
			this.receiveEvents.put(tag, readEvent);
		}
	}

	/**
	 * @param tag
	 */
	public void deleteReceiveEvent(String tag)
	{
		this.receiveEvents.remove(tag);
	}
	
	public void receive(String tag, String message)
	{
		if (this.receiveEvents.containsKey(tag))
		{
			// 通常の受信処理
			this.send(tag, MESSAGE_TYPE.RETURN, "");			// 正常に受信したことを返信
			this.receiveEvents.get(tag).event(message);	// 受信イベントで定義されている処理を実行
		}
		else
		{
			// 受信が定義されていない場合
			// リトライ用の連想配列にメッセージを貯める
			this.retryReadEvents.put(tag, message);		// リトライ用に貯める
		}
	}
	
	/**
	 * @author toshiyuki
	 *
	 */
	private static class BluetoothCommunicationHandler extends Handler
	{
		private BluetoothManager bluetoothManager;
		public BluetoothCommunicationHandler(BluetoothManager bluetoothManager)
		{
			this.bluetoothManager		= bluetoothManager;
		}
		
		@Override
        public void handleMessage(Message msg)
		{
			switch (BLUETOOTH_MESSAGE.values()[msg.what])
			{
				//-----------------------------------------
				// 接続状況変化
				//-----------------------------------------
				case STATE_CHANGE:
					 switch (BLUETOOTH_STATE.values()[msg.arg1])
					 {
					 	case NONE:
					 		break;
					 	case LISTEN:
					 		break;
					 	case CONNECTING:
					 		break;
					 	case CONNECTED:
					 		break;
					 }
					 if (this.bluetoothManager.bluetoothCommunicationStateChangeEventInterface != null)
					 {
						 this.bluetoothManager.bluetoothCommunicationStateChangeEventInterface.event(BLUETOOTH_STATE.values()[msg.arg1]);
					 }
					break;
					
				//-----------------------------------------
				// 送信
				//-----------------------------------------
				case SEND:	
					break;
				
				//-----------------------------------------
				// 受信
				//-----------------------------------------
				case RECEIVE:
					byte[] receiveBuf		= (byte[]) msg.obj;
					String receiveMessage	= new String(receiveBuf, 0, msg.arg1);
					
					// タグ(改行コードで区切った1行目)とメッセージ(改行コードで区切った2行目以降)に切り分ける
					// 1行目にタグ
					// 2行目に送信か返信か
					// 3行目以降にメッセージ
					int tagSeparatorIndex		= receiveMessage.indexOf("\n");
					int typeSeparatorIndex		= receiveMessage.indexOf("\n", tagSeparatorIndex + 1);
					String tag					= receiveMessage.substring(0, tagSeparatorIndex);
					MESSAGE_TYPE messageType	= MESSAGE_TYPE.values()[Integer.parseInt(receiveMessage.substring(tagSeparatorIndex + "\n".length(), typeSeparatorIndex))];
					String message				= receiveMessage.substring(typeSeparatorIndex + "\n".length());
					
					//---------------------------------------------------------
					// 
					// 受信処理
					// 
					//---------------------------------------------------------
					if (messageType.equals(MESSAGE_TYPE.SEND))
					{
						//=================================
						// 通常の送受信処理
						//=================================
						this.bluetoothManager.receive(tag, message);
					}
					else if (messageType.equals(MESSAGE_TYPE.RETURN))
					{
						//=================================
						// 相手からの返信待ち
						//=================================
						if (this.bluetoothManager.getReturnEvents().containsKey(tag))
						{
							// 送信結果の受信の場合
							this.bluetoothManager.getReturnEvents().get(tag).event(message);	// 相手が受信した時点のイベントを実行
							this.bluetoothManager.getReturnEvents().remove(tag);				// これは今後取って置く必要がないので削除する
						}
					}
					break;
					
				//-----------------------------------------
				// 接続
				//-----------------------------------------
				case CONNECTION:
					if (this.bluetoothManager.bluetoothCommunicationConnectionEventInterface != null)
					{
						this.bluetoothManager.bluetoothCommunicationConnectionEventInterface.event(msg.getData().getString(DEVICE_NAME));
					}
					break;
					
				//-----------------------------------------
				// 接続失敗
				//-----------------------------------------
				case CONNECTION_FAILED:
					if (this.bluetoothManager.bluetoothCommunicationConnectionFailedEventInterface != null)
					{
						this.bluetoothManager.bluetoothCommunicationConnectionFailedEventInterface.event();
					}
					break;
					
				//-----------------------------------------
				// 接続消失
				//-----------------------------------------
				case CONNECTION_LOST:
					if (this.bluetoothManager.bluetoothCommunicationConnectionLostEventInterface != null)
					{
						this.bluetoothManager.bluetoothCommunicationConnectionLostEventInterface.event();
					}
					break;
			}
		}
	}
	
	/**
	 * メッセージ受信リトライクラス
	 * 
	 * 通信メッセージを受け取った場合、そのリスナー(イベント)が定義されていなかった場合は、このクラスに未受信メッセージとしてタグとメッセージを溜め込む
	 * このクラスは、未受信メッセージを受信できるようになったか否かを監視し、受信できる状態になった場合、このクラスで保持しているメッセージを送り込む
	 * 同時に、受信したことを相手側に送信する。
	 * 
	 * @author toshiyuki
	 *
	 */
	private class RetryCheckThread extends Thread
	{
		private int count;
		private Iterator<String> iterator;
		private ArrayList<String> successKeys;
		private String tempKey;
		private BluetoothManager bluetoothManager;

		/**
		 * @param bluetoothManager
		 */
		public RetryCheckThread(BluetoothManager bluetoothManager)
		{
			this.bluetoothManager		= bluetoothManager;
			this.successKeys			= new ArrayList<String>();
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			while(this.bluetoothManager.isRunningRetryCheck())
			{
				// リトライすべきイベントがあればリトライを実施する
				this.successKeys.clear();
				this.iterator		= this.bluetoothManager.retryReadEvents.keySet().iterator();
				while(this.iterator.hasNext())
				{
					this.tempKey			= this.iterator.next();
					if (this.bluetoothManager.receiveEvents.containsKey(this.tempKey))
					{
						this.bluetoothManager.receive(this.tempKey, this.bluetoothManager.retryReadEvents.get(this.tempKey));
						this.successKeys.add(this.tempKey);
					}
				}
				for (count = 0 ; count < this.successKeys.size() ; count++)
				{
					this.bluetoothManager.retryReadEvents.remove(this.successKeys.get(count));
				}
				
				// 待ち
				try{Thread.sleep(1000);}catch(Exception exp){}
			}
		}
	}
}

