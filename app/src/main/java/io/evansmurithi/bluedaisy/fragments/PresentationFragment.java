package io.evansmurithi.bluedaisy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.evansmurithi.bluedaisy.R;

/**
 * Created by evans on 3/7/17.
 *
 * PresentationFragment class.
 */

public class PresentationFragment extends Fragment {

    private static final String TAG = "GeneralFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_presentation, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
