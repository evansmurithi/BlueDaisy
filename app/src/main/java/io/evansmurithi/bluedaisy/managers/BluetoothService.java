package io.evansmurithi.bluedaisy.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import io.evansmurithi.bluedaisy.fragments.ConnectionFragment;

/**
 * Created by evans on 3/5/17.
 *
 * BluetoothService class.
 */

public class BluetoothService {

    private static final String TAG = "BluetoothService";

    // uuid for this application (same as server)
    private static final UUID Q_UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // connection states
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public BluetoothService(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mContext = context;
    }

    private synchronized void setState(int state) {
        mState = state;
    }

    public synchronized int getState() {
        return mState;
    }

    /**
     * Cancel thread attempting to make a connection.
     */
    private synchronized void cancelConnectThread() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    /**
     * Cancel any thread running a connection
     */
    private synchronized void cancelConnectedThread() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    public synchronized void start() {
        cancelConnectThread();
        cancelConnectedThread();
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "Connection started. Device: " + device.getName());
        if (mState == STATE_CONNECTING) {
            cancelConnectThread();
        }
        cancelConnectedThread();

        // start thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // cancel thread that completed the connection
        cancelConnectThread();
        // cancel any thread running a connection
        cancelConnectedThread();

        // show that connection has been made
        sendMessage(ConnectionFragment.CONNECTION_STATUS, ConnectionFragment.CONN_SUCCESSFUL);

        // start thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        cancelConnectThread();
        cancelConnectedThread();
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        r.write(out);
    }

    private void connectionFailed() {
        sendMessage(ConnectionFragment.CONNECTION_STATUS, ConnectionFragment.CONN_FAILED);

        setState(STATE_NONE);
    }

    private void connectionLost() {
        sendMessage(ConnectionFragment.CONNECTION_STATUS, ConnectionFragment.CONN_LOST);

        setState(STATE_NONE);
    }

    private void sendMessage(String name, String message) {
        Intent intent = new Intent("BTConnectionEvent");
        intent.putExtra(name, message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(Q_UUID);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();

            // make a connection to the BluetoothSocket
            try {
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());

                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "Unable to close socket:: " + e.getMessage());
                }

                connectionFailed();
                return;

                // BluetoothService.this.start();
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Connect:: " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // get BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // keep listening to the input stream while connected
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
