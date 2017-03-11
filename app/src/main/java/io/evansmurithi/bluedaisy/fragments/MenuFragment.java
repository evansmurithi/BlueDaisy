package io.evansmurithi.bluedaisy.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.evansmurithi.bluedaisy.R;

/**
 * Created by evans on 3/7/17.
 *
 * MenuFragment class.
 */

public class MenuFragment extends Fragment implements OnTabSelectListener {

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    private static final String TAG = "MenuFragment";

    private String mDeviceName;
    private String mDeviceAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mDeviceName = savedInstanceState.getString(ConnectionFragment.DEVICE_NAME);
            mDeviceAddress = savedInstanceState.getString(ConnectionFragment.DEVICE_ADDRESS);
        }

        bottomBar.setOnTabSelectListener(this);

        return rootView;
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
            case R.id.generalTab:
                Log.d(TAG, "General tab selected");
                final GeneralFragment generalFragment = new GeneralFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.menuFragmentContainer, generalFragment).commit();
                break;
            case R.id.mediaTab:
                Log.d(TAG, "Media tab selected");
                final MediaFragment mediaFragment = new MediaFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.menuFragmentContainer, mediaFragment).commit();
                break;
            case R.id.presentationTab:
                Log.d(TAG, "Presentation tab selected");
                final PresentationFragment presentationFragment = new PresentationFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.menuFragmentContainer, presentationFragment).commit();
                break;
            default:
                break;
        }
    }
}
