package io.evansmurithi.bluedaisy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.evansmurithi.bluedaisy.R;
import io.evansmurithi.bluedaisy.managers.BDApplication;
import io.evansmurithi.bluedaisy.managers.BluetoothService;
import io.evansmurithi.bluedaisy.utils.BDCommand;

/**
 * Created by evans on 3/7/17.
 *
 * MediaFragment class.
 */

public class MediaFragment extends Fragment {

    private static final String TAG = "GeneralFragment";

    private BluetoothService mBluetoothService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBluetoothService = ((BDApplication) getActivity().getApplication()).getBluetoothService();
    }

    @OnClick({
            R.id.volumeUpButton, R.id.volumeDownButton, R.id.rewindButton, R.id.forwardButton,
            R.id.pauseButton, R.id.muteButton, R.id.unmuteButton
    })
    public void action(ImageButton button) {
        switch (button.getId()) {
            case R.id.volumeUpButton:
                mBluetoothService.write(BDCommand.getCommand("media", "volume_up"));
                break;
            case R.id.volumeDownButton:
                mBluetoothService.write(BDCommand.getCommand("media", "volume_down"));
                break;
            case R.id.rewindButton:
                mBluetoothService.write(BDCommand.getCommand("media", "previous"));
                break;
            case R.id.forwardButton:
                mBluetoothService.write(BDCommand.getCommand("media", "next"));
                break;
            case R.id.pauseButton:
                mBluetoothService.write((BDCommand.getCommand("media", "pause")));
                break;
            case R.id.muteButton:
                mBluetoothService.write(BDCommand.getCommand("media", "mute"));
                break;
            case R.id.unmuteButton:
                mBluetoothService.write(BDCommand.getCommand("media", "unmute"));
                break;
        }
    }
}
