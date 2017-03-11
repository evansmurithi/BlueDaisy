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
 * PresentationFragment class.
 */

public class PresentationFragment extends Fragment {

    private static final String TAG = "GeneralFragment";

    private BluetoothService mBluetoothService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_presentation, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBluetoothService = ((BDApplication) getActivity().getApplication()).getBluetoothService();
    }

    @OnClick({
            R.id.previousButton, R.id.nextButton, R.id.startButton
    })
    public void action(ImageButton button) {
        switch (button.getId()) {
            case R.id.previousButton:
                mBluetoothService.write(BDCommand.getCommand("presentation", "previous"));
                break;
            case R.id.nextButton:
                mBluetoothService.write(BDCommand.getCommand("presentation", "next"));
                break;
            case R.id.startButton:
                mBluetoothService.write(BDCommand.getCommand("presentation", "start"));
                break;
        }
    }
}
