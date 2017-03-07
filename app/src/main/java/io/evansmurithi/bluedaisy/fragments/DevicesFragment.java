package io.evansmurithi.bluedaisy.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.evansmurithi.bluedaisy.R;
import io.evansmurithi.bluedaisy.views.adapters.DeviceAdapter;

/**
 * Created by evans on 3/4/17.
 *
 * DevicesFragment class.
 */

public class DevicesFragment extends Fragment {

    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;

    private static final String TAG = "DevicesFragment";

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private List<Map<String, String>> mDiscoveredDevicesList;
    private DeviceAdapter mDeviceAdapter;
    private OnDeviceSelectedListener mListener;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addDevice(btDevice);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Discovery finished");
            }
        }
    };

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(Map<String, String> map);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Sorry. Your device does not support bluetooth.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, rootView);

        // Set layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        devicesRecyclerView.setLayoutManager(layoutManager);

        // Set adapter
        mDiscoveredDevicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter(mDiscoveredDevicesList);
        mDeviceAdapter.setCallback(new DeviceAdapter.Callback() {
            @Override
            public void onItemClicked(int index) {
                Map<String, String> map = mDiscoveredDevicesList.get(index);
                mListener.onDeviceSelected(map);
            }
        });
        devicesRecyclerView.setAdapter(mDeviceAdapter);
        bluetoothSetup();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // request to enable bluetooth granted
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(), "Bluetooth successfully enabled.", Toast.LENGTH_SHORT).show();
                    getPairedDevices();
                } else {
                    Toast.makeText(getActivity(), "Bluetooth needs to be enabled in order for the app to work.", Toast.LENGTH_LONG).show();
                }

            default:
                // handle this
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mListener = (OnDeviceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDeviceSelectedListener");
        }
    }

    private void bluetoothSetup() {
        // if bluetooth is not enabled, make request for it to be enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(getActivity(), "Bluetooth is enabled.", Toast.LENGTH_SHORT).show();
            getPairedDevices();
        }
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice btDevice : pairedDevices) {
                Log.d(TAG, "Device " + btDevice.getName());
                addDevice(btDevice);
            }
        }
    }

    private void addDevice(BluetoothDevice btDevice) {
        if (btDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER) {
            Map<String, String> map = new HashMap<>();
            map.put("device_name", btDevice.getName());
            map.put("device_address", btDevice.getAddress());

            mDeviceAdapter.add(0, map);
            devicesRecyclerView.scrollToPosition(0);
        }
    }
}
