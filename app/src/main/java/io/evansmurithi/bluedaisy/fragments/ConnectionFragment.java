package io.evansmurithi.bluedaisy.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.evansmurithi.bluedaisy.R;
import io.evansmurithi.bluedaisy.managers.BDApplication;
import io.evansmurithi.bluedaisy.managers.BluetoothService;

/**
 * Created by evans on 3/5/17.
 *
 * ConnectionFragment class.
 */

public class ConnectionFragment extends Fragment {

    @BindView(R.id.connectingAvi)
    AVLoadingIndicatorView connectingAvi;
    @BindView(R.id.retryButton)
    Button retryButton;
    @BindView(R.id.connectionStatusText)
    TextView connectionStatusText;
    @BindView(R.id.connectionStatusBar)
    View connectionStatusBar;

    @BindString(R.string.connecting_status_text)
    String connectingStatus;
    @BindString(R.string.connection_failed_status_text)
    String connectionFailedStatus;
    @BindString(R.string.connection_lost_status_text)
    String connectionLostStatus;

    @BindColor(R.color.colorSuccess)
    int successColor;
    @BindColor(R.color.colorDanger)
    int dangerColor;
    @BindColor(R.color.colorAccent)
    int accentColor;

    private static final String TAG = "ConnectionFragment";

    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";

    public static final String CONNECTION_STATUS = "connection_status";

    public static final String CONN_SUCCESSFUL = "success";
    public static final String CONN_FAILED = "failed";
    public static final String CONN_LOST = "lost";

    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothService mBluetoothService = null;
    private BluetoothDevice mBluetoothDevice;

    private OnDeviceConnectedListener mListener;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(CONNECTION_STATUS);

            Log.d(TAG, "Message: " + message);

            if (CONN_SUCCESSFUL.equals(message)) {
                connectingAvi.smoothToHide();
                connectionStatusBar.setBackgroundColor(successColor);

                Map<String, String> map = new HashMap<>();
                map.put(DEVICE_NAME, mDeviceName);
                map.put(DEVICE_ADDRESS, mDeviceAddress);
                mListener.onDeviceConnected(map);
            } else if (CONN_FAILED.equals(message)) {
                connectingAvi.smoothToHide();
                connectionStatusText.setText(connectionFailedStatus);
                connectionStatusBar.setBackgroundColor(dangerColor);
                retryButton.setVisibility(View.VISIBLE);
            } else if (CONN_LOST.equals(message)) {
                connectingAvi.smoothToHide();
                connectionStatusText.setText(connectionLostStatus);
                connectionStatusBar.setBackgroundColor(dangerColor);
                retryButton.setVisibility(View.VISIBLE);
            }
        }
    };

    public interface OnDeviceConnectedListener {
        void onDeviceConnected(Map<String, String> map);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mDeviceName = savedInstanceState.getString(DEVICE_NAME);
            mDeviceAddress = savedInstanceState.getString(DEVICE_ADDRESS);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            mDeviceName = args.getString(DEVICE_NAME);
            mDeviceAddress = args.getString(DEVICE_ADDRESS);
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mDeviceName);
        }

        connectingAvi.smoothToShow();
        connectionStatusText.setText(connectingStatus);

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothService = new BluetoothService(getActivity());
        ((BDApplication) getActivity().getApplication()).setBluetoothService(mBluetoothService);
        mBluetoothService.connect(mBluetoothDevice);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mReceiver, new IntentFilter("BTConnectionEvent"));

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mListener = (OnDeviceConnectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDeviceConnectedListener");
        }
    }

    @OnClick(R.id.retryButton)
    public void retry() {
        mBluetoothService.connect(mBluetoothDevice);
        retryButton.setVisibility(View.GONE);
        connectionStatusText.setText(connectingStatus);
        connectionStatusBar.setBackgroundColor(accentColor);
        connectingAvi.smoothToShow();
    }
}
