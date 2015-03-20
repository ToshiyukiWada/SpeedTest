package jp.futuresoftware.android.sakura.connection.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import jp.futuresoftware.android.sakura.connection.bluetooth.BluetoothManager.BLUETOOTH_MESSAGE;
import jp.futuresoftware.android.sakura.connection.bluetooth.BluetoothManager.BLUETOOTH_STATE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Bluetoothによる通信対戦管理クラス
 * 
 * @author toshiyuki
 *
 */
public class BluetoothCommunicationManager
{
	/**
	 * @author toshiyuki
	 *
	 */
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
	
	// メンバ変数
    private BluetoothManager bluetoothManager;
    private Handler bluetoothHandler;
    
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private BLUETOOTH_STATE bluetoothState;
	
	/**
	 * @param context
	 * @param handler
	 */
	public BluetoothCommunicationManager(BluetoothManager bluetoothManager)
	{
		this.bluetoothManager		= bluetoothManager;
		this.bluetoothState			= BLUETOOTH_STATE.NONE;
		this.bluetoothHandler		= bluetoothManager.getBluetoothCommunicationHandler();
	}
	
	/**
	 * @param btState
	 */
	private synchronized void setState(BLUETOOTH_STATE btState)
	{
		this.bluetoothState			= btState;
		this.bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.STATE_CHANGE.ordinal(), this.bluetoothState.ordinal(), -1).sendToTarget();
	}
	
	/**
	 * @return
	 */
	public synchronized BLUETOOTH_STATE getState()
	{
		return this.bluetoothState;
	}
	
	/**
	 * 
	 */
	public synchronized void start()
	{
		if (this.connectThread   != null){ this.connectThread.cancel();		this.connectThread = null;		}
		if (this.connectedThread != null){ this.connectedThread.cancel();	this.connectedThread = null;	}

		if (this.acceptThread == null)
		{
			this.acceptThread			= new AcceptThread();
			this.acceptThread.start();
		}

		setState(BLUETOOTH_STATE.LISTEN);
	}
	
	/**
	 * @param bluetoothDevice
	 */
	public synchronized void connect(BluetoothDevice bluetoothDevice)
	{
		if (this.bluetoothState.equals(BLUETOOTH_STATE.CONNECTING))
		{
			if (this.connectThread != null){ this.connectThread.cancel();	this.connectThread = null; 		}
		}
		if (this.connectedThread != null)  { this.connectedThread.cancel();	this.connectedThread = null;	}
		
		this.connectThread = new ConnectThread(bluetoothDevice);
		this.connectThread.start();
		setState(BLUETOOTH_STATE.CONNECTING);
	}
	
	/**
	 * @param bluetoothSocket
	 * @param bluetoothDevice
	 */
	public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice)
	{
		if (this.connectThread != null){ this.connectThread.cancel(); this.connectThread = null; }
		if (this.connectedThread != null){ this.connectedThread.cancel(); this.connectedThread = null; }
		if (this.acceptThread != null){ this.acceptThread.cancel(); this.acceptThread = null; }
		
		this.connectedThread = new ConnectedThread(bluetoothSocket);
		this.connectedThread.start();
		
		Message message = this.bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.CONNECTION.ordinal());
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothManager.DEVICE_NAME, bluetoothDevice.getName());
		message.setData(bundle);
		this.bluetoothHandler.sendMessage(message);
		
		setState(BLUETOOTH_STATE.CONNECTED);
	}
	
	/**
	 * 
	 */
	public synchronized void stop()
	{
		if (this.connectThread != null){ this.connectThread.cancel(); this.connectThread = null; }
		if (this.connectedThread != null){ this.connectedThread.cancel(); this.connectedThread = null; }
		if (this.acceptThread != null){ this.acceptThread.cancel(); this.acceptThread = null; }
		
		setState(BLUETOOTH_STATE.NONE);
	}
	
	/**
	 * @param out
	 */
	public void send(byte[] out)
	{
		ConnectedThread tempConnectedThread;
		
		synchronized (this)
		{
			if (!this.bluetoothState.equals(BLUETOOTH_STATE.CONNECTED)){ return; }
			tempConnectedThread = this.connectedThread;
		}
		tempConnectedThread.send(out);
	}
	
	/**
	 * 
	 */
	private void connectionFailed()
	{
		setState(BLUETOOTH_STATE.LISTEN);
		Message message = this.bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.CONNECTION_FAILED.ordinal());
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothManager.TOAST, "Unable to connect device");
		message.setData(bundle);
		this.bluetoothHandler.sendMessage(message);
	}
	
	/**
	 * 
	 */
	private void connectionLost()
	{
		setState(BLUETOOTH_STATE.LISTEN);
		Message message = this.bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.CONNECTION_LOST.ordinal());
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothManager.TOAST, "Device connection was lost");
		message.setData(bundle);
		this.bluetoothHandler.sendMessage(message);
	}
	
	/**
	 * AcceptThread
	 * @author toshiyuki
	 *
	 */
	private class AcceptThread extends Thread
	{
		private final BluetoothServerSocket bluetoothServerSocket;
		
		/**
		 * 
		 */
		public AcceptThread()
		{
			BluetoothServerSocket tmpBluetoothServerSocket = null;
			
			try
			{
				tmpBluetoothServerSocket = bluetoothManager.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(bluetoothManager.getSakuraManager().getApplicationName(), BluetoothManager.MY_UUID);
			}
			catch(IOException ioexp){}

			this.bluetoothServerSocket		= tmpBluetoothServerSocket;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			this.setName("AcceptThread");
			BluetoothSocket bluetoothSocket = null;
			
			while(!bluetoothState.equals(BLUETOOTH_STATE.CONNECTED))
			{
				try
				{
					bluetoothSocket = this.bluetoothServerSocket.accept(); 
				}
				catch(IOException e)
				{
					break;
				}
				
				if (bluetoothSocket != null)
				{
					synchronized(BluetoothCommunicationManager.this)
					{
						switch(bluetoothState)
						{
							case LISTEN:
							case CONNECTING:
								connected(bluetoothSocket, bluetoothSocket.getRemoteDevice());
								break;
							case NONE:
							case CONNECTED:
								try
								{
									bluetoothSocket.close();
								}
								catch(IOException e)
								{
									
								}
								break;
						}
					}
				}
			}
		}
		
		/**
		 * 
		 */
		public void cancel()
		{
			try
			{
				this.bluetoothServerSocket.close();
			}
			catch(IOException e)
			{
				
			}
		}
	}
	
	/**
	 * ConnectThread
	 * @author toshiyuki
	 *
	 */
	private class ConnectThread extends Thread
	{
		private final BluetoothSocket bluetoothSocket;
		private final BluetoothDevice bluetoothDevice;
		
		/**
		 * @param bluetoothDevice
		 */
		public ConnectThread(BluetoothDevice bluetoothDevice)
		{
			this.bluetoothDevice				= bluetoothDevice;
			BluetoothSocket tempBluetoothSocket	= null;
			
			try
			{
				tempBluetoothSocket				= this.bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothManager.MY_UUID);
			}
			catch(IOException e)
			{
			}
			
			this.bluetoothSocket		= tempBluetoothSocket;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			this.setName("ConnectThread");
			
			bluetoothManager.getBluetoothAdapter().cancelDiscovery();
			
			try
			{
				this.bluetoothSocket.connect();
			}
			catch(IOException e)
			{
				connectionFailed();
				try
				{
					this.bluetoothSocket.close();
				}
				catch(IOException e2)
				{	
				}
				
				BluetoothCommunicationManager.this.start();
				return;
			}
			
			synchronized(BluetoothCommunicationManager.this)
			{
				connectThread = null;
			}
			
			connected(this.bluetoothSocket, this.bluetoothDevice);
		}
		
		/**
		 * 
		 */
		public void cancel()
		{
			try
			{
				this.bluetoothSocket.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	
	/**
	 * ConnectedThread
	 * @author toshiyuki
	 *
	 */
	public class ConnectedThread extends Thread
	{
		private final BluetoothSocket bluetoothSocket;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		
		/**
		 * @param bluetoothSocket
		 */
		public ConnectedThread(BluetoothSocket bluetoothSocket)
		{
			this.bluetoothSocket				= bluetoothSocket;
			InputStream tempInputStream			= null;
			OutputStream tempOutputStream		= null;
			
			try
			{
				tempInputStream		= this.bluetoothSocket.getInputStream();
				tempOutputStream	= this.bluetoothSocket.getOutputStream();
			}
			catch(IOException e)
			{	
			}
			
			this.inputStream		= tempInputStream;
			this.outputStream		= tempOutputStream;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@SuppressLint("NewApi")
		@Override
		public void run()
		{
			// 変数定義
			StringBuffer receiveDataBuffer	= new StringBuffer();
			String receiveData				= null;
			byte[] inputStreamBuffer		= new byte[4096];
			byte[] enableBuffer				= new byte[4096];
			
			int inputStreambufferSize		= -1;
			int tempIndex					= -1;
			
			// メッセージ受信ループ
			while(true)
			{
				try
				{
					// InputStreamからデータを読み込み
					inputStreambufferSize		= this.inputStream.read(inputStreamBuffer);
					
					// バッファからデータを受信した場合
					if (inputStreambufferSize != 0)
					{
						// 読み込んだデータをバッファに追加していく
						Arrays.fill(enableBuffer, (byte) ' ');
						enableBuffer		= Arrays.copyOfRange(inputStreamBuffer, 0, inputStreambufferSize);
						receiveDataBuffer.append(new String(enableBuffer));
						
						// 追加した結果にエンドコードが存在している間は、エンドコードで区切ったメッセージを受信処理に送る
						while (receiveDataBuffer.indexOf(BluetoothManager.END_CODE) != -1)
						{
							// 最初に出現するエンドコードの位置を求める
							tempIndex			= receiveDataBuffer.indexOf(BluetoothManager.END_CODE);

							// 現在の先頭からエンドコードまでの文字を切り出して、それを１つのデータとする
							receiveData			= new String(receiveDataBuffer.substring(0, tempIndex));
							
							// １つのデータ分をバッファから削除する
							receiveDataBuffer	= receiveDataBuffer.delete(0, tempIndex + BluetoothManager.END_CODE.length());
							Log.i("BT message", receiveData);
							
							// 受信処理を呼び出す
							bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.RECEIVE.ordinal(), receiveData.getBytes().length, -1, receiveData.getBytes()).sendToTarget();
							
							// 受信データをnullを格納して、次に備える
							receiveData		= null;
						}
					}
				}
				catch(IOException e)
				{
					connectionLost();
					break;
				}
			}
		}
		
		/**
		 * @param buffer
		 */
		public void send(byte[] buffer)
		{
			try
			{
				this.outputStream.write(buffer);
				bluetoothHandler.obtainMessage(BLUETOOTH_MESSAGE.SEND.ordinal(), -1, -1, buffer).sendToTarget();				
			}
			catch(IOException e){}
		}
		
		/**
		 * 
		 */
		public void cancel()
		{
			try
			{
				this.bluetoothSocket.close();
			}
			catch(IOException e){}
		}
	}
}
