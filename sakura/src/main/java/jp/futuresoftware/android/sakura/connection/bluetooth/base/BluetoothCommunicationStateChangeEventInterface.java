package jp.futuresoftware.android.sakura.connection.bluetooth.base;

import jp.futuresoftware.android.sakura.connection.bluetooth.BluetoothManager.BLUETOOTH_STATE;

public interface BluetoothCommunicationStateChangeEventInterface
{
	public void event(BLUETOOTH_STATE bluetoothState);
}
