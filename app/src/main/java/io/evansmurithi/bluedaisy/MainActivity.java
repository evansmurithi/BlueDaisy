package io.evansmurithi.bluedaisy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.evansmurithi.bluedaisy.fragments.ConnectionFragment;
import io.evansmurithi.bluedaisy.fragments.DevicesFragment;
import io.evansmurithi.bluedaisy.fragments.MenuFragment;

/**
 * Created by evans on 2/28/17.
 *
 * MainActivity class.
 */

public class MainActivity extends AppCompatActivity implements
        DevicesFragment.OnDeviceSelectedListener, ConnectionFragment.OnDeviceConnectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragmentContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }

            DevicesFragment devicesFragment = new DevicesFragment();
            devicesFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, devicesFragment).commit();
        }
    }

    @Override
    public void onDeviceSelected(Map<String, String> map) {
        ConnectionFragment connectionFragment = new ConnectionFragment();

        Bundle args = new Bundle();
        args.putString(ConnectionFragment.DEVICE_NAME, map.get("device_name"));
        args.putString(ConnectionFragment.DEVICE_ADDRESS, map.get("device_address"));
        connectionFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, connectionFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onDeviceConnected(Map<String, String> map) {
        MenuFragment menuFragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putString(ConnectionFragment.DEVICE_NAME, map.get("device_name"));
        args.putString(ConnectionFragment.DEVICE_ADDRESS, map.get("device_address"));
        menuFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, menuFragment)
                .addToBackStack(null).commit();
    }
}
