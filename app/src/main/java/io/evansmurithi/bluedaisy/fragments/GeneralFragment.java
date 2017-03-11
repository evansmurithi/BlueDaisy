package io.evansmurithi.bluedaisy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.evansmurithi.bluedaisy.R;
import io.evansmurithi.bluedaisy.managers.BDApplication;
import io.evansmurithi.bluedaisy.managers.BluetoothService;
import io.evansmurithi.bluedaisy.utils.BDCommand;

/**
 * Created by evans on 3/7/17.
 *
 * GeneralFragment class.
 */

public class GeneralFragment extends Fragment {

    private static final String TAG = "GeneralFragment";

    private BluetoothService mBluetoothService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBluetoothService = ((BDApplication) getActivity().getApplication()).getBluetoothService();
    }

    @OnClick({
            R.id.lockButton, R.id.brightnessDownButton, R.id.brightnessUpButton,
            R.id.wifiOffButton, R.id.wifiOnButton
    })
    public void action(ImageButton button) {
        switch (button.getId()) {
            case R.id.lockButton:
                mBluetoothService.write(BDCommand.getCommand("general", "lock"));
                break;
            case R.id.brightnessDownButton:
                mBluetoothService.write(BDCommand.getCommand("general", "brightness_down"));
                break;
            case R.id.brightnessUpButton:
                mBluetoothService.write(BDCommand.getCommand("general", "brightness_up"));
                break;
            case R.id.wifiOffButton:
                mBluetoothService.write(BDCommand.getCommand("general", "wifi_off"));
                break;
            case R.id.wifiOnButton:
                mBluetoothService.write(BDCommand.getCommand("general", "wifi_on"));
                break;
        }
    }
}
