package io.evansmurithi.bluedaisy.managers;

import android.app.Application;

/**
 * Created by evans on 3/11/17.
 *
 * BDApplication class.
 */

public class BDApplication extends Application {

    private static final String TAG = "BDApplication";

    private BluetoothService mBluetoothService;

    public BluetoothService getBluetoothService() {
        return mBluetoothService;
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.mBluetoothService = bluetoothService;
    }
}
